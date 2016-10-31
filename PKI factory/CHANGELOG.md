PKIFactory Client v1.1.0 Release Notes
======================================

>**WARNING!** User PIN can only be used once during initial certificate issuing. 

Bug-fixes:

  - Removed dependency on client generated keys during renewal of issued certificates in TestApp. Renewal procedure transfers keys from client issued certificates (and fails if they are missing).

Changes:

  - .NET PKIFactory Client interface changed to use properties instead of get/set methods for these methods:
     - `Get/SetBankRootCertificate()` -> `BankRootCertificate` property.
     - `GetBankEncryptionCertificate()` -> `BankEncryptionCertificate` property.
     - `GetBankSigningCertificate()` -> `BankSigningCertificate` property.
     - `Get/SetXmlSigningService()` -> `XmlSigningService` property.
  - .NET IXmlSigningService interface changed to use properties instead of get/set methods for `Get/SetBankRootCertificate()`, changed into `BankRootCertificate` property.

Features:

  - Added backing up of issued certificates with keys before renewing them. 
  - Added command line interface for additional functionality in demo application (without commands application works as previously). The commands are:

    ```
        -create       Issues new ecryption and signing certificates.
        -renew        Renews currently issued certificates.
        -revoke [<serial> ...]
                      Revokes one or more certificates by specified serial numbers
                      in decimal format. If no serial is specified, then current
                      issued certificates are revoked.
        -revokeall [<exclude-serial> ...]
                      Revokes all issued certificates except the ones with provided
                      serial number in decimal format. If no exclude-serial is
                      specified, then current issued certificates are NOT revoked.
        -status [<serial> ...]
                      Displays status of one or more certificates by serial numbers
                      in decimal format. If no serial specified current issued
                      certificates status is shown.
        -statusall    Lists all issued certificates, active or not.
        -help         Displays this help text.
    ```

  - Java project restructured as Apache Maven project.
  - Java project demo app main method placed in \demo-src\dk\danskebank\mobilePay\demo\DemoPKIFactory.java.