module MobilePay
  module PKI
    class Operations
      # SOAPAction: RevokeCertificate.
      #
      # Revoke Client Encryption and Signing keystore pair.
      #
      # Note: This may in the future delete the PKCS#12 generated keystores defined in options.
      class RevokeCertificate < MobilePay::PKI::Operations
        REQUEST_OPERATION_NAME = 'elem:RevokeCertificateRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(customer_id, request_id, timestamp, environment)
		@environment = environment
          {
            'elem:KeyGeneratorType' => key_generator_type,
            'elem:CustomerId' => customer_id,
            revocation_element => certificate_serial_no,
            'elem:CRLReason' => crl_reason,
            'elem:Timestamp' => timestamp,
            'elem:RequestId' => request_id,
            'elem:Environment' => environment
          }
        end

        def handle_response
          # Maybe delete the keystore file for revoked certificate?
          # But we may still get a partial response back even if failed.
        end

        protected

        def verify_response!
		  puts("Revoked:")
          revoked_certificates_serial_no.each do |serial_no|
			puts(serial_no)
            certificate = all_certs.detect { |cert| cert.serial.to_s == serial_no }

            #certificate || fail(MobilePay::InvalidCertificateError, "Unknown revoked Certificate Serial No: #{serial_no}!")
          end
        end

        # SOAP Operation name (Symbol).
        def name
          :revoke_certificate
        end

        private

        # Get Certificate Serial No from parameters, or blank if not provided.
        def certificate_serial_no
          params[:certificate_serial_no] ? params[:certificate_serial_no] : ''
        end

        # Get CRL Reason code from parameters.
        #
        # See 'PKI Service Document' Appendix C for a complete list of CRL Reason codes.
        # https://danskebank.com/en-uk/ci/Products-Services/Transaction-Services/Online-Services/Pages/PKI-Services.aspx
        def crl_reason
          params[:crl_reason]
        end

        # Revocation element in request message, depending on optional Certificate Serial No parameter.
        def revocation_element
          revoke_all? ? 'elem:RevokeAll' : 'elem:CertificateSerialNo'
        end

        def revoke_all?
          certificate_serial_no.blank?
        end

        # Get list of revoked certificate serial number(s), or all.
        def revoked_certificates_serial_no
          serial_no = revoke_all? ? all_certs.map(&:serial).map(&:to_s) : (response_body_contents[:certificate_serial_no] || [])
          serial_no.is_a?(Array) ? serial_no : [serial_no]
        end

        def all_certs
          [encryption_certs.cert, signing_certs.cert]
        end
      end
    end
  end
end
