using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public interface ICertificateProvider
	{
		X509Certificate2 GetCertificate();
	}
}
