module MobilePay
  module PKI
    # MobilePay::PKI API Response handling.
    class Response
      include BankCertificates

      attr_accessor :request_id

      def initialize(options)
        @options = options
      end

      # Get the SOAP Eesponse's Body element.
      def response_body
        @response_body ||= @response.body
      end

      def response_body_contents
        response_body_out[response_body_out.keys[1]]
      end

      def verify!(response, customer_id, request_id)
        @response = response
        @request_id = request_id

        raise_on_fault!
        verify_response_header(customer_id, request_id)
        verify_signature
      end

      # To allow bootstrapping bank certificates we may disable validating existing files.
      # Note: This is to be used from GetBankCertificate only!
      def skip_verify_signing_certificate_file!
        @skip_verify_signing_certificate_file = true
      end

      # We need to skip file-validation to allow boot-strap using GetBankCertificate SOAP Operation.
      def skip_verify_signing_certificate_file?
        @skip_verify_signing_certificate_file
      end

      protected

      # Check response for error/SOAP Fault,
      # and raise exeption with relevant fields set.
      def raise_on_fault!
        fail MobilePay::SOAPFault.new(fault_return_code, fault_return_text, fault_additional_return_text) if fault_response?
        fail MobilePay::SOAPFault.new(soap_fault_code, soap_fault_text, soap_fault_additional_text) if soap_fault?
        fail @response.http_error if http_error?
        fail MobilePay::SOAPFault.new(error_return_code, error_return_text, error_additional_return_text) if error_response?
      end

      def signature_verifier
        @signature_verifier ||= MobilePay::PKI::WSSE::VerifySignature.new(@response)
      end

      # Verify the response header has expected values.
      def verify_response_header(customer_id, request_id)
        response_header = response_body_out.values.first

        response_header[:sender_id] == customer_id || fail(Error, "Unexpected ResponseHeader value for SenderId, expected: #{customer_id}, got: #{response_header[:sender_id]}")
        response_header[:customer_id] == customer_id || fail(Error, "Unexpected ResponseHeader value for CustomerId, expected: #{customer_id}, got: #{response_header[:customer_id]}")
        response_header[:request_id] == request_id.to_s || fail(Error, "Unexpected ResponseHeader value for RequestId, expected: #{request_id}, got: #{response_header[:request_id]}")
        valid_response_time_range.cover?(response_header[:timestamp].to_time) || fail(Error, "Unexpected ResponseHeader value for Timestamp: #{response_header[:timestamp]}")
        response_header[:interface_version] == interface_version.to_s || fail(Error, "Unexpected ResponseHeader value for SenderId, expected: #{interface_version}, got: #{response_header[:interface_version]}")
      end

      # Verify the response signature.
      # The response MUST be signed by bank's signing certificate.
      def verify_signature
        signature_verifier.verify!
        verify_signing_certificate!(signature_verifier.certificate)
        skip_verify_signing_certificate_file? || verify_signing_certificate_file!(signature_verifier.certificate)
      end

      private

      def response_body_out
        response_body.values.first
      end

      def error_response?
        error_return_code != '00'
      end

      def error_return_code
        response_body_contents[:return_code]
      end

      def error_return_text
        response_body_contents[:return_text]
      end

      def error_additional_return_text
        response_body_contents[:additional_return_text]
      end

      def fault_response
        response_body[:pki_factory_service_fault]
      end

      def fault_response?
        !!fault_response # || response_body[:return_code] != '00'
      end

      def fault_return_code
        fault_response && fault_response[:return_code]
      end

      def fault_return_text
        fault_response && fault_response[:return_text]
      end

      def fault_additional_return_text
        fault_response && fault_response[:additional_return_text]
      end

      def http_error?
        !!@response.http_error
      end

      def soap_fault?
        @response.soap_fault?
      end

      def soap_fault_code
        response_body[:fault][:detail][:pki_factory_service_fault][:return_code]
      end

      def soap_fault_text
        response_body[:fault][:faultstring]
      end

      def soap_fault_additional_text
        response_body[:fault][:detail][:pki_factory_service_fault][:additional_return_text]
      end

      # Verify signing certificate is bank signing certificate.
      #
      # The Response message contains its signing certificate in the
      # `//ds:Signature/ds:KeyInfo/ds:X509Data/ds:X509Certificate` element.
      #
      # Raises an Exception if signing certificate is not our current cert.
      def verify_signing_certificate!(certificate)
        cert_helper.verify(certificate, bank_root_certificate) || fail(MobilePay::InvalidCertificateError, 'Message signing certificate not signed by Bank Root Certificate!')
      end

      # Verify signing certificate is out currently known bank signing certificate.
      #
      # Raises an Exception if signing certificate is not our current cert.
      def verify_signing_certificate_file!(certificate)
        certificate.to_der == bank_signing_certificate.to_der || fail(MobilePay::InvalidCertificateError, 'Message signing certificate does not match current Bank Signing Certificate!')
      end

      DEFAULT_INTERFACE_VERSION = '1'.freeze

      def interface_version
        @options['pki_interface_version', DEFAULT_INTERFACE_VERSION]
      end

      # How far from now we accept time stamps
      VALID_RESPONSE_TIME_DIFF = 62 * 60 # seconds

      def valid_response_time_range
        now = Time.now.utc
        (now - VALID_RESPONSE_TIME_DIFF)..(now + VALID_RESPONSE_TIME_DIFF)
      end
    end
  end
end
