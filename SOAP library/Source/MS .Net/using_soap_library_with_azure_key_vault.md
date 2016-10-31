# Content
<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Content](#content)
- [About](#about)
- [Prerequisites](#prerequisites)
- [Certificates](#certificates)
- [Registering an Application in Azure Active Directory](#registering-an-application-in-azure-active-directory)
- [Getting Required values](#getting-required-values)
	- [Getting the Client Id](#getting-the-client-id)
	- [Creating a Secret Key](#creating-a-secret-key)
		- [Important Note](#important-note)
- [Setting Permissions in Active Directory](#setting-permissions-in-active-directory)
- [Using Soap Library with Azure Key Vault As Certificate Provider](#using-soap-library-with-azure-key-vault-as-certificate-provider)
	- [Preparing Your Project](#preparing-your-project)
	- [App.Config Sections](#appconfig-sections)
	- [SoapEnvelopes](#soapenvelopes)
		- [AuthorisedClients](#authorisedclients)
		- [appSettings](#appsettings)
		- [Sample app.config File](#sample-appconfig-file)
	- [Sample Application](#sample-application)
		- [Important Note](#important-note)

<!-- /TOC -->

# About
This document will guide the reader through the process of registering an application in azure's active directory (AAD), managing certificates in azure's key vault through powershell cmdlets, and using the soap library together with azure.

# Prerequisites
*	It is assumed that the reader has installed and configured the [azure powershell cmdlets](https://azure.microsoft.com/da-dk/documentation/articles/powershell-install-configure/).
*	Furthermore the reader should be familiar with the [azure key vault cmdlets](https://msdn.microsoft.com/en-us/library/dn868052.aspx).
*	It is also assumed that the reader has an account on Danske Banks azure, and can log in to [azure portal](https://portal.azure.com) as well as [MobilePay Test's Azure Active Directory](https://manage.windowsazure.com/mobilepaytest.onmicrosoft.com#Workspaces/ActiveDirectoryExtension/Directory/900750e7-f71e-486d-957f-7fead99489ec/directoryDashboard), and has the credentials to manage applications there.
*	The reader must also be able to log in from a powershell using the cmdlet `Login-AzureRmAccount`.
*	The certificates needed by the soap library must be added to the key vault. See [Managing Secrets in Azure Key Vault](managing_secrets_in_azure_key_vault.md).
*	The certificate passwords for each of the two client certificates must be added to the key vault. See [Managing Secrets in Azure Key Vault](managing_secrets_in_azure_key_vault.md).

Finally the reader must have access to the `mptest-keyvault` key vault. By executing the cmdlet `Get-AzureRmKeyVault` you should get something like this:

```
...
Vault Name          : mptest-keyvault
Resource Group Name : mptest-keyvault
Location            : northeurope
Resource ID         : /subscriptions/45439748-6e69-49c7-91c0-302ad449ba3a/resourceGroups/mptest-keyvault/providers/
                      Microsoft.KeyVault/vaults/mptest-keyvault
Tags                :
...
```

# Certificates
The certificates you will be using for your application must be uploaded to the azure key vault. There are two types:

|                     Type | Description                                                                                                        |
|-------------------------:|:-------------------------------------------------------------------------------------------------------------------|
| Client side certificates | These are the certificates issued to the merchant. They contain both public and private keys.                      |
|        Bank certificates | These certificates are used for further signing and encryption of symmetric keys, and contain only the public keys |

# Registering an Application in Azure Active Directory
For an application to access the key vault to obtain secrets, or perform cryptographic operations on data, that application has to be registered in the active directory. This is used to authenticate the application and provide a token (JWT) to access the key vault.

1.  Log in to [MobilePay Test's Azure Active Directory](https://manage.windowsazure.com/mobilepaytest.onmicrosoft.com#Workspaces/ActiveDirectoryExtension/Directory/900750e7-f71e-486d-957f-7fead99489ec/directoryDashboard).
2.  Click 'Applications', and then 'Add' in the bottom menu.
3.  Select 'Add an application my organization is developing' and enter the application name.
4.  Select 'Web application and/or web API'. *Even if you are writing a console application running on your local machine this will still work*.
5.  Enter 'http://<your-application-name-here' in both URL's, and select Ok. The URL's can be changed later if needed.

The application is now created.

# Getting Required values

For your application to acquire a token from the active directory you will need two values: client id and a shared secret (shared between your application and active directory only, that is). To obtain those values you will need to log in to [MobilePay Test's Azure Active Directory](https://manage.windowsazure.com/mobilepaytest.onmicrosoft.com#Workspaces/ActiveDirectoryExtension/Directory/900750e7-f71e-486d-957f-7fead99489ec/directoryDashboard), and find the configuration page of your application.

## Getting the Client Id
The client id is easy: just copy the value from the configuration page. This value is used by the application, but we also need it to configure the key vault permissions, which we will do shortly.

## Creating a Secret Key
The secret key can be generated on the application page, under the section `keys`. After you create and save on, you can copy it to the clipboard.

### Important Note
The shared secret cannot be retrieved once you leave the configuration page. If you return to that page to get a copy of the secret, then it is too late and you will have to create a new one. So remember to copy the secret as soon as you created it.

# Setting Permissions in Active Directory
The key vault must also be configured to allow you application to perform the required operations. The cmdlet `Get-AzureRmKeyVault -vaultname mptest-keyvault` will show the current permissions in the key vault for all the tenants. The application must have `Get` access to secrets. In the this example we use the following values

| Name | Value | Description |
|----------:|:---------------------------------------|:----------------------------------------------------------------------------------------------------|
| Client Id | `9c19127e-51ae-4497-b80e-f80751c06a60` | The id of the application created in azures AD. This is also known as the *service principal name*. |


By running the following command you can set permissions for the application to `Get` secrets from the key vault:

```
Set-AzureRmKeyVaultAccessPolicy
	-VaultName 'mptest-keyvault'
	-ServicePrincipalName 9c19127e-51ae-4497-b80e-f80751c06a60
	-PermissionsToSecrets Get
```

# Using Soap Library with Azure Key Vault As Certificate Provider

## Preparing Your Project
1.	Get the WSDL for each of the functions (cancel, capture, getreservations, getstatus, refund) you will need. These can be found [here](https://github.com/DanskeBank/MobilePay-SoapServices-SDK/tree/master/Merchant%20APIs) in the github repository.
2.	Add service references to the WSDL in your project.
3.	Add the soap library nuget package to your project using `install-package DB.soaplibrary -source http://mit-package-repository:81/nuget/mobilepay` in the package manager console.

## App.Config Sections

## SoapEnvelopes
This section configures the use of encryption and encryption algorithms.

### AuthorisedClients
Add the section `AuthorisedClients` to your app.config file. This must have at least one child element called `AuthorisedClient`, as the following example shows:

```xml
<AuthorisedClients>
  <AuthorisedClient id="Azure-SYST">
	<Certificate
		owner="Client"
		usage="Signature"
		path="SYST-AppSwitch-Sign"/>
	<Certificate
		owner="Client"
		usage="ContentEncryption"
		path="SYST-AppSwitch-Crypt"/>
	<Certificate
		owner="DB"
		usage="ContentEncryption"
		path="SYST-AppSwitch-DPCRYPT" />
	<Certificate
		owner="DB"
		usage="Signature"
		path="SYST-AppSwitch-DPSIGN" />
  </AuthorisedClient>
</AuthorisedClients>
```

The `path="..."` parameters are the names of the certificates, stored as secrets in the key vault.

### appSettings
In the section `appSettings` relative paths for each of the methods (cancel, capture etc.) must be defined, as well as a root url to the soap service gateway. Furthermore some certificate store specific settings must be provided.

Name | Description
|---------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CertificateStoreType | This can be either `MSCert`, `File` or `AzureKeyVault`. See [this](https://github.com/DanskeBank/MobilePay-SoapServices-SDK/tree/master/SOAP%20library/Source/MS%20.Net) for more information. |
|   AuthorisedClientId | This is the identifier for the `AuthorisedClient` in the `AuthorisedClients` section.                                                                                                          |


If `CertificateStoreType` is `AzureKeyVault` the following additional settings must be provided as well:

Name | Description
|------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------------------------|
|                             Key Vault URL | The root URL to the key vault secrets.                                                                                                     |
|                Azure AD client identifier | The client identifier, also know as the service principal.                                                                                 |
|                    Azure AD client secret | The shared secret for your application. This must be generated in azures AD.                                                               |
| In-memory certificate life span (minutes) | Define how long each certificate must reside in memory. This is done to prevent a call to the key vault each time a certificate is needed. |


### Sample app.config File

```xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
  <configSections>
    <section name="SoapEnvelopes"
		type="DB.SoapLibrary.Configuration.SoapEnvelope.SoapEnvelopesSection, DB.SoapLibrary" />
    <section name="AuthorisedClients"
		type="DB.SoapLibrary.Configuration.AuthorisedClient.AuthorisedClientsSection, DB.SoapLibrary" />
  </configSections>

  <SoapEnvelopes>
    <SoapEnvelope direction="Outgoing">
      <Apply usage="Signature" algorithm="RSASHA1" />
      <Apply usage="KeyEncryption" algorithm="XmlEncRSA15Url" />
      <Apply usage="ContentEncryption" algorithm="XmlEncTripleDESUrl" />
    </SoapEnvelope>
    <SoapEnvelope direction="Incoming">
      <Apply usage="Signature" algorithm="RSASHA1" />
      <Apply usage="KeyEncryption" algorithm="XmlEncRSA15Url" />
      <Apply usage="ContentEncryption" algorithm="XmlEncTripleDESUrl" />
    </SoapEnvelope>
  </SoapEnvelopes>

  <AuthorisedClients>
    <AuthorisedClient id="Azure-SYST">
  	<Certificate
  		owner="Client"
  		usage="Signature"
  		path="SYST-AppSwitch-Sign"/>
  	<Certificate
  		owner="Client"
  		usage="ContentEncryption"
  		path="SYST-AppSwitch-Crypt"/>
  	<Certificate
  		owner="DB"
  		usage="ContentEncryption"
  		path="SYST-AppSwitch-DPCRYPT" />
  	<Certificate
  		owner="DB"
  		usage="Signature"
  		path="SYST-AppSwitch-DPSIGN" />
    </AuthorisedClient>
  </AuthorisedClients>

  <appSettings>
    <add key="RootUrl" value="https://syst-privatemobilepayservicesCert.danskebank.com/{0}"/>
    <add key="GetStatusV02" value="P2M/BackendAPI/GetStatusV02"/>
    <add key="CancelV01" value="P2M/BackendAPI/CancelV01"/>
    <add key="CaptureV01" value="P2M/BackendAPI/CaptureV01"/>
    <add key="RefundV02" value="P2M/BackendAPI/RefundV02"/>
    <add key="RequestPaymentV01" value="P2M/BackendAPI/RequestPaymentV01"/>
    <add key="GetStatusV03" value="P2M/BackendAPI/GetStatusV03"/>
    <add key="CancelV02" value="P2M/BackendAPI/CancelV02"/>
    <add key="CaptureV02" value="P2M/BackendAPI/CaptureV02"/>
    <add key="RefundV03" value="P2M/BackendAPI/RefundV03"/>
    <add key="GetReservationsV03" value="P2M/BackendAPI/GetReservationsV03"/>

    <!--azure keyvault config-->
    <add key="CertificateStoreType"  value="AzureKeyVault"/>
    <add key="AuthorisedClientId" value="Azure-SYST"/>
    <add key="Key Vault URL" value="https://mptest-keyvault.vault.azure.net:443/secrets"/>
    <add key="Azure AD client identifier" value="9c19127e-51ae-4497-b80e-f80751c06a60"/>
    <add key="Azure AD client secret" value="Zt4aeJFmDqlm+NW06Z+C78d1v01nBSAUwaD2HZ7Sauk="/>
    <add key="In-memory certificate life span (minutes)" value="10"/>

  </appSettings>

  <startup>
    <supportedRuntime version="v4.0" sku=".NETFramework,Version=v4.5.2" />
  </startup>
  <system.serviceModel>
    <bindings>
      <customBinding>
        <binding name="CancelV02">
          <textMessageEncoding messageVersion="Soap12" />
          <httpsTransport />
        </binding>
      </customBinding>
    </bindings>
    <client>
      <endpoint address="https://privatemobilepayservicesCert.danskebank.com/P2M/BackendAPI/CancelV02"
        binding="customBinding" bindingConfiguration="CancelV02" contract="CancelV02Service.CancelV02"
        name="CancelV02" />
    </client>
  </system.serviceModel>
</configuration>
```

## Sample Application
The following small program has a reference to the soap library, and has added a service reference to `CancelV02.wsdl`. The `app.config` from above is used as the configuration.

The program makes a cancel-request using version 2 of the method, and the result is just printed to the console. Other examples can be found in the [Db.SoapLibrary.Specification](https://github.com/DanskeBank/MobilePay-SoapServices-SDK/tree/master/SOAP%20library/Source/MS%20.Net) project available on github.

```csharp
using DB.SoapLibrary;
using SoapLibClient.CancelV02Service;
using System;
using System.Configuration;

namespace SoapLibClient
{
	class Program
	{
		static void Main(string[] args)
		{
			var rootUrl = ConfigurationManager.AppSettings["RootUrl"];
			var cancelV02Url = string.Format(rootUrl, ConfigurationManager.AppSettings["CancelV02"]);

			var senderId = "4F4741";
			var merchantid = "APPDK0074110008";
			var orderid = "DB TESTING 2015060908";
			SoapConnection<CancelV02Client, CancelV02> soapConnection =
				new SoapConnection<CancelV02Client, CancelV02>(cancelV02Url);

			CancelRequest req =
				new CancelRequest
				{
					Input = new Input
					{
						MerchantID = merchantid,
						OrderID = orderid,
						CustomerId = string.Empty,
					},
					Security = new SecurityHeaderType(),
					RequestHeader = new RequestHeaderType
					{
						Language = "DA",
						SenderId = senderId,
						SignerId1 = senderId,
						SignerId2 = senderId,
						SignerId3 = senderId,
						DBCryptId = senderId,
						RequestId = "123",
						Timestamp = DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ssZ")
					}
				};

			var output = soapConnection.Send(client => client.Cancel(ref req.Security, req.RequestHeader, req.Input));
			Console.WriteLine(
				new {
					OriginalTransactionId = output.OriginalTransactionId,
					ReasonCode =output.ReasonCode,
					ReturnCode =output.ReturnCode });
		}
	}
}
```

### Important Note
It is important that the `senderId` used in each request to the soap service gateway **must** match the technical user of the client certificate stored in azures key vault.
