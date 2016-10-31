using System.Configuration;
using DB.SoapLibrary.Configuration.CertificateStore;
using NUnit.Framework;

namespace Db.SoapLibrary.Specification.CertificateStoreTests
{
    [TestFixture]
    [Category("UnitTest")]
    public class CertificateStorefactory_Should
    {
        private string _originalCertificateStoreId;
        private string _originalCertificateStoreType;

        [TestFixtureSetUp]
        public void TestFixtureSetUp()
        {
            _originalCertificateStoreId = ConfigurationManager.AppSettings.Get("CertificateStoreId");
            _originalCertificateStoreType = ConfigurationManager.AppSettings.Get("CertificateStoreType");
        }

        [TestFixtureTearDown]
        public void TearDown()
        {
            ConfigurationManager.AppSettings.Set("CertificateStoreId", _originalCertificateStoreId);
            ConfigurationManager.AppSettings.Set("CertificateStoreType", _originalCertificateStoreType);
        }
        [Test]
        public void Load_FileStore()
        {
            ConfigurationManager.AppSettings.Set("CertificateStoreType", "File");
            ConfigurationManager.AppSettings.Set("CertificateStoreId", "061133");
            Assert.IsInstanceOf<FileCertificateStore>(CertificateStoreFactory.GetCertificateStore());
        }

        [Test]
        public void Load_MSCertificateStore()
        {
            ConfigurationManager.AppSettings.Set("CertificateStoreType", "MSCert");
            ConfigurationManager.AppSettings.Set("CertificateStoreId", "DanskeBank.PKIFactory");
            Assert.IsInstanceOf<MsCertificateStore>(CertificateStoreFactory.GetCertificateStore());
        }
         
    }
}
