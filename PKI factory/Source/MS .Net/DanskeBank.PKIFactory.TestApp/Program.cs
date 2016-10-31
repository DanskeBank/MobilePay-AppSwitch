using System;
using DanskeBank.PKIFactory.Library;
using DanskeBank.PKIFactory.Library.SoapService;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography;
using DanskeBank.PKIFactory.Library.Cryptography.Signing;
using System.Configuration;
using Org.BouncyCastle.Math;

namespace DanskeBank.PKIFactory.TestApp
{
    /// <summary>
    /// Demo application for certificate operations with PKI Factory.
    /// </summary>
    public class Program
    {
        /// <summary>
        /// Used when calling program in tests.
        /// </summary>
        public static bool isInTest = false;

        /// <summary>
        /// Customer ID provided by the bank.
        /// </summary>
        private static readonly string CustomerId = ConfigurationManager.AppSettings["Customer Id"];

        /// <summary>
        /// PIN provided by the bank.
        /// </summary>
        private static readonly string CustomerPIN = ConfigurationManager.AppSettings["Customer PIN"];

        /// <summary>
        /// Action types that are accepted as command line actions (with preceeding hyphen) by the program.
        /// </summary>
        public enum ActionType
        {
            NONE,
            CREATE,
            RENEW,
            REVOKE,
            REVOKEALL,
            STATUS,
            STATUSALL,
            HELP
        }
        
        private static ActionType ParseAction(string[] args)
        {
            if (args.Length == 0)
                return ActionType.NONE;

            try
            {
                return (ActionType)Enum.Parse(typeof(ActionType), args[0].Substring(1).ToUpper());
            }
            catch (Exception)
            {
                return ActionType.HELP;
            }
        }

        /// <summary>
        /// Client that performs calls to PKI factory service.
        /// </summary>
        public static IPKIClient PKIClient { get; set; }

        /// <summary>
        /// Utility class for accessing the windows certificate store.
        /// </summary>
        public static CertStore CertStore { get; set; }

        /// <summary>
        /// Program action that is being performed.
        /// </summary>
        public static ActionType CurrentAction { get; set; }

        private static bool CheckForError<T>(WrappedResponse<T> response) where T : class
        {
            if (response.IsSuccessful)
            {
                Console.WriteLine(typeof(T).Name + " succeeded.");
                return true;
            }

            Console.WriteLine(typeof(T).Name + " failed.");
            Console.WriteLine("  ReturnCode: " + response.Error.ReturnCode);
            Console.WriteLine("  ReturnText: " + response.Error.ReturnText);
            //Console.WriteLine("[Press Enter to continue]");
            //Console.ReadLine();

            return false;
        }

        private static void exitProgram(int exitCode = 0)
        {
            Console.WriteLine("[Press Enter to close]");
            Console.ReadLine();
            
            if (isInTest)
            {
                System.Environment.ExitCode = exitCode;
            } else
            {
                System.Environment.Exit(exitCode);
            }
        }

        private static void displayHelpText()
        {
            Console.WriteLine(
@"Usage: TestApp [-action]

Where action is:
    -create       Issues new ecryption and signing certificates.
    -renew        Renews currently issued certificates.
    -revoke [<serial> ...]
                  Revokes one or more certificates by specified serial numbers
                  in decimal format. If no serial is specified, then current
                  issued certificates are revoked.
    -revokeall [<exclude-serial> ...]
                  Revokes all issued certificates except the ones with provided
                  serial number in decimal format. If no exclude-serial is
                  specified, then current issued certificates are NOT revoked.
    -status [<serial> ...]
                  Displays status of one or more certificates by serial numbers
                  in decimal format. If no serial specified current issued
                  certificates status is shown.
    -statusall    Lists all issued certificates, active or not.
    -help         Displays this help text.
"
            );
        }

        private static string[] parseSerials(string[] args)
        {
            if (args.Length <= 1)
                return null;

            var serials = new string[args.Length - 1];
            Array.ConstrainedCopy(args, 1, serials, 0, serials.Length);
            return serials;
        }

        private static string HexToDec(string str)
        {
            return new BigInteger(str, 16).ToString();
        }

        #region Obtaining bank certificates

        private static void SetBankRootCertificate()
        {
            X509Certificate2 rootCert = CertStore.GetCertificate(CertStore.Certificiates.BankRoot);
            if (rootCert == null)
            {
                Console.WriteLine("Bank root certificate was not set in certificate store.");
                Console.WriteLine("Please set the correct bank root certificate in the \"DanskeBank.PKIFactory\" store using the friendlyname \"" + CertStore.Certificiates.BankRoot.ToString() + "\".");
                Console.ReadLine();
                exitProgram(1);
                return;
            }

            // Tell the client proxy to use the loaded root certificate
            PKIClient.BankRootCertificate = rootCert;
        }

        private static void ObtainLatestBankCertificates()
        {
            /*
             * This obtains the latest certificates used by the bank.
             * They have a finite lifecycle, so they must be renewed a regular interval
             *  - Root certifiate: 10 years
             *  - Signing certificate: 2 years
             *  - Encryption certificate: 2 years
             */
            Console.WriteLine("Requesting bank certificates ...");

            // Obtain bank certificate
            var res = PKIClient.GetBankCertificate();
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return;
            }

            Console.WriteLine("  Retrieved new certificates from bank.");
            Console.WriteLine("Saving bank certificates ...");

            // Save the bank certificates
            CertStore.SetCertificate(CertStore.Certificiates.BankRoot, res.Response.GetBankCertificateResponse.BankRootCert);
            CertStore.SetCertificate(CertStore.Certificiates.BankEncryption, res.Response.GetBankCertificateResponse.BankEncryptionCert);
            CertStore.SetCertificate(CertStore.Certificiates.BankSigning, res.Response.GetBankCertificateResponse.BankSigningCert);
            Console.WriteLine(" Bank certificates successfully saved.");

            // Instruct the PKIClient to use the new certificates
            PKIClient.BankRootCertificate = CertStore.GetCertificate(CertStore.Certificiates.BankRoot);
            PKIClient.SetBankCertificates(CertStore.GetCertificate(CertStore.Certificiates.BankEncryption), CertStore.GetCertificate(CertStore.Certificiates.BankSigning));
            Console.WriteLine(" Using new bank certificates.");
        }

#endregion

        private static void UseIssuedCertificateForSigning()
        {
            Console.WriteLine("Using certificates issued to client ...");
            var issuedSigningCertificate = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);
            PKIClient.XmlSigningService.SetClientCertificate(issuedSigningCertificate, (RSACryptoServiceProvider)issuedSigningCertificate.PrivateKey);
            Console.WriteLine("  Signing certificate successfully set.");
        }

        private static CreateCertificateResponse CreateCertificates()
        {
            Console.WriteLine("Loading own certificates ...");
            var myLoadedCryptoCert = CertStore.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption);
            var myLoadedSigningCert = CertStore.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning);

            if (myLoadedCryptoCert == null || myLoadedSigningCert == null)
            {
                Console.WriteLine("Client generated certificates and private keys were not set in certificate CertStore.");
                Console.WriteLine("Please set the encryption and signing certificates in the \"DanskeBank.PKIFactory\" CertStore using the friendlyname names:");
                Console.WriteLine("  Signing: \"" + CertStore.Certificiates.ClientGeneratedSigning.ToString() + "\".");
                Console.WriteLine("  Encryption: \"" + CertStore.Certificiates.ClientGeneratedEncryption.ToString() + "\".");
                exitProgram(1);
                return null;
            }

            Console.WriteLine("Sending certificate signing requests for own certificates ...");
            var res = PKIClient.CreateCertificate(myLoadedSigningCert, myLoadedCryptoCert, CustomerPIN, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return null;
            }

            // Read the x509 certificates returned from the server
            var issuedCryptoCert = new X509Certificate2(res.Response.CreateCertificateResponse.EncryptionCert);
            var issuedSigningCert = new X509Certificate2(res.Response.CreateCertificateResponse.SigningCert);

            // Set the private key on the X509Certificate2 instances, so we can easily CertStore them
            issuedCryptoCert.PrivateKey = myLoadedCryptoCert.PrivateKey;
            issuedSigningCert.PrivateKey = myLoadedSigningCert.PrivateKey;

            // Save the issued certificataes
            CertStore.SetCertificate(CertStore.Certificiates.ClientIssuedEncryption, issuedCryptoCert.Export(X509ContentType.Pkcs12));
            CertStore.SetCertificate(CertStore.Certificiates.ClientIssuedSigning, issuedSigningCert.Export(X509ContentType.Pkcs12));

            Console.WriteLine("  Certificates created successfully.");
            return res.Response.CreateCertificateResponse;
        }

        private static RenewCertificateResponse RenewCertificates()
        {
            Console.WriteLine("Renewing own certificates ...");

            var myIssuedCryptoCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption);
            var myIssuedSigningCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);

            // This issues a new set of certificates without revoking the old ones.
            var res = PKIClient.RenewCertificate(myIssuedSigningCert, myIssuedCryptoCert, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return null;
            }

            // backup old certificates
            CertStore.SetCertificate(CertStore.Certificiates.BackupClientIssuedEncryption, myIssuedCryptoCert.Export(X509ContentType.Pkcs12));
            CertStore.SetCertificate(CertStore.Certificiates.BackupClientIssuedSigning, myIssuedSigningCert.Export(X509ContentType.Pkcs12));

            // Read the x509 certificates returned from the server and attach the private keys
            X509Certificate2 newIssuedCryptoCert = new X509Certificate2(res.Response.RenewCertificateResponse.EncryptionCert);
            X509Certificate2 newIssuedSigningCert = new X509Certificate2(res.Response.RenewCertificateResponse.SigningCert);

            // Set the private key on the X509Certificate2 instances, so we can easily store them
            newIssuedCryptoCert.PrivateKey = myIssuedCryptoCert.PrivateKey;
            newIssuedSigningCert.PrivateKey = myIssuedSigningCert.PrivateKey;

            // Save the newly issued certificates in the certificate store
            CertStore.SetCertificate(CertStore.Certificiates.ClientIssuedEncryption, newIssuedCryptoCert.Export(X509ContentType.Pkcs12));
            CertStore.SetCertificate(CertStore.Certificiates.ClientIssuedSigning, newIssuedSigningCert.Export(X509ContentType.Pkcs12));

            Console.WriteLine("  Renewal was successful.");

            return res.Response.RenewCertificateResponse;
        }

        private static void RevokeCertificates(string[] serials)
        {
            if (serials == null || serials.Length == 0)
            {
                var myEncryptionCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption);
                var mySigningCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);

                Console.WriteLine("Revoking issued certificates ...");
                serials = new string[]
                {
                    HexToDec(myEncryptionCert.SerialNumber),
                    HexToDec(mySigningCert.SerialNumber)
                };
            }
            else
            {
                Console.WriteLine("Revoking provided serials certificates ...");
            }

            var res = PKIClient.RevokeCertificates(KeyGeneratorTypeType.software, serials);
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return;
            }

            Console.WriteLine("  Revoke was successful.");
        }

        private static void RevokeAllCertificates(string[] excludedSerials)
        {
            if (excludedSerials == null || excludedSerials.Length == 0)
            {
                var myEncryptionCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption);
                var mySigningCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);

                Console.WriteLine("Revoking all certificates EXCEPT issued ones ...");
                excludedSerials = new string[]
                {
                    HexToDec(myEncryptionCert.SerialNumber),
                    HexToDec(mySigningCert.SerialNumber)
                };
            }
            else
            {
                Console.WriteLine("Revoking all not excluded certificates ...");
            }

            // Revoke all certificates except our initial ones.
            var res = PKIClient.RevokeAllCertificates(KeyGeneratorTypeType.software, excludedSerials);
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return;
            }

            Console.WriteLine("  Revoke was successful.");
        }

        private static void CertificateStatus(string[] serials = null)
        {
            if (serials == null || serials.Length == 0)
            {
                Console.WriteLine("Checking status of issued certificates ...");
                var myEncryptionCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption);
                var mySigningCert = CertStore.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);
                serials = new string[] { HexToDec(myEncryptionCert.SerialNumber), HexToDec(mySigningCert.SerialNumber) };
            }
            else
            {
                Console.WriteLine("Checking status of provided serials certificates ...");
            }

            var res = PKIClient.CertificateStatus(serials, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
            {
                exitProgram(Int32.Parse(res.Error.ReturnCode));
                return;
            }

            foreach (var certStatus in res.Response.CertificateStatusResponse.CertificateStatus)
                PrintCertInfo(certStatus);
        }

        private static void AllCertificiateStatus()
        {
            Console.WriteLine("Getting all our own certificates ...");
            var res = PKIClient.GetClientCertificateList(KeyGeneratorTypeType.software);
            if (!CheckForError(res))
                exitProgram(Int32.Parse(res.Error.ReturnCode));

            foreach (var certStatus in res.Response.GetOwnCertificateListResponse.CertificateStatus)
                PrintCertInfo(certStatus);

            Console.WriteLine("Total certificates: " + res.Response.GetOwnCertificateListResponse.CertificateStatus.Length);
        }

        private static void PrintCertInfo(CertificateStatusType certStatus)
        {
            Console.WriteLine("Certificate:");
            Console.WriteLine(" - Serial: " + certStatus.CertificateSerialNo);
            Console.WriteLine(" - CertificateType: " + certStatus.CertificateType);
            Console.WriteLine(" - MatchingSerial: " + certStatus.MatchingCertificateSerialNo);
            Console.WriteLine(" - Status: " + certStatus.Status.Item.GetType().Name);
        }

        public static void Main(string[] args)
        {
            var curAction = ParseAction(args);

            if (curAction == ActionType.HELP)
            {
                displayHelpText();
                exitProgram();
                return;
            }

            // Create client that will perform calls to PKI factory
            PKIClient = new PKIClient(CustomerId);

            // Create a utility class for accessing the windows certificate store
            CertStore = new CertStore(); 

            /* 
             * Create a software based XML signing strategy
             * A custom implementation backed by a hardware security module could be used here instead
             */
            PKIClient.XmlSigningService = new SoftwareBasedXmlSigningService();

            SetBankRootCertificate();
            ObtainLatestBankCertificates();

            if (curAction != ActionType.CREATE && curAction != ActionType.NONE)
                UseIssuedCertificateForSigning();

            switch (curAction)
            {
                case ActionType.NONE:
                    // Do it all
                    CreateCertificates();
                    UseIssuedCertificateForSigning();
                    RenewCertificates();
                    UseIssuedCertificateForSigning();
                    CertificateStatus(parseSerials(args));
                    break;
                case ActionType.CREATE:
                    CreateCertificates();
                    break;
                case ActionType.RENEW:
                    RenewCertificates();
                    break;
                case ActionType.REVOKE:
                    // Revoke the specified certificates
                    // Note: Revoking latest issued certificate will force you to request new certificates based on the locally generated ones
                    RevokeCertificates(parseSerials(args));
                    break;
                case ActionType.REVOKEALL:
                    // Revoke all certificates except with serials provided
                    // Note: Revoking latest issued certificate will force you to request new certificates based on the locally generated ones
                    RevokeAllCertificates(parseSerials(args));
                    break;
                case ActionType.STATUS:
                    CertificateStatus(parseSerials(args));
                    break;
                case ActionType.STATUSALL:
                    AllCertificiateStatus();
                    break;
            }

            exitProgram();
        }
    }
}