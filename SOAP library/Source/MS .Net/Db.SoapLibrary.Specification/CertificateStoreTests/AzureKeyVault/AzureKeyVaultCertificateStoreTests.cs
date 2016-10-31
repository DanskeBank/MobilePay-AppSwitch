using DB.SoapLibrary;
using DB.SoapLibrary.Configuration.CertificateStore;
using DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault;
using Moq;
using NUnit.Framework;
using System;
using Shouldly;
using System.Security.Cryptography.X509Certificates;

namespace Db.SoapLibrary.Specification.CertificateStoreTests.AzureKeyVault
{
	[TestFixture]
	public class AzureKeyVaultCertificateStoreTests
	{
		[TestFixture]
		public class Constructor
		{
			[Test]
			public void when_a_constructor_argument_is_invalid_then_an_exception_is_thrown()
			{
				var timeProvider = new Mock<ITimeProvider>().Object;
				var certificateProvider = new Mock<ICertificateProvider>().Object;
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(null, certificateProvider, certificateProvider, certificateProvider, timeProvider, 1.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, null, certificateProvider, certificateProvider, timeProvider, 1.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, certificateProvider, null, certificateProvider, timeProvider, 1.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, certificateProvider, certificateProvider, null, timeProvider, 1.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, certificateProvider, certificateProvider, certificateProvider, null, 1.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, certificateProvider, certificateProvider, certificateProvider, null, 0.seconds()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new AzureKeyVaultCertificateStore(certificateProvider, certificateProvider, certificateProvider, certificateProvider, null, new TimeSpan(-1L)));
			}
		}

		[TestFixture]
		public class ClientEncryptionCertificate
		{
			Mock<ICertificateProvider> clientEncryptCertProviderMock;
			Mock<ICertificateProvider> clientSignCertProviderMock;
			Mock<ICertificateProvider> bankEncryptCertProviderMock;
			Mock<ICertificateProvider> bankSignCertProviderMock;
			AzureKeyVaultCertificateStore store;
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;

			[SetUp]
			public void Setup()
			{
				clientEncryptCertProviderMock = new Mock<ICertificateProvider>();
				clientSignCertProviderMock = new Mock<ICertificateProvider>();
				bankSignCertProviderMock = new Mock<ICertificateProvider>();
				bankEncryptCertProviderMock = new Mock<ICertificateProvider>();
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.seconds();

				clientEncryptCertProviderMock.Setup(provider => provider.GetCertificate()).Returns(new X509Certificate2());

				store = new AzureKeyVaultCertificateStore(
					clientEncryptCertProviderMock.Object,
					clientSignCertProviderMock.Object,
					bankEncryptCertProviderMock.Object,
					bankSignCertProviderMock.Object,
					timeProvider, certificateInMemLifeSpan);
			}

			[Test]
			public void when_certificate_has_not_been_cached_then_certificate_provider_is_invoked()
			{
				store.ClientEncryptionCertificate().ShouldNotBeNull();
				clientEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_not_expired_then_certificate_provider_is_not_invoked()
			{
				store.ClientEncryptionCertificate(); //to cache it
				store.ClientEncryptionCertificate().ShouldNotBeNull();
				clientEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_also_expired_then_certificate_provider_is_invoked()
			{
				store.ClientEncryptionCertificate(); //to cache it
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				store.ClientEncryptionCertificate().ShouldNotBeNull();
				clientEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Exactly(2));
			}
		}

		[TestFixture]
		public class ClientSignatureCertificate
		{
			Mock<ICertificateProvider> clientEncryptCertProviderMock;
			Mock<ICertificateProvider> clientSignCertProviderMock;
			Mock<ICertificateProvider> bankEncryptCertProviderMock;
			Mock<ICertificateProvider> bankSignCertProviderMock;
			AzureKeyVaultCertificateStore store;
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;

			[SetUp]
			public void Setup()
			{
				clientEncryptCertProviderMock = new Mock<ICertificateProvider>();
				clientSignCertProviderMock = new Mock<ICertificateProvider>();
				bankSignCertProviderMock = new Mock<ICertificateProvider>();
				bankEncryptCertProviderMock = new Mock<ICertificateProvider>();
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.seconds();

				clientSignCertProviderMock.Setup(provider => provider.GetCertificate()).Returns(new X509Certificate2());

				store = new AzureKeyVaultCertificateStore(
					clientEncryptCertProviderMock.Object,
					clientSignCertProviderMock.Object,
					bankEncryptCertProviderMock.Object,
					bankSignCertProviderMock.Object,
					timeProvider, certificateInMemLifeSpan);
			}

			[Test]
			public void when_certificate_has_not_been_cached_then_certificate_provider_is_invoked()
			{
				store.ClientSignatureCertificate().ShouldNotBeNull();
				clientSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_not_expired_then_certificate_provider_is_not_invoked()
			{
				store.ClientSignatureCertificate(); //to cache it
				store.ClientSignatureCertificate().ShouldNotBeNull();
				clientSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_also_expired_then_certificate_provider_is_invoked()
			{
				store.ClientSignatureCertificate(); //to cache it
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				store.ClientSignatureCertificate().ShouldNotBeNull();
				clientSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Exactly(2));
			}
		}

		[TestFixture]
		public class DBEncryptionCertificate
		{
			Mock<ICertificateProvider> clientEncryptCertProviderMock;
			Mock<ICertificateProvider> clientSignCertProviderMock;
			Mock<ICertificateProvider> bankEncryptCertProviderMock;
			Mock<ICertificateProvider> bankSignCertProviderMock;
			AzureKeyVaultCertificateStore store;
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;

			[SetUp]
			public void Setup()
			{
				clientEncryptCertProviderMock = new Mock<ICertificateProvider>();
				clientSignCertProviderMock = new Mock<ICertificateProvider>();
				bankSignCertProviderMock = new Mock<ICertificateProvider>();
				bankEncryptCertProviderMock = new Mock<ICertificateProvider>();
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.seconds();

				bankEncryptCertProviderMock.Setup(provider => provider.GetCertificate()).Returns(new X509Certificate2());

				store = new AzureKeyVaultCertificateStore(
					clientEncryptCertProviderMock.Object,
					clientSignCertProviderMock.Object,
					bankEncryptCertProviderMock.Object,
					bankSignCertProviderMock.Object,
					timeProvider, certificateInMemLifeSpan);
			}

			[Test]
			public void when_certificate_has_not_been_cached_then_certificate_provider_is_invoked()
			{
				store.DBEncryptionCertificate().ShouldNotBeNull();
				bankEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_not_expired_then_certificate_provider_is_not_invoked()
			{
				store.DBEncryptionCertificate(); //to cache it
				store.DBEncryptionCertificate().ShouldNotBeNull();
				bankEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_also_expired_then_certificate_provider_is_invoked()
			{
				store.DBEncryptionCertificate(); //to cache it
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				store.DBEncryptionCertificate().ShouldNotBeNull();
				bankEncryptCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Exactly(2));
			}
		}

		[TestFixture]
		public class DBSignatureCertificate
		{
			Mock<ICertificateProvider> clientEncryptCertProviderMock;
			Mock<ICertificateProvider> clientSignCertProviderMock;
			Mock<ICertificateProvider> bankEncryptCertProviderMock;
			Mock<ICertificateProvider> bankSignCertProviderMock;
			AzureKeyVaultCertificateStore store;
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;

			[SetUp]
			public void Setup()
			{
				clientEncryptCertProviderMock = new Mock<ICertificateProvider>();
				clientSignCertProviderMock = new Mock<ICertificateProvider>();
				bankSignCertProviderMock = new Mock<ICertificateProvider>();
				bankEncryptCertProviderMock = new Mock<ICertificateProvider>();
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.seconds();

				bankSignCertProviderMock.Setup(provider => provider.GetCertificate()).Returns(new X509Certificate2());

				store = new AzureKeyVaultCertificateStore(
					clientEncryptCertProviderMock.Object,
					clientSignCertProviderMock.Object,
					bankEncryptCertProviderMock.Object,
					bankSignCertProviderMock.Object,
					timeProvider, certificateInMemLifeSpan);
			}

			[Test]
			public void when_certificate_has_not_been_cached_then_certificate_provider_is_invoked()
			{
				store.DBSignatureCertificate().ShouldNotBeNull();
				bankSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_not_expired_then_certificate_provider_is_not_invoked()
			{
				store.DBSignatureCertificate(); //to cache it
				store.DBSignatureCertificate().ShouldNotBeNull();
				bankSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}

			[Test]
			public void when_certificate_is_cached_and_also_expired_then_certificate_provider_is_invoked()
			{
				store.DBSignatureCertificate(); //to cache it
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				store.DBSignatureCertificate().ShouldNotBeNull();
				bankSignCertProviderMock.Verify(provider => provider.GetCertificate(), Times.Exactly(2));
			}
		}
	}
}
