module MobilePay
  module AppSwitch
    class Operations
      # MobilePay SOAP Operation: Cancel.
      class Cancel < MobilePay::AppSwitch::Operations
        # SOAP Request Input element name.
        def request_input_element
          'tns:Input'.freeze
        end

        def request_input_contents
          {
            'tns:MerchantID' => merchant_id,
            'tns:OrderID' => order_id,
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
          :cancel
        end

        def wsdl_file
          'CancelV02.wsdl'
        end
      end
    end
  end
end
