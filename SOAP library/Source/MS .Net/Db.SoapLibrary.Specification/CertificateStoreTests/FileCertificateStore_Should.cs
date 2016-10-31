using System.Configuration;
using DB.SoapLibrary.Configuration.CertificateStore;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.CertificateStoreTests
{
    [TestFixture]
    [Category("UnitTest")]
    public class FileCertificateStore_Should
    {
        private ICertificateStore sut;
        private string originalCertificateStoreId;
        private string originalCertificateStoreType;

        [TestFixtureSetUp]
        public void TestFixtureSetUp()
        {
            originalCertificateStoreId = ConfigurationManager.AppSettings.Get("CertificateStoreId");
            originalCertificateStoreType = ConfigurationManager.AppSettings.Get("CertificateStoreType");

            ConfigurationManager.AppSettings.Set("CertificateStoreId", "061133");
            ConfigurationManager.AppSettings.Set("CertificateStoreType", "File");
   
            sut = CertificateStoreFactory.GetCertificateStore();
            Assert.IsInstanceOf<FileCertificateStore>(sut);
        }

        [TestFixtureTearDown]
        public void TestFixtureTearDown()
        {
            ConfigurationManager.AppSettings.Set("CertificateStoreId", originalCertificateStoreId);
            ConfigurationManager.AppSettings.Set("CertificateStoreType", originalCertificateStoreType);
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