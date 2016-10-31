using System.IO;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public class SignatureValidationInspector : IClientMessageInspector
    {
        private readonly IPKIClient _client;

        public SignatureValidationInspector(IPKIClient client)
        {
            _client = client;
        }

        public void AfterReceiveReply(ref Message reply, object correlationState)
        {
            System.Security.Cryptography.CryptoConfig.AddAlgorithm(typeof(MyXmlDsigExcC14NTransform), "http://www.w3.org/2001/10/xml-exc-c14n#");

            XmlDocument xmlDoc = new XmlDocument();

            // Read received response into an XML Document we can work with
            using (MemoryStream ms = new MemoryStream())
            {
                using (XmlWriter writer = XmlWriter.Create(ms))
                {
                    reply.WriteMessage(writer);
                    writer.Flush();
                }

                ms.Position = 0;
                xmlDoc.PreserveWhitespace = true;
                xmlDoc.Load(ms);
            }

            // "Unread" the message by creating a new identical message
            var reader = XmlReader.Create(new StringReader(xmlDoc.OuterXml));
            reply = Message.CreateMessage(reader, int.MaxValue, reply.Version);

            // Validate the signature on the response
            // (Note: This method also checks if the response should be signed)
            _client.XmlSigningService.ValidateSignature(xmlDoc);
        }

        public object BeforeSendRequest(ref Message request, System.ServiceModel.IClientChannel channel)
        {
            return request;
        }
    }

}
