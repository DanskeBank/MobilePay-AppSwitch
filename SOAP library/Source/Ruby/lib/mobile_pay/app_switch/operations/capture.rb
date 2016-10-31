module MobilePay
  module AppSwitch
    class Operations
      # MobilePay SOAP Operation: Capture.
      class Capture < MobilePay::AppSwitch::Operations
        # SOAP Request Input element name.
        def request_input_element
          'tns:Input'.freeze
        end

        def request_input_contents
          {
            'tns:MerchantId' => merchant_id,
            'tns:OrderId' => order_id,
            'tns:BulkRef' => bulk_ref,
            'tns:Amount' => amount,
            'tns:Test' => mp_test
          }.tap do |request|
            request['tns:DateFrom'] = date_from unless date_from.blank?
            request['tns:DateTo'] = date_to unless date_to.blank?
          end
        end

        protected

        # Get `amount` from parameters.
        def amount
          params[:amount].to_f
        end

        # Get `bulk_ref` from parameters.
        def bulk_ref
          params[:bulk_ref]
        end

        # SOAP Operation name (Symbol).
        def name
          :capture
        end

        def wsdl_file
          'CaptureV02.wsdl'
        end
      end
    end
  end
end
