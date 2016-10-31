using System;
using System.Configuration;
using System.Linq;
using System.Security.Cryptography.X509Certificates;

namespace DB.SoapLibrary.Configuration.CertificateStore
{
    /// <summary>
    /// Provides a wrapper for accessing Windows' certificate store
    /// </summary>
    public class MsCertificateStore : ICertificateStore
    {
        private readonly X509Store _store;

        public MsCertificateStore()
        {
            var storename = ConfigurationManager.AppSettings.Get("CertificateStoreId");
            if(string.IsNullOrEmpty(storename))
                throw new ArgumentException("storename must be set");
            _store = new X509Store(storename, StoreLocation.CurrentUser);
            _store.Open(OpenFlags.ReadWrite);
        }

        private X509Certificate2 GetByFriendlyName(string friendlyName)
        {
            var certs = _store.Certificates.Cast<X509Certificate2>();
            var cert = certs.FirstOrDefault(x => x.FriendlyName == friendlyName);
            return cert;
        }

        public X509Certificate2 ClientEncryptionCertificate()
        {
            return GetByFriendlyName("ClientIssuedEncryption");
        }

        public X509Certificate2 ClientSignatureCertificate()
        {
            return GetByFriendlyName("ClientIssuedSigning");
        }

        public X509Certificate2 DBEncryptionCertificate()
        {
            return GetByFriendlyName("BankEncryption");
        }

        public X509Certificate2 DBSignatureCertificate()
        {
            return GetByFriendlyName("BankSigning");
        }
    }
}
