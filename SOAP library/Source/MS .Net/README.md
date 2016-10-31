# About Danske Bank's SOAP library

In this code base you will find:
+ a library to interact with Danske Bank's service gateway.
+ a specification of how to use the SOAP connection implemented as unit tests.

# Configuration
In order to use the library a tiny bit of configuration is needed in order to load the correct certificate store.

There are currently 3 different certificate stores to choose from:

1.  Local Windows certificate store
2.  The file system
3.  Microsoft Azure's key vault

## Local Windows Certificate Store
If you have installed the certificates in the local machine's certificate store (run `certmgr.msc` to see) you can configure the application to look in there for the certificates.

```xml
<configuration>
    ...
    <appSettings>
        <add key ="CertificateStoreType" value="MSCert"/>
        <add key ="CertificateStoreId" value="DanskeBank.PKIFactory"/>
    </appSettings>
    ...
</configuration>
```

Note however that the certificate must have specific *friendly names* for this to work, and they must be stored in the certificate store with the logical name `DanskeBank.PKIFactory`.

|                   Certificate | Friendly name            |
|------------------------------:|:-------------------------|
|         Bank root certificate | `BankRoot`               |
|   Bank encryption certificate | `BankEncryption`         |
|    Bank signature certificate | `BankSigning`            |
| Client encryption certificate | `ClientIssuedEncryption` |
|  Client signature certificate | `ClientIssuedSigning`    |

## The File System
You can also elect to use certificates stored in the file system. To do this you must configure your application in the following way:

```xml
<configuration>
 <AuthorisedClients>
    <!-- SYST configs -->
    <AuthorisedClient id="061133">
      <Certificate owner="Client" usage="Signature" path="Certificates/SYST/061133/Signing.pfx" password="Qwe123123" />
      <Certificate owner="Client" usage="ContentEncryption" path="Certificates/SYST/061133/Encryption.pfx" password="Qwe123123" />
      <Certificate owner="DB" usage="ContentEncryption" path="Certificates/SYST/dpcrypt.cer" />
      <Certificate owner="DB" usage="Signature" path="Certificates/SYST/dpsign.cer" />
    </AuthorisedClient>
  </AuthorisedClients>

  <appSettings>
    <!--file config-->
    <add key ="CertificateStoreType" value="File"/>
    <add key ="CertificateStoreId" value="061133"/>
  </appSettings>
</code>
```

Examples of both usages (file system and local machines certificate store) can be foud in the _app.config_ file in the `Db.SoapLibrary.Specification` project.

## Microsoft Azure Key Vault
As of version 1.1 of `DB.SoapLibrary` it is also possible to use Microsoft Azure's key vault to store certificates. The configuration of `DB.SoapLibrary` and management of certificates is a bit more convoluted, and is described in separate documents.

 -  [Managing secrets in azure key vault](managing_secrets_in_azure_key_vault.md)
 -  [Using `DB.SoapLibrary` with azure key vault](using_soap_library_with_azure_key_vault.md)
