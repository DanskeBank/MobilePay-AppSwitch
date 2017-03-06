# MobilePay-SoapServices-SDK
This repository contains the code and documentation for calling server-side MobilePay SOAP backend services in Danske Bank. Further documentation can be found in the [MobilePay AppSwitch API implementation guide](https://github.com/DanskeBank/MobilePay-AppSwitch/blob/master/MobilePay%20AppSwitch%20API%20Implementation%20Guide.pdf "MobilePay AppSwitch API implementation guide").

### Merchant APIs
When the certificates are in place, the merchants can start interacting with the business endpoints (the merchant APIs). This folder contains the latest versions of all WSDL files as well as descriptions of input and output fields and values. Documentation describing how to test the setup is also available here.

### PKI factory
The APIs are SOAP-based web services and the security model for this setup is proprietary and hence out-of-the-box WCF cannot be used. The central point is the so called PKI Factory. The PKI Factory is the service interface to create and renew certificates and is used to call the business endpoints. Danske Bank has developed a PKI client (.NET, Java, and Ruby) that encapsulates all communication with the PKI factory. The code, test app and documentation can be found in the PKI factory folder. The standard implementation makes it easier for merchants to do proper automated certificate administration.

NOTE: Merchants are recommended to implement automatic certificate renewal as part of the solution. This will avoid certificate expiration in the future.

### SOAP library
The Appswitch solution is built upon backend to backend SOAP communication between the merchant backend and Danske Bank’s SI interface. Danske Bank offers a standard implementation for this merchant API interaction (a SOAP Connection) together with a suite of integration tests. Both can be found in the SOAP library folder. The tests shows in detail how to use the SOAP connection from the library and how to interact with the merchant API endpoints. The folder also contains example interactions in .NET, Java, and Ruby.
