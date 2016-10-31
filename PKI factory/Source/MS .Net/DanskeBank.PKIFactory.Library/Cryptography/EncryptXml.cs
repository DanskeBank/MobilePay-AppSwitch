using System;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Xml;
using System.Linq;

namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public interface IEncryptXml
    {
        void EncryptDataContainer(XmlDocument xmlDoc);
    }

    internal class EncryptXml : IEncryptXml
    {
        private readonly IPKIClient _client;

        public EncryptXml(IPKIClient client)
        {
            _client = client;
        }
        public void EncryptDataContainer(XmlDocument xmlDoc)
        {
            #region Find tag to encrypt
            string[] encryptTheseTags = new[] { "CreateCertificateRequest", "RenewCertificateRequest" };
            XmlNamespaceManager xmlns = new XmlNamespaceManager(xmlDoc.NameTable);
            var requestElements = xmlDoc.SelectNodes("//*['Request' = substring(name(), string-length(name()) - string-length('Request') + 1)]", xmlns);

            if (requestElements.Count > 1)
                throw new InvalidOperationException("Cannot continue with request, since multiple elements that would have been crypted exists");

            XmlElement elementToEncrypt = null;
            foreach (XmlElement elem in requestElements)
            {
                if (!encryptTheseTags.Contains(elem.LocalName))
                    continue; // Skip this element

                if (elementToEncrypt != null)
                    throw new Exception("Cannot encrypt multiple nodes");
                elementToEncrypt = elem;

            }

            if (elementToEncrypt == null)
                return;

            #endregion

            var cert = _client.BankEncryptionCertificate;
            if (cert == null)
                throw new InvalidOperationException("Cannot continue with request, since the bank encryption certificate is not set");

            // Encrypt and replace body
            EncryptedData edElement = EncryptElement(cert, elementToEncrypt);
            EncryptedXml.ReplaceElement(elementToEncrypt, edElement, false);

            // Move X509 element to correct place.
            var x509element = xmlDoc.GetElementsByTagName("X509Data")[0];
            var encryptedKey = xmlDoc.GetElementsByTagName("KeyInfo");
            for (int i = 0; i < encryptedKey.Count; i++)
            {
                if (encryptedKey[i].ParentNode.Name.Equals("EncryptedKey"))
                    encryptedKey[i].AppendChild(x509element);
            }

            // Make sure the correct Xenc namespace is set (or else DataPower will reject the message)
            SetEncryptionXencNamespace(xmlDoc);
        }

        private EncryptedData EncryptElement(X509Certificate2 cert, XmlElement elementToEncrypt)
        {
            /*
             * This constructs a symmetric key with which the data is encrypted
             * This encryption key is then encrypted itself under the public key of the recipient
             * The encrypted symmetric key is then attached to the document
             */

            // Create a new 3des instance (This automaticly creates a non-weak 3des key)
            var tripleDesAlgo = TripleDES.Create();


            EncryptedXml xmlCryptoUtil = new EncryptedXml();
            // Create a new EncryptedData element for the xml document
            EncryptedData edElement = new EncryptedData();

            // Set type and crypto method (So recipient will know how to decrypt it)
            edElement.Type = EncryptedXml.XmlEncElementContentUrl;
            edElement.EncryptionMethod = new EncryptionMethod(EncryptedXml.XmlEncTripleDESUrl);

            // Encrypt the element
            byte[] encryptedElement = xmlCryptoUtil.EncryptData(elementToEncrypt, tripleDesAlgo, false);

            // Add the encrypted element data to the
            // EncryptedData object.
            edElement.CipherData.CipherValue = encryptedElement;

            #region Encryption of symmetric key
            EncryptedKey ek = new EncryptedKey();

            // Specify key encryption method and encrypt under recipient public key
            ek.EncryptionMethod = new EncryptionMethod(EncryptedXml.XmlEncRSA15Url);
            byte[] encryptedKey = EncryptedXml.EncryptKey(tripleDesAlgo.Key, (RSACryptoServiceProvider)cert.PublicKey.Key, false);
            ek.CipherData = new CipherData(encryptedKey);

            KeyInfoName kin = new KeyInfoName();
            ek.KeyInfo.AddClause(kin);

            // Add a new KeyInfo element to the encrypted data element.
            edElement.KeyInfo = new KeyInfo();
            edElement.KeyInfo.AddClause(new KeyInfoEncryptedKey(ek));
            edElement.KeyInfo.AddClause(new KeyInfoX509Data(cert));

            #endregion

            return edElement;
        }

        private void SetEncryptionXencNamespace(XmlDocument xmlDoc)
        {
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(xmlDoc.NameTable);
            nsmgr.AddNamespace("xenc", "http://www.w3.org/2001/04/xmlenc#");

            XmlElement encryptedDataElement = xmlDoc.GetElementsByTagName("EncryptedData", "http://www.w3.org/2001/04/xmlenc#")[0] as XmlElement;
            encryptedDataElement.Prefix = "xenc";
            encryptedDataElement.GetElementsByTagName("EncryptionMethod")[0].Prefix = "xenc";
            XmlElement cipherDataElement = encryptedDataElement.SelectNodes("./xenc:CipherData", nsmgr)[0] as XmlElement;
            cipherDataElement.Prefix = "xenc";
            cipherDataElement.GetElementsByTagName("CipherValue")[0].Prefix = "xenc";
        }
    }
}
