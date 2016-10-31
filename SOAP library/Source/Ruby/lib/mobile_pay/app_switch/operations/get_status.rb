module MobilePay
  module AppSwitch
    class Operations
      # MobilePay SOAP Operation: GetStatus.
      class GetStatus < MobilePay::AppSwitch::Operations
        # SOAP Request Input element name.
        def request_input_element
          'tns:dacGetStatusInput'.freeze
        end

        def request_input_contents
          {
            'tns:MerchantId' => merchant_id,
            'tns:OrderId' => order_id,
            'tns:ActionCode' => action_code,
            'tns:Test' => mp_test
          }.tap do |request|
            request['tns:CustomerId'] = mp_customer_id unless mp_customer_id.blank?
            request['tns:DateFrom'] = date_from unless date_from.blank?
            request['tns:DateTo'] = date_to unless date_to.blank?
          end
        end

        protected

        # SOAP Operation name (Symbol).
        def name
          :get_status
        end

        def wsdl_file
          'GetStatusV03.wsdl'
        end

        private

        # Get `ActionCode` depending on `extended_output` parameter; either 'B'(asic?) or 'E'(xtended?).
        def action_code
          !!params[:extended_output] ? 'E' : 'B'
        end
      end
    end
  end
end
