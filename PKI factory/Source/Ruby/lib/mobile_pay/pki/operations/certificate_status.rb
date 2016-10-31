module MobilePay
  module PKI
    class Operations
      # SOAPAction: CertificateStatus.
      class CertificateStatus < MobilePay::PKI::Operations
        REQUEST_OPERATION_NAME = 'elem:CertificateStatusRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(customer_id, request_id, timestamp, environment)
          @environment = environment

          {
            'elem:KeyGeneratorType' => key_generator_type,
            'elem:CertificateSerialNo' => certificate_serial_no,
            'elem:CustomerId' => customer_id,
            'elem:Timestamp' => timestamp,
            'elem:RequestId' => request_id
          }
        end

        def handle_response
          response_body_contents[:certificate_status]
        end

        protected

        def verify_response!
          response_status = response_body_contents[:certificate_status]
          response_status = [response_status] unless response_status.is_a?(Array)

          response_status.each do |certificate_status|
            verify_certificate_serial_no!(certificate_status) unless customer_test?
            verify_certificate_status!(certificate_status)
          end
        end

        # SOAP Operation name (Symbol).
        def name
          :certificate_status
        end

        private

        def certificate_serial_no
          params[:certificate_serial_no] ? params[:certificate_serial_no] : client_certificates_serial_no
        end

        def client_certificates_serial_no
          [encryption_verified_keystore, signing_verified_keystore].map(&:load).map(&:certificate).map(&:serial).map(&:to_s)
        end

        def customer_test?
          @environment == 'customertest'
        end

        def encryption_certificate
          @encryption_certificate ||= encryption_verified_keystore.load.certificate
        end

        def encryption_certificate_serial_no
          encryption_certificate.serial.to_s
        end

        def signing_certificate
          @signing_certificate ||= signing_verified_keystore.load.certificate
        end

        def signing_certificate_serial_no
          signing_certificate.serial.to_s
        end

        def verify_certificate_serial_no!(certificate_status)
          certificate_type = certificate_status[:certificate_type]
          status = certificate_status[:status].keys.first
		  
		  puts("Certificate: #{certificate_status[:certificate_serial_no]}, status: #{status.upcase}")

          case certificate_type
          when 'encryption'
            if certificate_status[:certificate_serial_no] == encryption_certificate_serial_no
              certificate_status[:matching_certificate_serial_no] == signing_certificate_serial_no || warn("Unknown encryption certificate matching serial no; expected: #{signing_certificate_serial_no}, got: #{certificate_status[:matching_certificate_serial_no]}.")
            else
              status == :revoked || warn("Unknown encryption certificate serial no; expected: #{encryption_certificate_serial_no}, got: #{certificate_status[:certificate_serial_no]}.")
            end
            encryption_certificate
          when 'signing'
            if certificate_status[:certificate_serial_no] == signing_certificate_serial_no
              certificate_status[:matching_certificate_serial_no] == encryption_certificate_serial_no || warn("Unknown signing certificate matching serial no; expected: #{encryption_certificate_serial_no}, got: #{certificate_status[:matching_certificate_serial_no]}.")
            else
              status == :revoked || warn("Unknown signing certificate serial no; expected: #{signing_certificate_serial_no}, got: #{certificate_status[:certificate_serial_no]}.")
            end
            signing_certificate
          else
            fail MobilePay::InvalidCertificateError, "Unknown certificate type: '#{certificate_type}'"
          end
        end

        def verify_certificate_status!(certificate_status)
          status = certificate_status[:status].keys.first
          [:good, :expires_soon, :revoked].include?(status) || fail(MobilePay::InvalidCertificateError, "Certificate status: '#{status}' for serial no: #{certificate_status[:certificate_serial_no]}")
        end
      end
    end
  end
end
