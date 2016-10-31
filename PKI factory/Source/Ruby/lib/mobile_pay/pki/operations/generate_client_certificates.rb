module MobilePay
  module PKI
    class Operations
      # Generate new Client Encryption and Signing Key/Certificate sets ready for bank signing.
      #
      # Note: Overwrites the PKCS#12 generated keystores defined in options.
      class GenerateClientCertificates < MobilePay::PKI::Operations
        # Generate new client certificates to be signed by bank.
        #
        # Note: Override parent's call as we handle this operation internally.
        def call
          generate! encryption_generated_keystore
          generate! signing_generated_keystore
          true
        end

        def handle_response
          fail 'Not a SOAP Operation.'
        end

        private

        # Generate new Key/Certificate and store as PKCS#12 keystore.
        def generate!(keystore)
          keystore.generate(name)
          keystore.write
        end

        # Get `name` parameter.
        def name
          params[:name]
        end
      end
    end
  end
end
