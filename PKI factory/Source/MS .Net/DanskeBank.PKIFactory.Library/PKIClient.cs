//#define CUSTEST

using Org.BouncyCastle.Asn1.X509;
using Org.BouncyCastle.Pkcs;
using DanskeBank.PKIFactory.Library.Cryptography;
using DanskeBank.PKIFactory.Library.FaultCompensation;
using DanskeBank.PKIFactory.Library.SoapService;
using System;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using DanskeBank.PKIFactory.Library.Cryptography.Signing;
using Org.BouncyCastle.Math;

namespace DanskeBank.PKIFactory.Library
{
    /// <summary>
    /// Implements the public interface for communicating with the PKI factory service provided by the bank.
    /// </summary>
    public class PKIClient : IPKIClient
    {
        // SOAP proxy
        private readonly PkiServicePortTypeClient _proxy;
        private X509Certificate2 _bankEncryptionCertificate;
        private X509Certificate2 _bankSigningCertificate;

        // This is used to validate the applied signatures on responses
        // All bank signing certificates must be issued _directly_ by this certificate
        // Thus, any otherwise valid signature with > 2 certificates in the chain is not allowed
        private X509Certificate2 _bankRootCertificate;
        private IXmlSigningService _signingStrategy;
        private string _customerId;

        /// <summary>
        /// Constructs client with specified customer id.
        /// </summary>
        /// <param name="customerId"></param>
        public PKIClient(string customerId)
        {
            if (String.IsNullOrEmpty(customerId))
                throw new ArgumentException("Must set customer id", "customerId");

            _customerId = customerId;
            _proxy = new PkiServicePortTypeClient();
            
            // Attach an interceptor to detect misplaced fault elements and present proper errors to the user
            _proxy.Endpoint.Behaviors.Add(new FaultInspectorBehaviour());

            // Add interceptor perform cryptographic operations (encrypt and sign requests, validate signatures on responses)
            _proxy.Endpoint.Behaviors.Add(new CryptographyBehavior(this));

            CryptoConfig.AddAlgorithm(typeof(MyXmlDsigExcC14NTransform), "http://www.w3.org/2001/10/xml-exc-c14n#");
        }

        #region Properties

        /// <summary>
        /// Currently used bank root certificate.
        /// </summary>
        public X509Certificate2 BankRootCertificate
        {
            get { return _bankRootCertificate; }
            set
            {
                if (_signingStrategy != null)
                    _signingStrategy.BankRootCertificate = value;

                _bankRootCertificate = value;
            }
        }

        /// <summary>
        /// The current bank encryption certificate. Used for decrypting responses from the bank.
        /// </summary>
        public X509Certificate2 BankEncryptionCertificate
        {
            get { return _bankEncryptionCertificate; }
            set { _bankEncryptionCertificate = value; }
        }

        /// <summary>
        /// The current bank signing certificate. Used for validating the signature on responses from bank.
        /// </summary>
        public X509Certificate2 BankSigningCertificate
        {
            get { return _bankSigningCertificate; }
            set { _bankSigningCertificate = value; }
        }

        /// <summary>
        /// Service for signing and encrypting requests.
        /// </summary>
        public IXmlSigningService XmlSigningService
        {
            get { return _signingStrategy; }
            set { _signingStrategy = value; }
        }

        #endregion

        #region Getters and setters

        /// <summary>
        /// Sets the bank certificates to use for communicatiom.
        /// </summary>
        /// <param name="cryptoCert">Certificate to use for decrypting responses from the bank.</param>
        /// <param name="signingCert">Certificate to use for validating the signature on responses.</param>
        public void SetBankCertificates(X509Certificate2 cryptoCert, X509Certificate2 signingCert)
        {
            BankEncryptionCertificate = cryptoCert;
            BankSigningCertificate = signingCert;
        }
        
        #endregion

        /// <summary>
        /// Utility function for creating a request header.
        /// </summary>
        /// <returns></returns>
        private RequestHeaderType CreateHeader()
        {
            string reqid = (DateTime.UtcNow.Ticks % 1000000000).ToString(""); // Create a sequential request id which is not longer than 10 characters


            return new RequestHeaderType()
            {
                SenderId = _customerId,
                CustomerId = _customerId,
                RequestId = reqid,
                Timestamp = DateTime.UtcNow,
                InterfaceVersion = "1",
#if CUSTEST
                EnvironmentSpecified = true,
                Environment = EnvironmentType.customertest
#endif
            };
        }

        /// <summary>
        /// Construct a certificate signing request given a certificate.
        /// </summary>
        /// <param name="cert">Certificate for signing</param>
        /// <returns></returns>
        private static Pkcs10CertificationRequest CreateCSR(X509Certificate2 cert)
        {
            //Requested Certificate Name
            X509Name name = new X509Name(cert.Subject);

            var keyPair = Org.BouncyCastle.Security.DotNetUtilities.GetKeyPair(cert.PrivateKey);

            //PKCS#10 Certificate Signing Request
            Pkcs10CertificationRequest csr = new Pkcs10CertificationRequest("SHA1WITHRSA", name, keyPair.Public, null, keyPair.Private);

            return csr;
        }

        private void EnsureSigningStrategyIsSet()
        {
            if (_signingStrategy == null)
                throw new InvalidOperationException("Cannot make this requests as no xml signing strategy has been set");
        }

        /// <summary>
        /// Retrieves signing, encryption and updated root certificate given an initial certificate serial number.
        /// </summary>
        /// <returns></returns>
        public WrappedResponse<GetBankCertificateOutType> GetBankCertificate()
        {
            if (_bankRootCertificate == null)
                throw new InvalidOperationException("Cannot request latest bank certificates. The initial bank root certificate must be set.");

            var req = new GetBankCertificateInType()
            {
                GetBankCertificateRequest = new GetBankCertificateRequest()
                {
                    BankRootCertificateSerialNo = new BigInteger(_bankRootCertificate.SerialNumber, 16).ToString(),
                    Timestamp = DateTime.UtcNow
                },
                RequestHeader = CreateHeader()
            };

            // Duplicate header values
            req.GetBankCertificateRequest.RequestId = req.RequestHeader.RequestId;
            req.GetBankCertificateRequest.Timestamp = req.RequestHeader.Timestamp;

            try
            {
                var res = _proxy.GetBankCertificate(req);
                return new WrappedResponse<GetBankCertificateOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<GetBankCertificateOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Requests signing of client certificates which can then be used for further comminications.
        /// Note: Only the certificate, and not the private key, is transmitted to the bank.
        /// </summary>
        /// <param name="signingCert">The desired signing certificate</param>
        /// <param name="cryptoCert">The desired encryption certificate</param>
        /// <param name="pin">PIN code to authenticate the signing.</param>
        /// <param name="generatorType">Source of the certificates</param>
        /// <returns></returns>
        public WrappedResponse<CreateCertificateOutType> CreateCertificate(X509Certificate2 signingCert, X509Certificate2 cryptoCert, string pin, KeyGeneratorTypeType generatorType)
        {
            if (_bankEncryptionCertificate == null)
                throw new InvalidOperationException("Cannot request renewal of client certificates. The bank encryption certificate must be set.");

            // Create certificate signing requests for the certificates
            var csrSign = CreateCSR(signingCert);
            var csrCrypt = CreateCSR(cryptoCert);

            CreateCertificateInType req = new CreateCertificateInType()
            {
                RequestHeader = CreateHeader(),
                CreateCertificateRequest = new CreateCertificateRequest()
                {
                    EncryptionCertPKCS10 = csrCrypt.GetDerEncoded(),
                    SigningCertPKCS10 = csrSign.GetDerEncoded(),
                    PIN = pin,
                    CustomerId = _customerId,
                    KeyGeneratorType = generatorType
                }
            };

            // Duplicate header values
            req.CreateCertificateRequest.RequestId = req.RequestHeader.RequestId;
            req.CreateCertificateRequest.Timestamp = req.RequestHeader.Timestamp;
            req.CreateCertificateRequest.CustomerId = req.RequestHeader.CustomerId;

            req.CreateCertificateRequest.Environment = req.RequestHeader.Environment;
            req.CreateCertificateRequest.EnvironmentSpecified = req.RequestHeader.EnvironmentSpecified;

            try
            {
                var res = _proxy.CreateCertificate(req);
                return new WrappedResponse<CreateCertificateOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<CreateCertificateOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Requests renewal of previously signed certificates
        /// </summary>
        /// <param name="signingCert">Signing certificate to be renewed</param>
        /// <param name="cryptoCert">Encryption certificate to be renewed</param>
        /// <param name="generatorType">Source of the certificates</param>
        /// <returns></returns>
        public WrappedResponse<RenewCertificateOutType> RenewCertificate(X509Certificate2 signingCert, X509Certificate2 cryptoCert, KeyGeneratorTypeType generatorType)
        {
            EnsureSigningStrategyIsSet();
            if (_bankEncryptionCertificate == null)
                throw new InvalidOperationException("Cannot request renewal of client certificates. The bank encryption certificate must be set.");


            // Create certificate signing requests for the certificates
            var csrSign = CreateCSR(signingCert);
            var csrCrypt = CreateCSR(cryptoCert);

            RenewCertificateInType req = new RenewCertificateInType()
            {
                RequestHeader = CreateHeader(),
                RenewCertificateRequest = new RenewCertificateRequest()
                {
                    CustomerId = _customerId,
                    KeyGeneratorType = generatorType,
                    EncryptionCertPKCS10 = csrCrypt.GetDerEncoded(),
                    SigningCertPKCS10 = csrSign.GetDerEncoded()
                }
            };

            // Duplicate header values
            req.RenewCertificateRequest.RequestId = req.RequestHeader.RequestId;
            req.RenewCertificateRequest.Timestamp = req.RequestHeader.Timestamp;
            req.RenewCertificateRequest.CustomerId = req.RequestHeader.CustomerId;

            req.RenewCertificateRequest.Environment = req.RequestHeader.Environment;
            req.RenewCertificateRequest.EnvironmentSpecified = req.RequestHeader.EnvironmentSpecified;

            try
            {
                var res = _proxy.RenewCertificate(req);
                return new WrappedResponse<RenewCertificateOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<RenewCertificateOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Retrives a list of all certificates which the bank will accept on behalf of the client.
        /// This also includes both expired and revoked certificates.
        /// </summary>
        /// <param name="generatorType">Generation source we want to get certificates for</param>
        /// <returns></returns>
        public WrappedResponse<GetOwnCertificateListOutType> GetClientCertificateList(KeyGeneratorTypeType generatorType)
        {
            EnsureSigningStrategyIsSet();

            var req = new GetOwnCertificateListInType()
            {
                RequestHeader = CreateHeader(),
                GetOwnCertificateListRequest = new GetOwnCertificateListRequest()
                {
                    KeyGeneratorType = generatorType
                }
            };

            req.GetOwnCertificateListRequest.RequestId = req.RequestHeader.RequestId;
            req.GetOwnCertificateListRequest.CustomerId = req.RequestHeader.CustomerId;
            req.GetOwnCertificateListRequest.Timestamp = req.RequestHeader.Timestamp;

            try
            {
                var res = _proxy.GetOwnCertificateList(req);
                return new WrappedResponse<GetOwnCertificateListOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<GetOwnCertificateListOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Queries the bank to determine the status of the given certificate serial numbers
        /// </summary>
        /// <param name="serialNumbers">Serial numbers in decimal notation</param>
        /// <param name="generatorType">Only match certificates with this key generation type</param>
        /// <returns></returns>
        public WrappedResponse<CertificateStatusOutType> CertificateStatus(string[] serialNumbers, KeyGeneratorTypeType generatorType)
        {
            EnsureSigningStrategyIsSet();

            var req = new CertificateStatusInType()
            {
                RequestHeader = CreateHeader(),
                CertificateStatusRequest = new CertificateStatusRequest()
                {
                    CertificateSerialNo = serialNumbers,
                    KeyGeneratorType = generatorType
                }
            };

            req.CertificateStatusRequest.RequestId = req.RequestHeader.RequestId;
            req.CertificateStatusRequest.CustomerId = req.RequestHeader.CustomerId;
            req.CertificateStatusRequest.CustomerId = req.RequestHeader.CustomerId;

            try
            {
                var res = _proxy.CertificateStatus(req);
                return new WrappedResponse<CertificateStatusOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<CertificateStatusOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Revokes all certificates which were generated using a specified method
        /// </summary>
        /// <param name="keyTypeToRevoke">Certificates of generated this way will be revoked</param>
        /// <param name="exceptTheseSerialNumbers">Serial numbers of certificates which should not be revoked</param>
        /// <param name="reason"></param>
        /// <returns></returns>
        public WrappedResponse<RevokeCertificateOutType> RevokeAllCertificates(KeyGeneratorTypeType keyTypeToRevoke, string[] exceptTheseSerialNumbers = null, CRLReasonType? reason = null)
        {
            EnsureSigningStrategyIsSet();

            var req = new RevokeCertificateInType()
            {
                RequestHeader = CreateHeader(),
                RevokeCertificateRequest = new RevokeCertificateRequest()
                {
                    KeyGeneratorType = keyTypeToRevoke,
                    Items = new object[] { 
                        new RevokeCertificateRequestRevokeAll() {
                            ExceptCertificateSerialNo = exceptTheseSerialNumbers ?? new string[0]
                        }
                    }
                },

            };

            req.RevokeCertificateRequest.RequestId = req.RequestHeader.RequestId;
            req.RevokeCertificateRequest.CustomerId = req.RequestHeader.CustomerId;
            req.RevokeCertificateRequest.CustomerId = req.RequestHeader.CustomerId;

            req.RevokeCertificateRequest.Environment = req.RequestHeader.Environment;
            req.RevokeCertificateRequest.EnvironmentSpecified = req.RequestHeader.EnvironmentSpecified;

            try
            {
                var res = _proxy.RevokeCertificate(req);
                return new WrappedResponse<RevokeCertificateOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<RevokeCertificateOutType>(ex.Details);
            }
        }

        /// <summary>
        /// Revokes a given list of certificates.
        /// Note: Certificates are revoked in pairs, so if only an encryption certificate is specified its corrosponding signing certificate will also be revoked
        /// </summary>
        /// <param name="keyTypeToRevoke"></param>
        /// <param name="serialNumbers"></param>
        /// <param name="reason"></param>
        /// <returns></returns>
        public WrappedResponse<RevokeCertificateOutType> RevokeCertificates(KeyGeneratorTypeType keyTypeToRevoke, string[] serialNumbers, CRLReasonType? reason = null)
        {
            EnsureSigningStrategyIsSet();

            var req = new RevokeCertificateInType()
            {
                RequestHeader = CreateHeader(),
                RevokeCertificateRequest = new RevokeCertificateRequest()
                {
                    KeyGeneratorType = keyTypeToRevoke,
                    CRLReasonSpecified = reason != null,
                    Items = serialNumbers
                }
            };

            req.RevokeCertificateRequest.RequestId = req.RequestHeader.RequestId;
            req.RevokeCertificateRequest.CustomerId = req.RequestHeader.CustomerId;
            req.RevokeCertificateRequest.CustomerId = req.RequestHeader.CustomerId;

            req.RevokeCertificateRequest.Environment = req.RequestHeader.Environment;
            req.RevokeCertificateRequest.EnvironmentSpecified = req.RequestHeader.EnvironmentSpecified;

            try
            {
                var res = _proxy.RevokeCertificate(req);
                return new WrappedResponse<RevokeCertificateOutType>(res);
            }
            catch (PKIFactoryFaultException ex)
            {
                return new WrappedResponse<RevokeCertificateOutType>(ex.Details);
            }
        }
    }
}
