using System.Configuration;
using System.IO;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.Text;
using System.Xml;
using DB.SoapLibrary.Configuration.SoapEnvelope;
using DB.SoapLibrary.WSSecurity;
using DB.SoapLibrary.Xml;

namespace DB.SoapLibrary.WCF
{
    public class ClientMessageInspector : IClientMessageInspector
    {
        private readonly IEncryptXml _encryptXml;
        private readonly ISignXml _signXml;
        private readonly SoapEnvelopesSection _soapEnvelopeConfiguration;

        public string LastRequestXML { get; private set; }
        public string LastResponseXML { get; private set; }

        public ClientMessageInspector(IEncryptXml encryptXml,
            ISignXml signXml)
        {
            _encryptXml = encryptXml;
            _signXml = signXml;
            _soapEnvelopeConfiguration = (SoapEnvelopesSection)ConfigurationManager.GetSection("SoapEnvelopes");
        }

        public void AfterReceiveReply(ref Message reply, object correlationState)
        {
            // Get xml without pretty printing and preserving whitespaces
            MemoryStream ms = new MemoryStream();
            XmlWriter writer = XmlWriter.Create(ms);
            reply.WriteMessage(writer);
            writer.Flush();
            ms.Position = 0;
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.PreserveWhitespace = true;
            xmlDoc.Load(ms);

            // Decrypt message
            if (_encryptXml.DecryptBody(xmlDoc))
                _signXml.ValidateSignature(xmlDoc); // Validate message signature


            // Remove node which does not adhere to SOAP specifications when dealing with faults
            XmlNamespaceManager autNS = new XmlNamespaceManager(xmlDoc.NameTable);
            autNS.AddNamespace("aut", "http://danskebank.dk/AGENA/SecSSGW/AuthenticateService");
            autNS.AddNamespace("soap", "http://www.w3.org/2003/05/soap-envelope");
            var faultNode = xmlDoc.SelectSingleNode("//soap:Body/soap:Fault", autNS);
            if (faultNode != null && faultNode.NextSibling != null && faultNode.NextSibling.Name == "aut:Encryption")
                faultNode.ParentNode.RemoveChild(faultNode.NextSibling);

            var reader = XmlReader.Create(new StringReader(xmlDoc.OuterXml));

            // Create deserialized message to return to client.
            reply = Message.CreateMessage(reader, int.MaxValue, reply.Version);
        }

        public object BeforeSendRequest(ref Message request, System.ServiceModel.IClientChannel channel)
        {
            LastRequestXML = request.ToString();

            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(LastRequestXML);

            XmlHelpers.AddNamespacesToEnvelope(xmlDoc);
            XmlHelpers.AddNamespaceToBody(xmlDoc);
            XmlHelpers.AddTimestamp(xmlDoc);
            string id1 = XmlHelpers.AddIDToElement(xmlDoc, "Body", "http://www.w3.org/2003/05/soap-envelope", "DB-1");
            XmlHelpers.ReplaceIdWithNamespaceId(xmlDoc);
            _signXml.AddSignature(xmlDoc, "#" + id1);

            _encryptXml.EncryptBody(xmlDoc);

            string id2 = XmlHelpers.AddIDToElement(xmlDoc, "EncryptedData", "http://www.w3.org/2001/04/xmlenc#", "DB-2");
            _signXml.AddSignature(xmlDoc, "#" + id2);

            using (StringWriter stringWriter = new StringWriter())
            using (var xmlTextWriter = XmlWriter.Create(stringWriter))
            {
                xmlDoc.WriteTo(xmlTextWriter);
                xmlTextWriter.Flush();
                var result = stringWriter.GetStringBuilder().ToString();

                request = ChangeString(request, result);
                return request;
            }
        }

        public Message ChangeString(Message oldMessage, string newBody)
        {
            MemoryStream ms = new MemoryStream(Encoding.Unicode.GetBytes(newBody));
            XmlDictionaryReader xdr = XmlDictionaryReader.CreateTextReader(ms, new XmlDictionaryReaderQuotas());
            Message newMessage = Message.CreateMessage(xdr, int.MaxValue, oldMessage.Version);
            newMessage.Properties.CopyProperties(oldMessage.Properties);
            return newMessage;
        }
    }
}