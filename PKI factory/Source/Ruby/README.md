# PKI factory

This folder contains example implementations in Ruby on how to interact with Danske Bank's PKI Factory.

The [MobilePay AppSwitch API implementation guide](https://github.com/DanskeBank/MobilePay-AppSwitch/blob/master/MobilePay%20AppSwitch%20API%20Implementation%20Guide.pdf "MobilePay AppSwitch API implementation guide") describes the PKI integration (chapter 4).

When using the obtained certificates in interaction with the Danske Bank API, please consider how the certificates are stored. The test app uses the windows Cert Store, but further security meassures may be in order. An HSM setup may be appropriate; however, the library is currently not supporting this.

# MobilePay::Pki

This Ruby gem implements a PKI Client for the Danske Bank PKI Factory service.
The PKI Factory service enables bank customers to manage certificates for use in secure communication with the bank.

Before using the PKI Client and the MobilePay AppSwitch client, it is strongly advised you are familiar with [MobilePay AppSwitch API Implementation Guide](https://github.com/DanskeBank/MobilePay-AppSwitch/blob/master/MobilePay%20AppSwitch%20API%20Implementation%20Guide.pdf).

## Installation

You need to have Ruby version 2.0 or later installed.  On Linux/UNIX, you can use the system's package manager or third-party tools (_chruby_ with _ruby-install_ or _rbenv_ / _rvm_).
On Windows machines, you can use _RubyInstaller_.
See [Installing Ruby](https://www.ruby-lang.org/en/documentation/installation/) for further information.

First clone the git repository to your local file system:

      $ git clone git@github.com:DanskeBank/MobilePay-AppSwitch.git --depth 1

Add this line to your application's Gemfile:

```ruby
gem 'mobile_pay-pki', path: './MobilePay-AppSwitch/PKI factory/Source/Ruby'
```

And then execute:

      $ bundle install

Or download the latest ruby gem from [mobile_pay-pki](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/PKI%20factory/Source/Ruby/pkg) install it yourself as:

      $ gem install ./mobile_pay-pki-1.0.0

## Usage

The PKI client part of this gem implements PKI [Encryption and Signing](http://www.danskebank.com/en-uk/ci/Products-Services/Transaction-Services/Online-Services/Integration-Services/Documents/ChannelsAndSecurity/Channel_WebService/EncryptionSigningCompressionWebServices.pdf) necessary to access the Danske Bank PKI Factory.

Check prerequisite in _MobilePay AppSwitch API Implementation Guide_ section 4: "PKI integration", and ensure you have the customer number and PIN code from your MobilePay agreement at hand to create your encryption and signing certificates.
WARNING! 
        User PIN can only be used once during initial certificate issuing.
        User PIN will be invalidated and account locked if wrong PIN is entered 4 times 
		
You need the following steps to get started:

1. Go to [Danske Bank PKI Services](https://danskebank.com/en-uk/ci/Products-Services/Transaction-Services/Online-Services/Pages/PKI-Services.aspx) to download the latest _PKI Service Documentation for Danske Bank Group_ and current _root certificate for Danske Bank Group_.

    1.1. Extract both zip files to a suitable location and look for the bank root certificate and WSDL files.

    1.2. Now generate a configuration file in [YAML](https://en.wikipedia.org/wiki/YAML) format from the skeleton below, exchanging the paths and file names to match the location where Bank Root Certificate and WSDL files are installed, including a secure location for the client keystore pairs (secured by a strong password):

    Sample __options.yml__ file:

        ---
        mobile_pay:
          bank_root_certificate_path: "./config/DBGROOT_1111110002/DBGROOT_1111110002.cer"
          bank_encryption_certificate_path: "./config/DBG_encrypt.cer"
          bank_signing_certificate_path: "./config/DBG_sign.cer"

          client_generated_encryption_keystore_path: "./config/ClientGeneratedEncryption.pfx"
          client_verified_encryption_keystore_path: "./config/ClientVerifiedEncryption.pfx"
          client_generated_signing_keystore_path: "./config/ClientGeneratedSigning.pfx"
          client_verified_signing_keystore_path: "./config/ClientVerifiedSigning.pfx"
          client_keystore_password: "some-secure-password"
          customer_id: "your-customer-number"

          merchant_id: "your-merchant-id"
          mp_language: "DK"
          mp_test: "N" # set to "Y" for testing

          pki_environment: "production" # or "customertest" for pre-production testing
          pki_interface_version: "1"
          pki_key_generator: "software"
          pki_wsdl_uri: "./config/pki/PKIService.wsdl"

    The options `client_keystore_password`, `customer_id` and `merchant_id` may alse be provided as parameters to each request for a more secure set-up.

    **Note:** Please ensure you keep an up-to-date back-up of the certificate and keystore files!

2. If you have already retrieved the Bank encryption and signing certificates, edit `options.yml` accordingly; else you may bootstrap the set-up using following command in `irb` interactive ruby shell:

          require 'mobile_pay/pki'
          response = MobilePay::PKI.get_bank_certificate(options_file: '/path/to/options.yml')

    or, if `customer_id` is left out of options file:

          require 'mobile_pay/pki'
          response = MobilePay::PKI.get_bank_certificate(options_file: '/path/to/options.yml', customer_id: 'your-customer-number')

3. Generating a client encryption/signing PKCS#12 keystore pair may be done either using `makecert` or `openssl` command line tools, or the following convenience method:

        require 'mobile_pay/pki'
        MobilePay::PKI.create_client_certificates(options_file: '/path/to/options.yml', name: '/CN=YourOrganization/DC=Something')

4. The generated keystore pair must now be verified and signed by Danske Bank PKI Factory before they are ready to use in MobilePay AppSwitch API;

        require 'mobile_pay/pki'
        response = MobilePay::PKI.create_certificate(options_file: '/path/to/options.yml', pin: 1234)

    or, if you prefer leaving customer key and password out of the configuration file:

        require 'mobile_pay/pki'
        response = MobilePay::PKI.create_certificate(options_file: '/path/to/options.yml', customer_id: 'your-customer-id', client_keystore_password: 'some-secure-password', pin: 1234)

    The signed (verified) certificates are stored in the corresponding client verified signing/encryption keystore specified in configuration.

    **Note:** This operation will overwrite the verified client keystore pair specified in options, so be sure the application is using its own copy of the certificates!

    If you already have the pairs of certificates and private keys, you may skip step 3 and 4 and invoke `renew_certificate` to generate a new certificate pair specific for this application.

5. For renewal, and revocation of a certificate pair, use the methods `renew_certificate` and `revoke_certificate` respectively.

    **Note:** As `create_certificate` operation, the `renew_certificate` operation will overwrite the verified client keystore pair!

See the documentation for each method in `lib/mobile_pay/pki.rb` for further examples.

**Note:** It is *required* you check the bank root certificate CRL on a regular (daily) basis; e.g. by validating the returned CRL object from `MobilePay::PKI.check_crl` convenience method.

It is also *strongly advised* to periodically call `get_bank_certificate` to ensure bank certificates are kept up to date; and to call either `certificate_status` or `get_own_certificate_list` to check client certificate status in Danske Bank PKI Factory.

## Development

After checking out the repo, run `bin/setup` to install dependencies. Then, run `rake spec` to run the tests. You can also run `bin/console` for an interactive prompt that will allow you to experiment.

To install this gem onto your local machine, run `bundle exec rake install`. To release a new version, update the version number in `version.rb`, and then run `bundle exec rake release`, which will create a git tag for the version, push git commits and tags, and push the `.gem` file to [rubygems.org](https://rubygems.org).

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/DanskeBank/MobilePay-AppSwitch. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

See the LICENSE.txt file.
