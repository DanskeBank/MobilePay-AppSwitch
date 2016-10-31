using DanskeBank.PKIFactory.Library.SoapService;
using System.IO;
using System.ServiceModel.Dispatcher;
using System.Xml;
using System.Xml.Serialization;

namespace DanskeBank.PKIFactory.Library.FaultCompensation
{
    /// <summary>
    /// Intercepts responses and looks for PKIFactoryServiceFault nodes
    /// If one is found a PKIFactoryFaultException with the contents is raised
    /// </summary>
    public class SoapFaultInspector : IClientMessageInspector
    {
        public void AfterReceiveReply(ref System.ServiceModel.Channels.Message reply, object correlationState)
        {
            // Read reply
            string responseXml = reply.ToString();

            // Contruct an XML document (Assumes response was well-formatted)
            XmlDocument doc = new XmlDocument();
            doc.LoadXml(responseXml);

            // Construct namespace manager for expected output
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(doc.NameTable);
            nsmgr.AddNamespace("env", "http://schemas.xmlsoap.org/soap/envelope/");
            nsmgr.AddNamespace("pkif", "http://danskebank.dk/PKI/PKIFactoryService");

            // Search for a fault node
            XmlNode faultNode = doc.SelectSingleNode("/env:Envelope/env:Body/pkif:PKIFactoryServiceFault", nsmgr);
            if (faultNode != null)
            {
                // A fault node was found in body => throw an exception
                PKIFactoryServiceFaultDetailType faultDetails = ConvertNode<PKIFactoryServiceFaultDetailType>(faultNode);
                throw new PKIFactoryFaultException(faultDetails);
            }
        }

        public object BeforeSendRequest(ref System.ServiceModel.Channels.Message request, System.ServiceModel.IClientChannel channel)
        {
            return request;
        }


        private static T ConvertNode<T>(XmlNode node) where T : class
        {
            using (MemoryStream ms = new MemoryStream())
            {
                using (StreamWriter stw = new StreamWriter(ms))
                {
                    // Cleanup the result so it can be auto-deserialzed.
                    string str = node.OuterXml.Replace("pkif:PKIFactoryServiceFault", "PKIFactoryServiceFault")
                        .Replace("pkif:", "")
                        .Replace("xmlns:pkif=\"http://danskebank.dk/PKI/PKIFactoryService\"", "xmlns=\"http://danskebank.dk/PKI/PKIFactoryService\"")
                        .Replace("<PKIFactoryServiceFault", "<PKIFactoryServiceFaultDetailType")
                        .Replace("</PKIFactoryServiceFault>", "</PKIFactoryServiceFaultDetailType>");

                    // Write "processed" result to memory stream 
                    stw.Write(str);
                    stw.Flush();
                    ms.Position = 0;

                    // Construct a proxied reader for the new stream
                    XmlSoapProxyReader reader = new XmlSoapProxyReader(ms);
                    reader.ProxyNamespace = "http://danskebank.dk/PKI/PKIFactoryService";
                    reader.ProxyType = typeof(PKIFactoryServiceFaultDetailType);

                    // Deserialize
                    var serializer = new XmlSerializer(typeof(T));
                    T result = (T)serializer.Deserialize(reader);

                    return result;
                }
            }
        }
    }
}
