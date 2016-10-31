module MobilePay
  module AppSwitch
    class Operations
      # MobilePay SOAP Operation: GetReservations.
      class GetReservations < MobilePay::AppSwitch::Operations
        # SOAP Request Input element name.
        def request_input_element
          'tns:dacGetReservationsInput'.freeze
        end

        def request_input_contents
          {
            'tns:ActionCode' => action_code,
            'tns:MerchantId' => merchant_id,
            'tns:CustomerId' => mp_customer_id,
            'tns:Test' => mp_test
          }.tap do |request|
            request['tns:TimeStart'] = date_from unless date_from.blank?
            request['tns:TimeEnd'] = date_to unless date_to.blank?
          end
        end

        protected

        # Format a `date` in required format; `YYMMDD`.
        def format_date(date)
          date.to_time.utc.strftime '%Y-%m-%dT%H:%M'
        end

        # SOAP Operation name (Symbol).
        def name
          :get_reservations
        end

        def action_code
          params[:action_code]
        end

        def wsdl_file
          'GetReservationsV01.wsdl'
        end
      end
    end
  end
end
