module MobilePay
  module PKI
    class Operations
      # SOAPAction: RenewCertificate.
      #
      # Renew Client Encryption and Signing keystore pair.
      #
      # Note: Overwrites the PKCS#12 generated keystores defined in options, if response contains updated certificates.
      class RenewCertificate < MobilePay::PKI::Operations
        REQUEST_OPERATION_NAME = 'elem:RenewCertificateRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(customer_id, request_id, timestamp, environment)
          verify_keystore_permissions!

          {
            'elem:CustomerId' => customer_id,
            'elem:KeyGeneratorType' => key_generator_type,
            'elem:EncryptionCertPKCS10' => Base64.encode64(pkcs10(encryption_certificate, encryption_private_key)).delete("\n"),
            'elem:SigningCertPKCS10' => Base64.encode64(pkcs10(signing_certificate, signing_private_key)).delete("\n"),
            'elem:Timestamp' => timestamp,
            'elem:RequestId' => request_id,
            'elem:Environment' => environment
          }
        end

        # Update encryption and signing keystore with new certificates and generated key.
        #
        # Note: This won't work in `customertest` as returned certificates are fixed.
        def handle_response
          encryption_verified_keystore.update(encryption_private_key, encryption_cert, [ca_cert])
          signing_verified_keystore.update(signing_private_key, signing_cert , [ca_cert])
        end

        protected

        # Safeguard; ensure write permission on PKCS#12 keystore files.
        def verify_keystore_permissions!
          verify_writable_file! encryption_verified_keystore_path, 'client_verified_encryption_keystore_path'
          verify_writable_file! signing_verified_keystore_path, 'client_verified_signing_keystore_path'
        end

        # Verify write permissions on file or path if file does not exist.
        def verify_writable_file!(path, type)
          if File.exist?(path)
            fail "Permission denied: #{type} - '#{path}'" unless File.writable?(path)
            fail "Is a directory: #{type} - '#{path}'" if File.directory?(path)
          else
            dir = File.dirname(path)
            fail "Is not a directory: #{type} - '#{dir}'" unless File.directory?(dir)
            fail "Permission denied: #{type} - '#{dir}'" unless File.writable?(dir)
          end
        end

        # Verify CA Chain for returned certificates.
        #
        # Note: This won't work in `customertest` as returned certificates are fixed.
        def verify_response!
          verify_ca_cert
          verify_encryption_cert
          verify_signing_cert
        end

        # SOAP Operation name (Symbol).
        def name
          :renew_certificate
        end

        # This request must be encrypted.
        def encrypt_request?
          true
        end

        private

        def pkcs10(certificate, key)
          csr = OpenSSL::X509::Request.new
          csr.version = 0
          csr.subject = certificate.subject
          csr.public_key = key.public_key
          csr.sign key, OpenSSL::Digest::SHA1.new
          csr.to_der
        end

        def encryption_certificate
          encryption_verified_keystore.load.certificate
        end

        def encryption_private_key
          encryption_verified_keystore.tap(&:load).private_key
        end

        def signing_certificate
          signing_verified_keystore.load.certificate
        end

        def signing_private_key
          signing_verified_keystore.tap(&:load).private_key
        end

        # Verify CA Signing Certificate was signed by Bank Certificate.
        def verify_ca_cert
          return if cert_helper.verify(ca_cert, bank_root_certificate)

          # fail MobilePay::InvalidCertificateError, 'Bank CA Certificate not signed by Bank Root Certificate!'
          warn 'Bank CA Certificate not signed by Bank Root Certificate!'
        end

        # Verify Encryption Certificate was signed by CA Certificate.
        def verify_encryption_cert
          return if cert_helper.verify(encryption_cert, ca_cert)

          # fail MobilePay::InvalidCertificateError, 'Encryption Certificate not signed by Bank CA Certificate!'
          warn 'Encryption Certificate not signed by Bank CA Certificate!'
        end

        # Verify Signing Certificate was signed by CA Certificate.
        def verify_signing_cert
          return if cert_helper.verify(signing_cert, ca_cert)

          # fail MobilePay::InvalidCertificateError, 'Signing Certificate not signed by Bank CA Certificate!'
          warn 'Signing Certificate not signed by Bank Ca Certificate!'
        end

        #
        # Certificates from response body parts.
        #

        def ca_cert
          @ca_cert ||= cert_helper.parse(ca_cert_text)
        end

        def ca_cert_text
          response_body_contents[:ca_cert]
        end

        def encryption_cert
          @encryption_cert ||= cert_helper.parse(encryption_cert_text)
        end

        def encryption_cert_text
          response_body_contents[:encryption_cert]
        end

        def signing_cert
          @signing_cert ||= cert_helper.parse(signing_cert_text)
        end

        def signing_cert_text
          response_body_contents[:signing_cert]
        end
      end
    end
  end
end
