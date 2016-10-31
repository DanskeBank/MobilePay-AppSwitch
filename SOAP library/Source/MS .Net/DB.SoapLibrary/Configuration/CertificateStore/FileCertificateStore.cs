using System.Security.Cryptography.X509Certificates;
using DB.SoapLibrary.Configuration.AuthorisedClient;
using System;

namespace DB.SoapLibrary.Configuration.CertificateStore
{
	public class FileCertificateStore : ICertificateStore
	{
		private readonly AuthorisedClientElement _clientConfiguration;

		public FileCertificateStore(AuthorisedClientElement clientConfiguration)
		{
			_clientConfiguration = clientConfiguration;
		}

		public X509Certificate2 ClientEncryptionCertificate()
		{
			var clientEncryptionConfig = _clientConfiguration.CertificateElement(Owner.Client, Usage.ContentEncryption);
			return new X509Certificate2(clientEncryptionConfig.Path, clientEncryptionConfig.Password);
		}

		public X509Certificate2 ClientSignatureCertificate()
		{
			var clientSignatureConfig = _clientConfiguration.CertificateElement(Owner.Client, Usage.Signature);
			return new X509Certificate2(clientSignatureConfig.Path, clientSignatureConfig.Password);
		}

		public X509Certificate2 DBEncryptionCertificate()
		{
			var dbEncryptionConfig = _clientConfiguration.CertificateElement(Owner.DB, Usage.ContentEncryption);
			return new X509Certificate2(dbEncryptionConfig.Path);
		}

		public X509Certificate2 DBSignatureCertificate()
		{
			var dbSignPath = _clientConfiguration.CertificateElement(Owner.DB, Usage.Signature);
			return new X509Certificate2(dbSignPath.Path);
		}
	}
}
