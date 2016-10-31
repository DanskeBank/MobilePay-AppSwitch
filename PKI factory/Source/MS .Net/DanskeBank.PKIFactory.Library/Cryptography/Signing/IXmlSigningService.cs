using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography.Signing
{
    public interface IXmlSigningService
    {
        /// <summary>
        /// The bank root certificate, used to validate bank signing certificates in responses. 
        /// I.e. signing certificates will only be accepted if they are issued by this certificate.
        /// </summary>
        X509Certificate2 BankRootCertificate { get; set; }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="signingCert"></param>
        /// <param name="privateKey"></param>
        void SetClientCertificate(X509Certificate2 signingCert, RSACryptoServiceProvider privateKey);

        /// <summary>
        /// Adds an enveloped signature to the request element of the document.
        /// </summary>
        /// <param name="xmlDoc"></param>
        void AddSignature(XmlDocument xmlDoc);

        /// <summary>
        /// Checks that the signature is valid.
        /// </summary>
        /// <param name="xmlDoc"></param>
        void ValidateSignature(XmlDocument xmlDoc);
    }
}
