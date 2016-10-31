using System.Security.Cryptography.X509Certificates;

namespace DB.SoapLibrary.Configuration.CertificateStore
{
    public interface ICertificateStore
    {
        X509Certificate2 ClientEncryptionCertificate();
        X509Certificate2 ClientSignatureCertificate();
        X509Certificate2 DBEncryptionCertificate();
        X509Certificate2 DBSignatureCertificate();
    }
}