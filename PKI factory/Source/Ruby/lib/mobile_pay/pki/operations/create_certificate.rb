module MobilePay
  module PKI
    class Operations
      # SOAPAction: CreateCertificate.
      #
      # Create new Client Encryption and Signing Key/Certificate pair from PIN.
      #
      # This is a special case of `RenewCertificate`, so response verification
      # and handling is left to parent class.
      #
      # Note: Overwrites the PKCS#12 generated keystores defined in options.
      class CreateCertificate < RenewCertificate
        REQUEST_OPERATION_NAME = 'elem:CreateCertificateRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(customer_id, request_id, timestamp, environment)
          super.merge(
            'elem:PIN' => pin
          )
        end

        protected

        # SOAP Operation name (Symbol).
        def name
          :create_certificate
        end

        # This request must be encrypted.
        def encrypt_request?
          true
        end

        # This request should not be signed.
        def sign_request?
          false
        end

        private

		def encryption_certificate
          encryption_generated_keystore.load.certificate
        end

        def encryption_private_key
          encryption_generated_keystore.tap(&:load).private_key
        end

        def signing_certificate
          signing_generated_keystore.load.certificate
        end

        def signing_private_key
          signing_generated_keystore.tap(&:load).private_key
        end

        # Get pin from parameters.
        def pin
          params[:pin]
        end
      end
    end
  end
end
