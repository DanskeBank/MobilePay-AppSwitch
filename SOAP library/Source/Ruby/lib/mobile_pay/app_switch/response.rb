module MobilePay
  module AppSwitch
    # MobilePay::AppSwitch API Response handling.
    class Response < MobilePay::PKI::Response
      attr_accessor :certificate, :certs

      # Get the response body content.
      #
      # Response body looks like this:
      # {
      #   :get_status_response => {
      #     :dac_get_status_output => {
      #       <actual content>
      #     }
      #   },
      #   ...
      # }
      def response_body_contents
        response_body.values.first.values.first
      end

      def verify!(response, customer_id, request_id)
        @response = response
        @request_id = request_id

        raise_on_fault!
        verify_response_header(customer_id, request_id)
        verify_signature
        raise_on_fault_decrypted_body!
      end

      protected

      # Check response for error/SOAP Fault,
      # and raise exeption with relevant fields set.
      def raise_on_fault!
        fail MobilePay::SOAPFault.new(soap_fault_code, soap_fault_text, soap_fault_additional_text) if soap_fault?
        fail @response.http_error if http_error?
      end

      def raise_on_fault_decrypted_body!
        fail MobilePay::SOAPFault.new(fault_return_code, fault_return_text, fault_additional_return_text) if fault_response?
      end

      # Verify the response header contents.
      #
      # Example response header:
      #   <wsu:Timestamp wsu:Id="Timestamp-9671f4f1-d4a1-47a8-8dc1-6f81fd2b6ffb" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
      #     <wsu:Created>2016-02-25T19:23:04Z</wsu:Created>
      #     <wsu:Expires>2016-02-25T19:28:04Z</wsu:Expires>
      #   </wsu:Timestamp>
      def verify_response_header(_customer_id, _request_id)
        timestamp = @response.header[:security][:timestamp]
        created_ts = timestamp[:created].to_time.utc
        expires_ts = timestamp[:expires].to_time.utc

        (valid_response_time_range.first..expires_ts).cover?(created_ts) || fail(Error, "Unexpected Response Header value for Timestamp/Created: #{timestamp[:created]}")
        valid_response_time_range.cover?(expires_ts) || fail(Error, "Unexpected Response Header value for Timestamp/Expires: #{timestamp[:expires]}")
      end

      # Delegate signature verification to WSSE and set response body to decrypted response.
      def verify_signature
        decrypted_document = MobilePay::AppSwitch::WSSE.new.verify_signed_encrypted!(certificate, certs, @response)
        @response_body = document_body_hash(decrypted_document.to_xml)
      end

      private

      # Convert response xml to hash and extract body element.
      def document_body_hash(xml)
        nori_options = {
          advanced_typecasting:         true,
          convert_tags_to:              lambda { |tag| tag.snakecase.to_sym },
          strip_namespaces:             true,
          delete_namespace_attributes:  true
        }

        nori = Nori.new(nori_options)
        nori_hash = nori.parse(xml)
        envelope = nori.find(nori_hash, 'Envelope')
        nori.find(envelope, 'Body')
      end

      def fault_response
        response_body[:fault]
      end

      def fault_return_code
        fault_response && fault_response[:code]
      end

      def fault_return_text
        fault_response && fault_response[:reason][:text]
      end

      def fault_additional_return_text
        fault_response && fault_response[:detail]
      end

      def soap_fault_code
        response_body[:fault][:code]
      end

      def soap_fault_text
        response_body[:fault][:reason][:text]
      end

      def soap_fault_additional_text
        response_body[:fault][:detail]
      end
    end
  end
end
