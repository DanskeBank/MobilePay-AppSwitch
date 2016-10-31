module MobilePay
  module PKI
    class Operations
      # SOAPAction: GetBankCertificate.
      #
      # Create new Client Encryption and Signing keystore pair.
      #
      # Note: Overwrites the BANK Encryption/Signing Certificates defined in Options, if response contains updated certificates.
      class GetBankCertificate < MobilePay::PKI::Operations
        REQUEST_OPERATION_NAME = 'elem:GetBankCertificateRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(_, request_id, timestamp, _environment)
          {
            'elem:BankRootCertificateSerialNo' => bank_root_certificate.serial.to_s,
            'elem:Timestamp' => timestamp,
            'elem:RequestId' => request_id
          }
        end

        def handle_response
          save_bank_encryption_cert
          save_bank_signing_cert
        end

        protected

        def verify_response!
          verify_bank_root_cert
          verify_bank_encryption_cert
          verify_bank_signing_cert
        end

        # SOAP Operation name (Symbol).
        def name
          :get_bank_certificate
        end

        # This request should not be signed.
        def sign_request?
          false
        end

        private

        def save_bank_encryption_cert
          cert_helper.save(bank_encryption_cert_path, bank_encryption_cert)
        end

        def save_bank_signing_cert
          cert_helper.save(bank_signing_cert_path, bank_signing_cert)
        end

        # Verify Bank Root Certificate is the exact same as we have locally.
        def verify_bank_root_cert
          if bank_root_certificate.to_der != bank_root_cert.to_der
            cert_helper.save("#{bank_root_cert_path}.err", certificate)

            fail MobilePay::InvalidCertificateError, 'Bank Root Certificate has changed!  Download latest from a trusted source and install manually.'
          end
        end

        # Verify Bank Encryption Certificate was signed by Bank Root Certificate.
        def verify_bank_encryption_cert
          return if cert_helper.verify(bank_encryption_cert, bank_root_certificate)

          fail MobilePay::InvalidCertificateError, 'Bank Encryption Certificate not signed by Bank Root Certificate!'
        end

        # Verify Bank Signing Certificate was signed by Bank Root Certificate.
        def verify_bank_signing_cert
          return if cert_helper.verify(bank_signing_cert, bank_root_certificate)

          fail MobilePay::InvalidCertificateError, 'Bank Signing Certificate not signed by Bank Root Certificate!'
        end

        #
        # Certificates from response body parts.
        #

        def bank_encryption_cert
          @bank_encryption_cert ||= cert_helper.parse(bank_encryption_cert_text)
        end

        def bank_encryption_cert_text
          response_body_contents[:bank_encryption_cert]
        end

        def bank_signing_cert
          @bank_signing_cert ||= cert_helper.parse(bank_signing_cert_text)
        end

        def bank_signing_cert_text
          response_body_contents[:bank_signing_cert]
        end

        def bank_root_cert
          @bank_root_cert ||= cert_helper.parse(bank_root_cert_text)
        end

        def bank_root_cert_text
          response_body_contents[:bank_root_cert]
        end

        # Override response handler to allow getting bank encryption and signing certificates.
        def response_handler
          super.tap(&:skip_verify_signing_certificate_file!)
        end
      end
    end
  end
end
