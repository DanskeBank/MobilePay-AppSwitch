using System.Linq;
using System.Security.Cryptography.X509Certificates;

namespace DanskeBank.PKIFactory.TestApp
{
    /// <summary>
    /// Provides a convinient wrapper for accessing Windows' certificate store
    /// </summary>
    public class CertStore
    {
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
            ClientIssuedSigning = 7
        }

        private readonly X509Store _store;

        public CertStore()
        {
            _store = new X509Store("DanskeBank.PKIFactory", StoreLocation.CurrentUser);
            _store.Open(OpenFlags.ReadWrite);
        }

        private X509Certificate2 GetByFriendlyName(string friendlyName)
        {
            var certs = _store.Certificates.Cast<X509Certificate2>();
            var cert = certs.FirstOrDefault(x => x.FriendlyName == friendlyName);
            return cert;
        }

        private void SetCertificate(string friendlyName, byte[] certData)
        {
            var certs = _store.Certificates.Cast<X509Certificate2>();

            var oldCert = certs.FirstOrDefault(x => x.FriendlyName == friendlyName);
            if (oldCert != null)
                _store.Remove(oldCert);

            var newCert = new X509Certificate2(certData, "", X509KeyStorageFlags.Exportable | X509KeyStorageFlags.PersistKeySet); // Mark the (potentially) private key as exportable
            newCert.FriendlyName = friendlyName;
            _store.Add(newCert);
        }

        public X509Certificate2 GetCertificate(Certificiates certType)
        {
            return GetByFriendlyName(certType.ToString());
        }

        public void SetCertificate(Certificiates certType, byte[] certData)
        {
            SetCertificate(certType.ToString(), certData);
        }
    }
}
