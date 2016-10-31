using DanskeBank.PKIFactory.Library.Cryptography;
using DanskeBank.PKIFactory.Library.SoapService;
using System.Security.Cryptography.X509Certificates;
using DanskeBank.PKIFactory.Library.Cryptography.Signing;

namespace DanskeBank.PKIFactory.Library
{
    /// <summary>
    /// Specifies the public interface for communicating with the PKI factory service provided by the bank
    /// </summary>
    public interface IPKIClient
    {
        /// <summary>
        /// Sets the bank certificates to use for communicatiom.
        /// </summary>
        /// <param name="cryptoCert">Certificate to use for decrypting responses from the bank.</param>
        /// <param name="signingCert">Certificate to use for validating the signature on responses.</param>
        void SetBankCertificates(X509Certificate2 cryptoCert, X509Certificate2 signingCert);

        /// <summary>
        /// Currently used bank root certificate.
        /// </summary>
        X509Certificate2 BankRootCertificate { get; set; }

        /// <summary>
        /// The current bank encryption certificate. Used for decrypting responses from the bank.
        /// </summary>
        X509Certificate2 BankEncryptionCertificate { get; set; }

        /// <summary>
        /// The current bank signing certificate. Used for validating the signature on responses from bank.
        /// </summary>
        X509Certificate2 BankSigningCertificate { get; set; }

        /// <summary>
        /// Service for signing and encrypting requests.
        /// </summary>
        IXmlSigningService XmlSigningService { get; set; }

        #region Backend operations

        /// <summary>
        /// Retrieves signing, encryption and updated root certificate given an initial certificate serial number.
        /// </summary>
        /// <returns></returns>
        WrappedResponse<GetBankCertificateOutType> GetBankCertificate();

        /// <summary>
        /// Requests signing of client certificates which can then be used for further comminications.
        /// Note: Only the certificate, and not the private key, is transmitted to the bank.
        /// </summary>
        /// <param name="signingCert">The desired signing certificate</param>
        /// <param name="cryptoCert">The desired encryption certificate</param>
        /// <param name="pin">PIN code to authenticate the signing.</param>
        /// <param name="generatorType">Source of the certificates</param>
        /// <returns></returns>
        WrappedResponse<CreateCertificateOutType> CreateCertificate(X509Certificate2 signingCert, X509Certificate2 cryptoCert, string pin, KeyGeneratorTypeType generatorType);

        /// <summary>
        /// Requests renewal of previously signed certificates
        /// </summary>
        /// <param name="signingCert">Signing certificate to be renewed</param>
        /// <param name="cryptoCert">Encryption certificate to be renewed</param>
        /// <param name="generatorType">Source of the certificates</param>
        /// <returns></returns>
        WrappedResponse<RenewCertificateOutType> RenewCertificate(X509Certificate2 signingCert, X509Certificate2 cryptoCert, KeyGeneratorTypeType generatorType);

        /// <summary>
        /// Retrives a list of all certificates which the bank will accept on behalf of the client.
        /// This also includes both expired and revoked certificates.
        /// </summary>
        /// <param name="generatorType">Generation source we want to get certificates for</param>
        /// <returns></returns>
        WrappedResponse<GetOwnCertificateListOutType> GetClientCertificateList(KeyGeneratorTypeType generatorType);

        /// <summary>
        /// Queries the bank to determine the status of the given certificate serial numbers
        /// </summary>
        /// <param name="serialNumbers">Serial numbers in decimal notation</param>
        /// <param name="generatorType">Only match certificates with this key generation type</param>
        /// <returns></returns>
        WrappedResponse<CertificateStatusOutType> CertificateStatus(string[] serialNumbers, KeyGeneratorTypeType generatorType);

        /// <summary>
        /// Revokes all certificates which were generated using a specified method
        /// </summary>
        /// <param name="keyTypeToRevoke">Certificates of generated this way will be revoked</param>
        /// <param name="exceptTheseSerialNumbers">Serial numbers of certificates which should not be revoked</param>
        /// <param name="reason"></param>
        /// <returns></returns>
        WrappedResponse<RevokeCertificateOutType> RevokeAllCertificates(KeyGeneratorTypeType keyTypeToRevoke, string[] exceptTheseSerialNumbers = null, CRLReasonType? reason = null);

        /// <summary>
        /// Revokes a given list of certificates.
        /// Note: Certificates are revoked in pairs, so if only an encryption certificate is specified its corrosponding signing certificate will also be revoked
        /// </summary>
        /// <param name="keyTypeToRevoke"></param>
        /// <param name="serialNumbers"></param>
        /// <param name="reason"></param>
        /// <returns></returns>
        WrappedResponse<RevokeCertificateOutType> RevokeCertificates(KeyGeneratorTypeType keyTypeToRevoke, string[] serialNumbers, CRLReasonType? reason = null);

        #endregion
    }
}
