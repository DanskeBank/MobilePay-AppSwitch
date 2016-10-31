using System.IO;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.Text;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public class SignAndCryptRequestInspector : IClientMessageInspector
    {
        private readonly IEncryptXml _encryptXml;

        private readonly IPKIClient _client;

        public SignAndCryptRequestInspector(IEncryptXml encryptXml, IPKIClient client)
        {
            _encryptXml = encryptXml;
            _client = client;
        }

        public void AfterReceiveReply(ref Message reply, object correlationState)
        {
        }

        public object BeforeSendRequest(ref Message request, System.ServiceModel.IClientChannel channel)
        {
            /*
             * 1. Start by matching the requests we're interested in (e.g. the ones where the SOAP body contains a Request-node)
             * 2. Extract this node
             * 3. Encrypt this node
             * 4. Replace the node with the corrosponding EncryptedData node
             * */
            XmlDocument xmlDoc = new XmlDocument();
            xmlDoc.LoadXml(request.ToString());

            // Sign the request
            _client.XmlSigningService.AddSignature(xmlDoc);

            // Encrypt the request
            _encryptXml.EncryptDataContainer(xmlDoc);

            // Replace the message in the pipeline
            using (StringWriter stringWriter = new StringWriter())
            {
                using (var xmlTextWriter = XmlWriter.Create(stringWriter))
                {
                    // Write XML doc with changed node to writer
                    xmlDoc.WriteTo(xmlTextWriter);
                    xmlTextWriter.Flush();

                    // Convert to string
                    var result = stringWriter.GetStringBuilder().ToString();

                    // Perform replacement
                    request = ChangeString(request, result);
                    return request;
                }
            }
        }

        private Message ChangeString(Message oldMessage, string newBody)
        {
            MemoryStream ms = new MemoryStream(Encoding.Unicode.GetBytes(newBody));
            XmlDictionaryReader xdr = XmlDictionaryReader.CreateTextReader(ms, new XmlDictionaryReaderQuotas());
            Message newMessage = Message.CreateMessage(xdr, int.MaxValue, oldMessage.Version);
            newMessage.Properties.CopyProperties(oldMessage.Properties);
            return newMessage;
        }
    }
}
