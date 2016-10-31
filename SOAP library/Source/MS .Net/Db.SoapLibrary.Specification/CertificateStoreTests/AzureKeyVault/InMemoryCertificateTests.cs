using DB.SoapLibrary;
using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Shouldly;
using DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault;
using Moq;
using System.Security.Cryptography.X509Certificates;

namespace Db.SoapLibrary.Specification.CertificateStoreTests.AzureKeyVault
{
	[TestFixture]
	[Category("UnitTest")]
	public class InMemoryCertificateTests
	{
		[TestFixture]
		public class Constructor
		{
			[Test]
			public void when_a_constructor_argument_is_invalid_then_an_exception_is_thrown()
			{
				An<ArgumentNullException>.ShouldBeThrownBy(() => new InMemoryCertificate(null, 2.seconds(), new ControllableTimeProvider()));
				An<ArgumentException>.ShouldBeThrownBy(() => new InMemoryCertificate(new X509Certificate2(), 0.seconds(), new ControllableTimeProvider()));
				An<ArgumentException>.ShouldBeThrownBy(() => new InMemoryCertificate(new X509Certificate2(), new TimeSpan(-1L), new ControllableTimeProvider()));
				An<ArgumentNullException>.ShouldBeThrownBy(() => new InMemoryCertificate(new X509Certificate2(), 2.seconds(), null));

			}
		}

		[TestFixture]
		public class IsExpired
		{
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;
			InMemoryCertificate certificate;

			[SetUp]
			public void Setup()
			{
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.minutes();
				certificate = new InMemoryCertificate(new X509Certificate2(), certificateInMemLifeSpan, timeProvider);
			}

			[Test]
			public void when_expiration_date_has_passed_then_the_certificate_is_expired()
			{
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				certificate.IsExpired.ShouldBe(true);
			}

			[Test]
			public void when_expiration_date_has_not_passed_then_the_certificate_is_not_expired()
			{
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan;
				certificate.IsExpired.ShouldBe(false);
			}
		}

		[TestFixture]
		public class X509
		{
			ControllableTimeProvider timeProvider;
			TimeSpan certificateInMemLifeSpan;
			InMemoryCertificate certificate;

			[SetUp]
			public void Setup()
			{
				timeProvider = new ControllableTimeProvider();
				certificateInMemLifeSpan = 2.minutes();
				certificate = new InMemoryCertificate(new X509Certificate2(), certificateInMemLifeSpan, timeProvider);
			}

			[Test]
			public void when_expiration_date_has_passed_then_getting_the_value_throws_an_exception()
			{
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan + 1.seconds();
				An<Exception>.ShouldBeThrownBy(() => certificate.X509);
			}

			[Test]
			public void when_expiration_date_has_not_passed_then_getting_the_value_returns_the_certificate()
			{
				timeProvider.Now = timeProvider.Now + certificateInMemLifeSpan;
				certificate.X509.ShouldNotBeNull();
			}
		}

		[TestFixture]
		public class UpdateUsing
		{
			Mock<ITimeProvider> timeProviderMock;
			Mock<ICertificateProvider> certificateProviderMock;
			InMemoryCertificate certificate;
			TimeSpan lifeSpan;

			[SetUp]
			public void Setup()
			{
				lifeSpan = 2.seconds();
				timeProviderMock = new Mock<ITimeProvider>();
				certificateProviderMock = new Mock<ICertificateProvider>();
				certificate = new InMemoryCertificate(new X509Certificate2(), lifeSpan, timeProviderMock.Object);
			}

			[Test]
			public void when_called_then_the_certificate_provider_is_invoked_with_correct_certificate_name()
			{
				certificate.UpdateUsing(certificateProviderMock.Object);
				certificateProviderMock.Verify(provider => provider.GetCertificate(), Times.Once());
			}
		}
	}
}
