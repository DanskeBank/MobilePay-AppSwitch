using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.KeyVault;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public class ClientCertificateProvider : AbstractCertificateProvider
	{
		readonly string password;

		public ClientCertificateProvider(string url, string certificateName, KeyVaultClient keyVaultClient) : base(url, certificateName, keyVaultClient)
		{
			var certificatePasswordName = certificateName + "-password";
			var identifier = url.EndsWith("/") ? url + certificatePasswordName : $"{url}/{certificatePasswordName}";
			password = keyVaultClient.GetSecretAsync(identifier).Result.Value;
		}

		public override X509Certificate2 GetCertificate()
		{
			return new X509Certificate2(rawData: GetCertificateBytes(), password: password, keyStorageFlags: X509KeyStorageFlags.Exportable | X509KeyStorageFlags.MachineKeySet);
		}
	}
}
