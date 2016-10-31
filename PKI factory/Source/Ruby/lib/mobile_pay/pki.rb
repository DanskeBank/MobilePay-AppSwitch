require "mobile_pay/pki/version"
require 'mobile_pay/options'
require 'mobile_pay/pki/client_factory'
require 'mobile_pay/pki/operations'
require 'mobile_pay/pki/operations/renew_certificate'
require 'mobile_pay/pki/operations/revoke_certificate'
require 'mobile_pay/pki/operations/create_certificate'
require 'mobile_pay/pki/operations/certificate_status'
require 'mobile_pay/pki/operations/get_bank_certificate'
require 'mobile_pay/pki/operations/get_own_certificate_list'
require 'mobile_pay/pki/operations/check_crl'
require 'mobile_pay/pki/operations/generate_client_certificates'

module MobilePay
  # Ruby client for communicating with Danske Bank Group PKI Factory service.
  #
  # For reference, see DBG PKI service description:
  # https://danskebank.com/en-uk/ci/Products-Services/Transaction-Services/Online-Services/Pages/PKI-Services.aspx
  module PKI
    # Call `CreateCertificate` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.create_certificate(pin: 1234)
    #   # => (valid response)
    #   # [customertest] => OpenSSL::PKCS12::PKCS12Error: key values mismatch
    #   puts response = MobilePay::PKI.create_certificate(pin: 1122334455)
    #   # => Problem with UserId or PIN.
    def self.create_certificate(params = {})
      call_and_handle_response Operations::CreateCertificate.new(params)
    end

    # Call `RenewCertificate` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.renew_certificate
    #   # => (valid response)
    #   # [customertest] => OpenSSL::PKCS12::PKCS12Error: key values mismatch
    def self.renew_certificate(params = {})
      call_and_handle_response Operations::RenewCertificate.new(params)
    end

    # Call `RevokeCertificate` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:certificate_serial_no` :: Serial number or `Array` of serial numbers (optional; will default to client encryption and signing certificate).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.revoke_certificate(crl_reason: 9)
    #   # => (valid response)
    #   puts response = MobilePay::PKI.revoke_certificate(certificate_serial_no: 3379000000001502, crl_reason: 9)
    #   # => MobilePay::SOAPFault: Some certificates could not be revoked. The following certificates were revoked:
    #   puts response = MobilePay::PKI.revoke_certificate(certificate_serial_no: [3379000000001502, 5169000000001702, 12345678], crl_reason: 9)
    #   # => MobilePay::SOAPFault: Some certificates could not be revoked. The following certificates were revoked: 12345678
    def self.revoke_certificate(params = {})
      call_and_handle_response Operations::RevokeCertificate.new(params)
    end

    # Call `CertificateStatus` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:certificate_serial_no` :: Serial number or `Array` of serial numbers (optional; will default to client encryption and signing certificate).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.certificate_status
    #   # => (valid response)
    #   puts response = MobilePay::PKI.certificate_status(certificate_serial_no: 1123000000001500)
    #   # => MobilePay::SOAPFault: Input error: Unknown certificate serial number: 1123000000001500
    #   puts response = MobilePay::PKI.certificate_status(certificate_serial_no: [0123000000001502])
    #   # => MobilePay::SOAPFault: Input error: Unknown certificate serial number: 5703716569922
    #   puts response = MobilePay::PKI.certificate_status(certificate_serial_no: 1123000000001502)
    #   # => MobilePay::InvalidCertificateError: Certificate status: 'expired' for serial no: 1123000000001502
    #   puts response = MobilePay::PKI.certificate_status(certificate_serial_no: [2123000000001502, 3123000000001502])
    #   # => (valid response)
    def self.certificate_status(params = {})
      call_and_handle_response Operations::CertificateStatus.new(params)
    end

    # Call `GetOwnCertificateList` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.get_own_certificate_list
    #   # => (valid response)
    #   puts response = MobilePay::PKI.get_own_certificate_list(customer_id: 360817)
    #   # => MobilePay::SOAPFault: User not authorized.
    def self.get_own_certificate_list(params = {})
      call_and_handle_response Operations::GetOwnCertificateList.new(params)
    end

    # Call `GetBankCertificate` SOAP Action.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:customer_id` :: customer_id (optional; otherwise use configuration value).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   puts response = MobilePay::PKI.get_bank_certificate
    #   # => (valid response)
    def self.get_bank_certificate(params = {})
      call_and_handle_response Operations::GetBankCertificate.new(params)
    end

    # Check bank certificate CRL.
    #
    # Parameters hash:
    #   `:options_file` :: path to `options` YAML file (optional).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   crl = MobilePay::PKI.check_crl
    #   # => #<OpenSSL::X509::CRL:0x007ff5feb0b188>
    #   crl.version
    #   # => 1
    #   crl.issuer.to_s
    #   # =>  "/CN=DBGROOT/C=DK/L=Copenhagen/ST=Denmark/O=Danske Bank Group/OU=Danske Bank Group/serialNumber=611262281110002"
    #   crl.last_update
    #   # => 2016-04-03 04:01:09 UTC
    #   crl.next_update
    #   # => 2016-04-03 04:01:09 UTC
    #   crl.revoked
    #   # => []
    #   crl.extensions.map &:to_s
    #   # => ["authorityKeyIdentifier = keyid:84:FA:E5:BF:DE:91:49:66:DF:C5:8A:52:C3:B3:B7:B3:20:1C:C7:6D, "]
    def self.check_crl(params = {})
      Operations::CheckCRL.new(params).call
    end

    # Generate new client-side certificates for signing.
    #
    # Parameters hash:
    #   `:name` :: distinguished name (String or Array), e.g. '/CN=YourOrganization/DC=CustomerTest'.
    #   `:options_file` :: path to `options` YAML file (optional).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/pki'
    #   MobilePay::PKI.create_client_certificates name: '/CN=YourOrganization/DC=CustomerTest'
    #   # => true
    def self.create_client_certificates(params)
      Operations::GenerateClientCertificates.new(params).call
    end

    # Call a DBG PKI SOAP Operation and tell it to handle the response.
    def self.call_and_handle_response(operation)
      operation.call
      operation.handle_response
    end
  end
end
