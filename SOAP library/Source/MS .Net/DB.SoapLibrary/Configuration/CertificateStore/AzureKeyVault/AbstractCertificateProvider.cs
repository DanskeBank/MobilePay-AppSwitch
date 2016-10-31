using Microsoft.Azure.KeyVault;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public abstract class AbstractCertificateProvider : ICertificateProvider
	{
		readonly string url, certificateName;
		readonly KeyVaultClient keyVaultClient;

		public AbstractCertificateProvider(string url, string certificateName, KeyVaultClient keyVaultClient)
		{
			Check.that(url.isSomething()).otherwiseThrow(new ArgumentException(nameof(url)));
			Check.that(certificateName.isSomething()).otherwiseThrow(new ArgumentException(nameof(certificateName)));
			Check.that(keyVaultClient != null).otherwiseThrow(new ArgumentException(nameof(keyVaultClient)));
			this.url = url;
			this.certificateName = certificateName;
			this.keyVaultClient = keyVaultClient;
		}

		public abstract X509Certificate2 GetCertificate();

		protected byte[] GetCertificateBytes()
		{
			var identifier = url.EndsWith("/") ? url + certificateName : $"{url}/{certificateName}";
			var base64EncodedCertificate = keyVaultClient.GetSecretAsync(identifier).Result.Value;
			return Convert.FromBase64String(base64EncodedCertificate);
		}
	}
}
