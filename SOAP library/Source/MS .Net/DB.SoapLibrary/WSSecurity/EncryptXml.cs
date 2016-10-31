using System;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Xml;
using DB.SoapLibrary.Configuration;
using DB.SoapLibrary.Configuration.CertificateStore;
using DB.SoapLibrary.Configuration.SoapEnvelope;

namespace DB.SoapLibrary.WSSecurity
{
    public interface IEncryptXml
    {
        void EncryptBody(XmlDocument xmlDoc);

        bool DecryptBody(XmlDocument xmlDoc);
    }

    internal class EncryptXml : IEncryptXml
    {
        private readonly ICertificateStore _certstore;
        private readonly SoapEnvelopesSection _soapEnvelopeConfiguration;

        public EncryptXml(
            ICertificateStore certstore,
            SoapEnvelopesSection soapEnvelopeConfiguration)
        {
            _certstore = certstore;
            _soapEnvelopeConfiguration = soapEnvelopeConfiguration;
        }

        public bool DecryptBody(XmlDocument xmlDoc)
        {
            Algorithm keyEncryptionAlgorithm = _soapEnvelopeConfiguration.ApplyElement(Direction.Incoming, Usage.KeyEncryption).Algorithm;
            X509Certificate2 cert = _certstore.ClientEncryptionCertificate();
            RSACryptoServiceProvider privateKeyProvider = (RSACryptoServiceProvider)cert.PrivateKey;

            // Get encrypted key
            var encryptedKeyNodes = xmlDoc.GetElementsByTagName("xenc:EncryptedKey");

            if (encryptedKeyNodes.Count == 0)
                return false;

            var encryptedKeyElement = encryptedKeyNodes[0] as XmlElement;
            EncryptedKey encKey = new EncryptedKey();
            encKey.LoadXml(encryptedKeyElement);
            
            // Decrypt key
            bool useOaep;
            encKey.EncryptionMethod = CalculateEncryptedKey(keyEncryptionAlgorithm, out useOaep);
            var decryptedKey = EncryptedXml.DecryptKey(encKey.CipherData.CipherValue, privateKeyProvider, useOaep);

            // Create tripledes key
            var sessionKey = TripleDES.Create();
            sessionKey.Key = decryptedKey;

            // Get encrypted data
            XmlElement encryptedElement = xmlDoc.GetElementsByTagName("xenc:EncryptedData")[0] as XmlElement;
            EncryptedData edElement = new EncryptedData();
            edElement.LoadXml(encryptedElement);

            EncryptedXml exml = new EncryptedXml(xmlDoc);
            var decryptedData = exml.DecryptData(edElement, sessionKey);

            exml.ReplaceData(encryptedElement, decryptedData);
            return true;
        }

        public void EncryptBody(XmlDocument xmlDoc)
        {
            XmlElement elementToEncrypt = xmlDoc.GetElementsByTagName("Body", "http://www.w3.org/2003/05/soap-envelope")[0] as XmlElement;
            elementToEncrypt = elementToEncrypt.FirstChild as XmlElement;
            X509Certificate2 cert = _certstore.DBEncryptionCertificate();

            // Encrypt and replace body
            EncryptedData edElement = EncryptBody(cert, elementToEncrypt);
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

        private EncryptedData EncryptBody(X509Certificate2 cert, XmlElement elementToEncrypt)
        {
            Algorithm keyEncryptionAlgorithm = _soapEnvelopeConfiguration.ApplyElement(Direction.Outgoing, Usage.KeyEncryption).Algorithm;
            Algorithm contentEncryptionAlgorithm = _soapEnvelopeConfiguration.ApplyElement(Direction.Outgoing, Usage.ContentEncryption).Algorithm;

            if (keyEncryptionAlgorithm != Algorithm.XmlEncRSA15Url &&
                keyEncryptionAlgorithm != Algorithm.XmlEncRSAOAEPUrl)
                throw new InvalidOperationException(string.Format("Key encryption: \"{0}\" not supported, at this time only \"XmlEncRSA15Url\" and \"XmlEncRSAOAEPUrl\" are supported", keyEncryptionAlgorithm));

            if (contentEncryptionAlgorithm != Algorithm.XmlEncTripleDESUrl)
                throw new InvalidOperationException(string.Format("Content encryption: \"{0}\" not supported, at this time only \"XmlEncTripleDESUrl\" is supported", contentEncryptionAlgorithm));

            var publicKeyProvider = (RSACryptoServiceProvider)cert.PublicKey.Key;
            var sessionKey = TripleDES.Create();

            EncryptedXml eXml = new EncryptedXml();
            byte[] encryptedElement = eXml.EncryptData(elementToEncrypt, sessionKey, false);
            EncryptedData edElement = new EncryptedData();
            edElement.Type = EncryptedXml.XmlEncElementContentUrl;
            edElement.EncryptionMethod = new EncryptionMethod(EncryptedXml.XmlEncTripleDESUrl);

            // Calculate encrypted key
            EncryptedKey ek = new EncryptedKey();
            bool useOaep;
            ek.EncryptionMethod = CalculateEncryptedKey(keyEncryptionAlgorithm, out useOaep);
            byte[] encryptedKey = EncryptedXml.EncryptKey(sessionKey.Key, publicKeyProvider, useOaep);
            ek.CipherData = new CipherData(encryptedKey);

            // Create a new KeyInfo element.
            edElement.KeyInfo = new KeyInfo();
            edElement.KeyInfo.AddClause(new KeyInfoEncryptedKey(ek));
            edElement.KeyInfo.AddClause(new KeyInfoX509Data(cert));

            // Add the encrypted element data to the
            // EncryptedData object.
            edElement.CipherData.CipherValue = encryptedElement;

            KeyInfoName kin = new KeyInfoName();
            ek.KeyInfo.AddClause(kin);
            
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

        private EncryptionMethod CalculateEncryptedKey(Algorithm keyEncryptionAlgorithm, out bool oaep)
        {
            switch (keyEncryptionAlgorithm)
            {
                case Algorithm.XmlEncRSA15Url:
                    oaep = false;
                    return new EncryptionMethod(EncryptedXml.XmlEncRSA15Url);
                case Algorithm.XmlEncRSAOAEPUrl:
                    oaep = true;
                    return new EncryptionMethod(EncryptedXml.XmlEncRSAOAEPUrl);
                default:
                    throw new Exception("Key encryption algorithm invalid");
            }
        }
    }
}
