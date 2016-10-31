using System;
using System.IdentityModel;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography.Xml;
using System.Xml;
using DB.SoapLibrary.Configuration;
using DB.SoapLibrary.Configuration.CertificateStore;
using DB.SoapLibrary.Configuration.SoapEnvelope;
using Microsoft.Web.Services3.Security;
using Microsoft.Web.Services3.Security.Tokens;

namespace DB.SoapLibrary.WSSecurity
{
    public interface ISignXml
    {
        void AddSignature(XmlDocument xmlDoc, string elementTag);
        void ValidateSignature(XmlDocument xmlDoc);
    }

    internal class SignXml : ISignXml
    {
        private readonly ICertificateStore _certificateStore;
        private readonly SoapEnvelopesSection _soapEnvelopeConfiguration;

        public SignXml(
            ICertificateStore certificateStore,
            SoapEnvelopesSection soapEnvelopeConfiguration)
        {
            _certificateStore = certificateStore;
            _soapEnvelopeConfiguration = soapEnvelopeConfiguration;
        }

        public void AddSignature(XmlDocument xmlDoc, string elementTag)
        {
            Algorithm algorithm = _soapEnvelopeConfiguration.ApplyElement(Direction.Outgoing, Usage.Signature).Algorithm;
            if (algorithm != Algorithm.RSASHA1)
                throw new InvalidOperationException("Signature could not be created, only RSASHA1 is supported currently");

            X509Certificate2 cert = _certificateStore.ClientSignatureCertificate();

            var signedXml = new SignedXmlWithId(xmlDoc) { SigningKey = cert.PrivateKey };

            // Specify a canonicalization method.
            signedXml.SignedInfo.CanonicalizationMethod = SignedXml.XmlDsigExcC14NTransformUrl;

            // Set the InclusiveNamespacesPrefixList property.        
            XmlDsigExcC14NTransform canMethod = (XmlDsigExcC14NTransform)signedXml.SignedInfo.CanonicalizationMethodObject;
            canMethod.InclusiveNamespacesPrefixList = "oas sec ser s";

            // Create securitytoken from certificate
            SecurityToken securityToken = new X509SecurityToken(cert);

            // Create key info
            KeyInfo keyInfo = new KeyInfo();
            keyInfo.AddClause(new SecurityTokenReference(securityToken, SecurityTokenReference.SerializationOptions.Reference));
            signedXml.KeyInfo = keyInfo;

            // Create a reference to be signed.
            Reference reference = new Reference { Uri = elementTag ?? "" };

            // Add an enveloped transformation to the reference.
            var xmlDsigExcC14NTransform = new XmlDsigExcC14NTransform();
            xmlDsigExcC14NTransform.InclusiveNamespacesPrefixList = "wsu oas sec ser s";
            reference.AddTransform(xmlDsigExcC14NTransform);

            // Add the reference to the SignedXml object.
            signedXml.AddReference(reference);

            // Compute the signature.
            signedXml.ComputeSignature();

            // Get the XML representation of the signature and save 
            // it to an XmlElement object.
            XmlElement xmlDigitalSignature = signedXml.GetXml();

            // Append the element to the XML document.
            XmlElement headerElement = xmlDoc.GetElementsByTagName("Security", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd")[0] as XmlElement;
            headerElement.AppendChild(xmlDoc.ImportNode(securityToken.GetXml(new XmlDocument()), true));
            headerElement.AppendChild(xmlDoc.ImportNode(xmlDigitalSignature, true));

            if (xmlDoc.FirstChild is XmlDeclaration)
            {
                xmlDoc.RemoveChild(xmlDoc.FirstChild);
            }
        }

        public void ValidateSignature(XmlDocument xmlDoc)
        {
            X509Certificate2 cert = _certificateStore.DBSignatureCertificate();
            RSACryptoServiceProvider rsaSignChecker = (RSACryptoServiceProvider)cert.PublicKey.Key;

            SignedXmlWithId signedXml = new SignedXmlWithId(xmlDoc);

            var signatureNodes = xmlDoc.GetElementsByTagName("Signature");
            if (signatureNodes.Count != 1)
                throw new SignatureVerificationFailedException("Response message does not contain a signature");

            var signature = signatureNodes[0] as XmlElement;

            signedXml.LoadXml(signature);

            if (signedXml.CheckSignature(rsaSignChecker) == false)
                throw new SignatureVerificationFailedException("Signature in response message could not be varified");
        }
    }
}
