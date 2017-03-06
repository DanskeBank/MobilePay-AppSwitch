# PKI factory

This folder contains example implementations in .NET, Java and Ruby on how to interact with Danske Bank's PKI Factory.

The [MobilePay AppSwitch API implementation guide](https://github.com/DanskeBank/MobilePay-AppSwitch/blob/master/MobilePay%20AppSwitch%20API%20Implementation%20Guide.pdf "MobilePay AppSwitch API implementation guide") describes the PKI integration (chapter 4).

When using the obtained certificates in interaction with the Danske Bank API, please consider how the certificates are stored. The test app uses the windows Cert Store, but further security meassures may be in order. An HSM setup may be appropriate; however, the libraries are currently not supporting this.

## Releases

Please take a look at the release notes and changes in the [change log](CHANGELOG.md "CHANGELOG.md file").
