using System.Linq;
using System.Security.Cryptography.X509Certificates;

namespace DanskeBank.PKIFactory.TestApp
{
    /// <summary>
    /// Provides a convinient wrapper for accessing Windows' certificate store
    /// </summary>
    public class CertStore
    {
        /// <summary>
        /// Accepted certitificate types corresponding to friendly names
        /// </summary>
        public enum Certificiates
        {
            /// <summary>
            /// Latest known bank root certificate
            /// </summary>
            BankRoot = 1,

            /// <summary>
            /// Bank encryption certificate
            /// </summary>
            BankEncryption = 2,

            /// <summary>
            /// Bank signing certificate
            /// </summary>
            BankSigning = 3,

            /// <summary>
            /// Client own generated encryption certificate.
            /// Note: In addition to the certificate, this will contain the private key
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            ClientGeneratedEncryption = 4,

            /// <summary>
            /// Client own generated signing certificate.
            /// Note: In addition to the certificate, this will contain the private key
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            ClientGeneratedSigning = 5,

            /// <summary>
            /// Client encryption certificate issued by the bank.
            /// Note: In addition to the certificate, this will contain the private key
            ///       which corrosponds to the certificate
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            ClientIssuedEncryption = 6,

            /// <summary>
            /// Client signing certificate issued by the bank.
            /// Note: In addition to the certificate, this will contain the private key
            ///       which corrosponds to the certificate
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            ClientIssuedSigning = 7,

            /// <summary>
            /// Backed up version of previous client encryption certificate issued by the bank.
            /// Note: In addition to the certificate, this will contain the private key
            ///       which corrosponds to the certificate
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            BackupClientIssuedEncryption = 8,

            /// <summary>
            /// Backed up version of previous client signing certificate issued by the bank.
            /// Note: In addition to the certificate, this will contain the private key
            ///       which corrosponds to the certificate
            ///       Thus a X509Certificate2 is provided
            /// </summary>
            BackupClientIssuedSigning = 9
        }

        private readonly X509Store _store;

        /// <summary>
        /// Creates client and opens it for "DanskeBank.PKIFactory" certificate store.
        /// </summary>
        public CertStore()
        {
            _store = new X509Store("DanskeBank.PKIFactory", StoreLocation.CurrentUser);
            _store.Open(OpenFlags.ReadWrite);
        }

        /// <summary>
        /// Gets certificate from store by its type corresponding to friendly name.
        /// </summary>
        /// <param name="certType">Certificate type</param>
        /// <returns>Found certificate</returns>
        public X509Certificate2 GetCertificate(Certificiates certType)
        {
            var certs = _store.Certificates.Cast<X509Certificate2>();
            var cert = certs.FirstOrDefault(x => x.FriendlyName == certType.ToString());
            return cert;
        }

        /// <summary>
        /// Saves certificate in store by its type corresponding to friendly name.
        /// </summary>
        /// <param name="certType">Certificate type</param>
        /// <param name="certData">Raw data of the certificate</param>
        public void SetCertificate(Certificiates certType, byte[] certData)
        {
            var oldCert = GetCertificate(certType);
            if (oldCert != null)
                _store.Remove(oldCert);

            var newCert = new X509Certificate2(certData, "", X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet); // Mark the (potentially) private key as exportable
            newCert.FriendlyName = certType.ToString();
            _store.Add(newCert);
        }
    }
}
