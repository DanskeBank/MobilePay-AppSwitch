using System;
using System.Configuration;
using DB.SoapLibrary.Configuration.AuthorisedClient;
using DB.SoapLibrary.Configuration.CertificateStore.AzureKeyVault;
using Microsoft.Azure.KeyVault;
using Microsoft.IdentityModel.Clients.ActiveDirectory;
using System.Collections.Generic;

namespace DB.SoapLibrary.Configuration.CertificateStore
{
	public static class CertificateStoreFactory
	{
		static Lazy<ICertificateStore> fileCertificateStore = new Lazy<ICertificateStore>(() => CreateCertificateStore(CertificateStoreType.File));
		static Lazy<ICertificateStore> msCertificateStore = new Lazy<ICertificateStore>(() => CreateCertificateStore(CertificateStoreType.MSCert));

		public static ICertificateStore GetCertificateStore()
		{
			//get certificate store
			var certificateStoreType = ConfigurationManager.AppSettings.Get("CertificateStoreType");
			if (string.IsNullOrEmpty(certificateStoreType))
			{
				throw new ArgumentException("CertificateStoreType must be set");
			}
			CertificateStoreType type;
			Enum.TryParse(certificateStoreType, out type);

			switch (type)
			{
				case CertificateStoreType.AzureKeyVault:
					return CreateCertificateStore(CertificateStoreType.AzureKeyVault);
				case CertificateStoreType.File:
					return fileCertificateStore.Value;
				case CertificateStoreType.MSCert:
					return msCertificateStore.Value;
				default:
					throw new Exception("Unknown certificatestore type");
			}
		}

		static ICertificateStore CreateCertificateStore(CertificateStoreType type)
		{
			switch (type)
			{
				case CertificateStoreType.File:
					{
						var storename = ConfigurationManager.AppSettings.Get("CertificateStoreId");
						var clientConfig = ((AuthorisedClientsSection)ConfigurationManager.GetSection("AuthorisedClients")).AuthorisedClientElement(storename);
						return new FileCertificateStore(clientConfig);
					}
				case CertificateStoreType.MSCert:
					return new MsCertificateStore();
				case CertificateStoreType.AzureKeyVault:
					{
						var authorisedClientIdentifier = ConfigurationManager.AppSettings["AuthorisedClientId"];
						var clientConfig = ((AuthorisedClientsSection)ConfigurationManager.GetSection("AuthorisedClients")).AuthorisedClientElement(authorisedClientIdentifier);

						var keyVaultUrl = ConfigurationManager.AppSettings["Key Vault URL"];
						var adClientId = ConfigurationManager.AppSettings["Azure AD client identifier"];
						var adClientSecret = ConfigurationManager.AppSettings["Azure AD client secret"];
						var inMemLifeSpanInMinutes = int.Parse(ConfigurationManager.AppSettings["In-memory certificate life span (minutes)"]);

						var keyVaultClient = new KeyVaultClient(async (authority, resource, scope) =>
						{
							var authContext = new AuthenticationContext(authority);
							var clientCredential = new ClientCredential(adClientId, adClientSecret);
							var authenticationResult = await authContext.AcquireTokenAsync(resource, clientCredential);
							if (authenticationResult == null) throw new InvalidOperationException("failed to obtain JWT token");
							return authenticationResult.AccessToken;
						});
						var clientEncryptionCertProvider = CreateCertificateProvider(clientConfig, Owner.Client, Usage.ContentEncryption, keyVaultUrl, keyVaultClient);
						var clientSignatureCertProvider = CreateCertificateProvider(clientConfig, Owner.Client, Usage.Signature, keyVaultUrl, keyVaultClient);
						var bankEncryptionCertProvider = CreateCertificateProvider(clientConfig, Owner.DB, Usage.ContentEncryption, keyVaultUrl, keyVaultClient);
						var bankSignatureCertProvider = CreateCertificateProvider(clientConfig, Owner.DB, Usage.Signature, keyVaultUrl, keyVaultClient);
						var timeProvider = new SystemTimeProvider();

						return new AzureKeyVaultCertificateStore(
							clientEncryptionCertProvider,
							clientSignatureCertProvider,
							bankEncryptionCertProvider,
							bankSignatureCertProvider,
							timeProvider,
							new TimeSpan(0, inMemLifeSpanInMinutes, 0));
					}
				default:
					throw new Exception("Unknown certificatestore type");
			}
		}

		static ICertificateProvider CreateCertificateProvider(AuthorisedClientElement clientConfig, Owner owner, Usage usage, string keyVaultUrl, KeyVaultClient keyVaultClient)
		{
			var config = clientConfig.CertificateElement(owner, usage);
			switch (owner)
			{
				case Owner.Client:
					return new ClientCertificateProvider(keyVaultUrl, config.Path, keyVaultClient);
				case Owner.DB:
					return new BankCertificateProvider(keyVaultUrl, config.Path, keyVaultClient);
				default:
					throw new Exception("Unknown certificatestore type");
			}
		}
	}
}
