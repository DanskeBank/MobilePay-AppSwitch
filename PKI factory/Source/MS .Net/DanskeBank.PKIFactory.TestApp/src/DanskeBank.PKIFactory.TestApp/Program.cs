using System;
using DanskeBank.PKIFactory.Library;
using DanskeBank.PKIFactory.Library.SoapService;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography;
using DanskeBank.PKIFactory.Library.Cryptography.Signing;

namespace DanskeBank.PKIFactory.TestApp
{
    class Program
    {
        /// <summary>
        /// Customer ID provided by the bank
        /// </summary>
        private static readonly string Customer_Id = "";

        /// <summary>
        /// PIN provided by the bank
        /// </summary>
        private static readonly string Customer_PIN = "";

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
            Console.WriteLine("[Press enter to continue]");
            Console.ReadLine();

            return false;
        }

        private static string HexToDec(string str)
        {
            return Convert.ToInt64(str, 16).ToString();
        }


        #region Obtaining bank certificates


        private static WrappedResponse<GetBankCertificateOutType> ObtainLatestBankCertificates(PKIClient client)
        {
            var res = client.GetBankCertificate();
            if (!CheckForError(res))
                return null;

            return res;
        }

        private static void SaveBankCertificates(GetBankCertificateOutType certRes, CertStore store)
        {
            store.SetCertificate(CertStore.Certificiates.BankRoot, certRes.GetBankCertificateResponse.BankRootCert);
            store.SetCertificate(CertStore.Certificiates.BankEncryption, certRes.GetBankCertificateResponse.BankEncryptionCert);
            store.SetCertificate(CertStore.Certificiates.BankSigning, certRes.GetBankCertificateResponse.BankSigningCert);
        }


        #endregion

        private static WrappedResponse<CreateCertificateOutType> CreateCertificates(PKIClient client, string pin, CertStore store)
        {
            var myLoadedCryptoCert = store.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption);
            var myLoadedSigningCert = store.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning);


            var res = client.CreateCertificate(myLoadedSigningCert, myLoadedCryptoCert, pin, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
                return null;

            return res;
        }

        private static void SaveClientCertificates(CreateCertificateOutType res, CertStore store)
        {
            // Read the x509 certificates returned from the server and attach the private keys
            X509Certificate2 issuedCryptoCertificate = new X509Certificate2(res.CreateCertificateResponse.EncryptionCert);
            X509Certificate2 issuedSigningCertificate = new X509Certificate2(res.CreateCertificateResponse.SigningCert);

            // Set the private key on the X509Certificate2 instances, so we can easiliy store them
            issuedCryptoCertificate.PrivateKey = store.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption).PrivateKey;
            issuedSigningCertificate.PrivateKey = store.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning).PrivateKey;

            // The certificate used by the bank for issuing certificates for our signing requests
            X509Certificate2 bankCACertificate = new X509Certificate2(res.CreateCertificateResponse.CACert);

            // Save the issued certificataes
            store.SetCertificate(CertStore.Certificiates.ClientIssuedEncryption, issuedCryptoCertificate.Export(X509ContentType.Pkcs12));
            store.SetCertificate(CertStore.Certificiates.ClientIssuedSigning, issuedSigningCertificate.Export(X509ContentType.Pkcs12));
        }

        private static RenewCertificateResponse RenewCertificates(PKIClient client, CertStore store)
        {
            var myLoadedCryptoCert = store.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption);
            var myLoadedSigningCert = store.GetCertificate(CertStore.Certificiates.ClientIssuedSigning);
            var myGeneratedCryptoCert = store.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption);
            var myGeneratedSigningCert = store.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning);

            // This issues a new set of certificates without revoking the old ones.
            var res = client.RenewCertificate(myLoadedSigningCert, myLoadedCryptoCert, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
                return null;

            // Read the x509 certificates returned from the server and attach the private keys
            X509Certificate2 issuedCryptoCertificate = new X509Certificate2(res.Response.RenewCertificateResponse.EncryptionCert);
            X509Certificate2 issuedSigningCertificate = new X509Certificate2(res.Response.RenewCertificateResponse.SigningCert);

            // Set the private key on the X509Certificate2 instances, so we can easiliy store them
            issuedCryptoCertificate.PrivateKey = myGeneratedCryptoCert.PrivateKey;
            issuedSigningCertificate.PrivateKey = myGeneratedSigningCert.PrivateKey;

            // Save the newly issued certificates in the certificate store
            store.SetCertificate(CertStore.Certificiates.ClientIssuedEncryption, issuedCryptoCertificate.Export(X509ContentType.Pkcs12));
            store.SetCertificate(CertStore.Certificiates.ClientIssuedSigning, issuedSigningCertificate.Export(X509ContentType.Pkcs12));

            return res.Response.RenewCertificateResponse;
        }



        private static void CertificateStatus(PKIClient client, X509Certificate2 queryCert)
        {
            var res = client.CertificateStatus(new[] { HexToDec(queryCert.SerialNumber) }, KeyGeneratorTypeType.software);
            if (!CheckForError(res))
                return;

            foreach (var x in res.Response.CertificateStatusResponse.CertificateStatus)
            {
                Console.WriteLine(" - " + x.CertificateType);
                Console.WriteLine(" - " + x.Status.Item);
            }
        }

        private static void GetOwnCertificiateList(PKIClient client)
        {
            var res = client.GetClientCertificateList(KeyGeneratorTypeType.software);
            if (!CheckForError(res))
                return;

            foreach (var certStatus in res.Response.GetOwnCertificateListResponse.CertificateStatus)
            {
                Console.WriteLine("Certificate:");
                Console.WriteLine(" - Serial: " + certStatus.CertificateSerialNo);
                Console.WriteLine(" - CertificateType: " + certStatus.CertificateType);
                Console.WriteLine(" - MatchingSerial: " + certStatus.MatchingCertificateSerialNo);
                Console.WriteLine(" - Status: " + certStatus.Status);
            }
        }

        static void Main(string[] args)
        {
            var client = new PKIClient(Customer_Id);

            /* 
             * Create a software based XML signing strategy
             * A custom implementation backed by a hardware security module could be used here instead
            */
            SoftwareBasedXmlSigningService xmlSigningStrategy = new SoftwareBasedXmlSigningService();
            client.SetXmlSigningService(xmlSigningStrategy);

            // Create a utility class for accessing the windows certificate store
            var store = new CertStore();

            X509Certificate2 rootCert = store.GetCertificate(CertStore.Certificiates.BankRoot);
            if (rootCert == null)
            {
                Console.WriteLine("Bank root certificate was not set in certificate store.");
                Console.WriteLine("Please set the correct bank root certificate in the \"DanskeBank.PKIFactory\" store using the friendlyname \"" + CertStore.Certificiates.BankRoot.ToString() + "\".");
                Console.ReadLine();
                return;
            }

            // Tell the client proxy to use the loaded root certificate
            client.SetBankRootCertificate(rootCert);

            #region Obtain bank certificates
            /*
             * This obtains the latest certificates used by the bank.
             * They have a finite lifecycle, so they must be renewed a regular interval
             *  - Root certifiate: 10 years
             *  - Signing certificate: 2 years
             *  - Encryption certificate: 2 years
             */
            Console.WriteLine("Requesting bank certificates ...");
            
            // Obtain bank certificate
            var res = ObtainLatestBankCertificates(client);
            if (res.IsSuccessful)
            {
                Console.WriteLine("  Retrieved new certificates from bank.");

                // Save the bank certificates
                Console.WriteLine("  Saving bank certificates ...");
                SaveBankCertificates(res.Response, store);
            }

            // Instruct the client to use the new certificates
            client.SetBankCertificates(store.GetCertificate(CertStore.Certificiates.BankEncryption), store.GetCertificate(CertStore.Certificiates.BankSigning));
            #endregion


            // Determine whether we have already been issued certificates from the bank by querying the key store
            bool mustRequestCertificates = store.GetCertificate(CertStore.Certificiates.ClientIssuedSigning) == null ||
                                           store.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption) == null;

            if (mustRequestCertificates)
            {
                if (store.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption) == null ||
                    store.GetCertificate(CertStore.Certificiates.ClientGeneratedEncryption) == null)
                {
                    Console.WriteLine("Client generated certificates and private keys were not set in certificate store.");
                    Console.WriteLine("Please set the encryption and signing certificates in the \"DanskeBank.PKIFactory\" store using the friendlyname names:");
                    Console.WriteLine("  Signing: \"" + CertStore.Certificiates.ClientGeneratedSigning.ToString() + "\".");
                    Console.WriteLine("  Encryption: \"" + CertStore.Certificiates.ClientGeneratedEncryption.ToString() + "\".");
                    Console.ReadLine();
                    return;
                }


                Console.WriteLine("Sending certificate signing requests for own certificates ...");
                var createCertRes  = CreateCertificates(client, Customer_PIN, store);

                SaveClientCertificates(createCertRes.Response, store);
            }

            // Use the (potentially newly) issued certificates
            Console.WriteLine("Using certificates issued to client ...");
            xmlSigningStrategy.SetClientCertificate(store.GetCertificate(CertStore.Certificiates.ClientIssuedSigning), (RSACryptoServiceProvider)store.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning).PrivateKey);
            
            // Check status of the certificates we're using
            Console.WriteLine("Checking status of own certificates ...");
            CertificateStatus(client, store.GetCertificate(CertStore.Certificiates.ClientIssuedSigning));
            CertificateStatus(client, store.GetCertificate(CertStore.Certificiates.ClientIssuedEncryption));

            /*
            Console.WriteLine("Getting all our own certificates");
            GetOwnCertificiateList(client);
            */
            Console.WriteLine("Renewing own certificates");
            var renewedCerts = RenewCertificates(client, store);

            Console.WriteLine("Using certificates issued to client ...");
            xmlSigningStrategy.SetClientCertificate(new X509Certificate2(renewedCerts.SigningCert), (RSACryptoServiceProvider)store.GetCertificate(CertStore.Certificiates.ClientGeneratedSigning).PrivateKey);
            

            // Revoke the newly renew'ed certificates
            // Note: Revoking this certificate will force you to request new certificates based on the locally generated ones
            // client.RevokeCertificates(KeyGeneratorTypeType.software, new[] { HexToDec(new X509Certificate2(renewedCerts.EncryptionCert).SerialNumber) });

            /*
                // Revoke all certificates except our initial ones.
            client.RevokeAllCertificates(KeyGeneratorTypeType.software, new[] {
                new X509Certificate2(ownInitialEncryptionCert).SerialNumber,
                new X509Certificate2(ownInitialigningCert).SerialNumber,                
            });
            */


            Console.WriteLine("Press enter to close.");
            Console.ReadLine();
        }
    }
}