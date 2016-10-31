using System.Configuration;
using DB.SoapLibrary.Configuration.CertificateStore;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.CertificateStoreTests
{
    [TestFixture]
    [Category("UnitTest")]
    public class MsCertificateStore_Should
    {
        private MsCertificateStore sut;
        private string originalCertificateStoreId;
        [TestFixtureSetUp]
        public void Setup()
        {
            originalCertificateStoreId = ConfigurationManager.AppSettings.Get("CertificateStoreId");
            ConfigurationManager.AppSettings.Set("CertificateStoreId", "DanskeBank.PKIFactory");
            sut = new MsCertificateStore();
        }

        [TestFixtureTearDown]
        public void TearDown()
        {
            ConfigurationManager.AppSettings.Set("CertificateStoreId", originalCertificateStoreId);
        }

        [Test]
        public void Can_load_db_signing()
        {
            Assert.IsNotNull(sut.DBSignatureCertificate());
        }


        [Test]
        public void Can_load_db_encryption()
        {
            Assert.IsNotNull(sut.DBEncryptionCertificate());
        }

        [Test]
        public void Can_load_client_signing()
        {
            Assert.IsNotNull(sut.ClientSignatureCertificate());
        }


        [Test]
        public void Can_load_client_encryption()
        {
            Assert.IsNotNull(sut.ClientEncryptionCertificate());
        }
    }
}