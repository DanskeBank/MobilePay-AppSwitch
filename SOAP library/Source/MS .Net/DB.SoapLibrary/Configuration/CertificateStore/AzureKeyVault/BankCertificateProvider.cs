using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Azure.KeyVault;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public class BankCertificateProvider : AbstractCertificateProvider
	{
		public BankCertificateProvider(string url, string certificateName, KeyVaultClient keyVaultClient) : base(url, certificateName, keyVaultClient)
		{
		}

		public override X509Certificate2 GetCertificate()
		{
			return new X509Certificate2(rawData: GetCertificateBytes());
		}
	}
}
