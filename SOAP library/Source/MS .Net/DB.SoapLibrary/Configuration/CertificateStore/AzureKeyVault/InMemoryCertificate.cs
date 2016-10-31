using Microsoft.Azure.KeyVault;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using DB.SoapLibrary;

namespace DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault
{
	public class InMemoryCertificate
	{
		X509Certificate2 x509;
		TimeSpan lifeSpan;
		DateTime expiresAt;
		ITimeProvider timeProvider;
		readonly object sync = new object();

		public InMemoryCertificate(X509Certificate2 x509, TimeSpan lifeSpan, ITimeProvider timeProvider)
		{
			Check.that(x509 != null).otherwiseThrow(new ArgumentNullException(nameof(x509)));
			Check.that(timeProvider != null).otherwiseThrow(new ArgumentNullException(nameof(timeProvider)));
			Check.that(lifeSpan.Ticks > 0L).otherwiseThrow(new ArgumentException(nameof(lifeSpan)));
			this.x509 = x509;
			this.timeProvider = timeProvider;
			this.lifeSpan = lifeSpan;
			expiresAt = timeProvider.Now + lifeSpan;
		}

		public bool IsExpired
		{
			get
			{
				lock (sync)
					return timeProvider.Now > expiresAt;
			}
		}

		public X509Certificate2 X509
		{
			get
			{
				lock(sync)
				{
					if (IsExpired) throw new Exception("in-memory certificate expired - use UpdateUsing to update");
					return x509;
				}
			}
		}

		public void UpdateUsing(ICertificateProvider certificateProvider)
		{
			lock (sync)
			{
				x509 = certificateProvider.GetCertificate();
				expiresAt = timeProvider.Now + lifeSpan;
			}
		}
	}
}
