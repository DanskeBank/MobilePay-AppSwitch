module MobilePay
  module AppSwitch
    # MobilePay::AppSwitch API Request handling.
    class Request
      attr_accessor :request_id, :timestamp, :message_body

      def initialize(options)
        @options = options
      end

      # Build the request message.
      def build(operation, client, name, customer_id, request_id = nil)
        @customer_id = customer_id
        @request_id = request_id || now.to_i
        @timestamp = ts_format(now)

        request = client.build_request(name, message: request_message(operation))
        @message_body = prepend_request_header(request.body)
      end

      # Encrypt the request message body.
      def encrypt_body!(encrypting_certs)
        @message_body = MobilePay::AppSwitch::WSSE.new.encrypt_body!(encrypting_certs, @message_body)
      end

      # Add signature to the request.
      def sign_body!(signing_certs)
        @message_body = MobilePay::AppSwitch::WSSE.new.sign_body!(signing_certs, @message_body)
      end

      # Add signature to the encrypted data element.
      def sign_encrypted_body!(signing_certs)
        @message_body = MobilePay::AppSwitch::WSSE.new.sign_encrypted_body!(signing_certs, @message_body)
      end

      protected

      # Prepend <Body> element with request header xml.
      def prepend_request_header(request)
        request.sub('<env:Body>', Gyoku.xml(request_header) + '<env:Body>')
      end

      WSSE_NAMESPACE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd'.freeze
      WSU_NAMESPACE = 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd'.freeze

      # MobilePay AppSwitch SOAP Request headers.
      def request_header
        {
          'env:Header' => {
            'wsse:Security' => '',
            'ins0:RequestHeader' => {
              'ins0:SenderId' => @customer_id,
              'ins0:SignerId1' => @customer_id,
              'ins0:SignerId2' => '',
              'ins0:SignerId3' => '',
              'ins0:DBCryptId' => '',
              'ins0:RequestId' => request_id,
              'ins0:Timestamp' => timestamp,
              'ins0:Language' => mp_language
            },
            attributes!: {
              'wsse:Security' => {
                'xmlns:wsse' => WSSE_NAMESPACE,
                'xmlns:wsu' => WSU_NAMESPACE,
                'env:mustUnderstand' => true
              }
            }
          }
        }
      end

      # MobilePay AppSwitch SOAP Request message.
      def request_message(operation)
        {
          operation.request_input_element => operation.request_input_contents
        }
      end

      private

      DEFAULT_LANGUAGE = 'DK'.freeze

      # Get `mp_language` from Options.
      def mp_language
        @options['mp_language', DEFAULT_LANGUAGE]
      end

      def now
        @now ||= Time.now.utc
      end

      # Format a `Time` instance correctly for SOAP requests.
      #
      # e.g. <pkif:Timestamp>2002-05-30T09:30:10Z</pkif:Timestamp>
      def ts_format(timestamp)
        timestamp.strftime '%Y-%m-%dT%H:%M:%SZ'
      end
    end
  end
end
