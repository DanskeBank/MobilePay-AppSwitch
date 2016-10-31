PKIService.wsdl is NOT identical to the file distributed as part of the documentation.

The following changes have been made
 - Schema location have been changed from remote to local. (Allows generation of proxy classes in offline mode)

 - The CreateCertificateRequest and RenewCertificateRequest elements have had their bodies changed from xenc:EncryptedData to el:CreateCertificateRequest and el:RenewCertificateRequest.
   The library will automaticly replace these nodes with their encrypted counterparts when requests are issued to the backend.


  

