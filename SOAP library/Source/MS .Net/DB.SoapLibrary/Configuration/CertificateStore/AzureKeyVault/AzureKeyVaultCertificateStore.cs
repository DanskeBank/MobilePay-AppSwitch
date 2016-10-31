using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public class AzureKeyVaultCertificateStore : ICertificateStore
	{
		readonly ICertificateProvider clientEncryptionCertificateProvider;
		readonly ICertificateProvider clientSignatureCertificateProvider;
		readonly ICertificateProvider bankEncryptionCertificateProvider;
		readonly ICertificateProvider bankSignatureCertificateProvider;
		readonly ITimeProvider timeProvider;
		readonly TimeSpan inMemLifeSpan;
		InMemoryCertificate clientEncryptionCertificate, clientSignatureCertificate, bankEncryptionCertificate, bankSigningCertificate;
		readonly object sync = new object();

		public AzureKeyVaultCertificateStore(
			ICertificateProvider clientEncryptionCertificateProvider,
			ICertificateProvider clientSignatureCertificateProvider,
			ICertificateProvider bankEncryptionCertificateProvider,
			ICertificateProvider bankSignatureCertificateProvider,
			ITimeProvider timeProvider,
			TimeSpan inMemLifeSpan)
		{
			Check.that(clientEncryptionCertificateProvider != null).otherwiseThrow(new ArgumentNullException(nameof(clientEncryptionCertificateProvider)));
			Check.that(clientSignatureCertificateProvider != null).otherwiseThrow(new ArgumentNullException(nameof(clientSignatureCertificateProvider)));
			Check.that(bankEncryptionCertificateProvider != null).otherwiseThrow(new ArgumentNullException(nameof(bankEncryptionCertificateProvider)));
			Check.that(bankSignatureCertificateProvider != null).otherwiseThrow(new ArgumentNullException(nameof(bankSignatureCertificateProvider)));
			Check.that(timeProvider != null).otherwiseThrow(new ArgumentNullException(nameof(timeProvider)));
			Check.that(inMemLifeSpan.Ticks > 0L).otherwiseThrow(new ArgumentException(nameof(inMemLifeSpan)));
			this.timeProvider = timeProvider;
			this.inMemLifeSpan = inMemLifeSpan;
			this.clientEncryptionCertificateProvider = clientEncryptionCertificateProvider;
			this.clientSignatureCertificateProvider = clientSignatureCertificateProvider;
			this.bankEncryptionCertificateProvider = bankEncryptionCertificateProvider;
			this.bankSignatureCertificateProvider = bankSignatureCertificateProvider;
		}

		public X509Certificate2 ClientEncryptionCertificate()
		{
			clientEncryptionCertificate = GetOrUpdateCertificate(clientEncryptionCertificate, clientEncryptionCertificateProvider);
			return clientEncryptionCertificate.X509;
		}

		public X509Certificate2 ClientSignatureCertificate()
		{
			clientSignatureCertificate = GetOrUpdateCertificate(clientSignatureCertificate, clientSignatureCertificateProvider);
			return clientSignatureCertificate.X509;
		}

		public X509Certificate2 DBEncryptionCertificate()
		{
			bankEncryptionCertificate = GetOrUpdateCertificate(bankEncryptionCertificate, bankEncryptionCertificateProvider);
			return bankEncryptionCertificate.X509;
		}

		public X509Certificate2 DBSignatureCertificate()
		{
			bankSigningCertificate = GetOrUpdateCertificate(bankSigningCertificate, bankSignatureCertificateProvider);
			return bankSigningCertificate.X509;
		}

		InMemoryCertificate GetOrUpdateCertificate(InMemoryCertificate cert, ICertificateProvider certificateProvider)
		{
			lock (sync)
			{
				if (cert == null)
					cert = new InMemoryCertificate(certificateProvider.GetCertificate(), inMemLifeSpan, timeProvider);
				if (cert.IsExpired)
					cert.UpdateUsing(certificateProvider);
				return cert;
			}
		}
	}
}
