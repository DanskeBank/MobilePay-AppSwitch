module MobilePay
  module PKI
    class Operations
      # SOAPAction: GetOwnCertificateList.
      #
      # This is a special case of 'CertificateStatus', so response validation
      # and handling is left to parent class.
      class GetOwnCertificateList < CertificateStatus
        REQUEST_OPERATION_NAME = 'elem:GetOwnCertificateListRequest'.freeze

        def request_message_name
          REQUEST_OPERATION_NAME
        end

        def request_message_contents(customer_id, request_id, timestamp, environment)
          @environment = environment

          {
            'elem:KeyGeneratorType' => key_generator_type,
            'elem:CustomerId' => customer_id,
            'elem:Timestamp' => timestamp,
            'elem:RequestId' => request_id
          }
        end

        protected

        # SOAP Operation name (Symbol).
        def name
          :get_own_certificate_list
        end
      end
    end
  end
end
