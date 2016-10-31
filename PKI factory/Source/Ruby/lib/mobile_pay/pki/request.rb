module MobilePay
  module PKI
    # MobilePay::PKI API Request handling.
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

        @request = client.build_request(name, message: request_message(operation))
        @message_body = @request.body
      end

      # Encrypt the request message body.
      def encrypt!(encrypting_certs)
        @message_body = MobilePay::PKI::WSSE.new.encrypt!(encrypting_certs, @message_body)
      end

      # Add DBG PKI style signature to the request.
      def sign!(signing_certs)
        @message_body = MobilePay::PKI::WSSE.new.sign!(signing_certs, @message_body)
      end

      protected

      # PKI Request headers.
      def request_header
        {
          'RequestHeader' => {
            'SenderId' => @customer_id,
            'CustomerId' => @customer_id,
            'RequestId' => request_id,
            'Timestamp' => timestamp,
            'InterfaceVersion' => interface_version,
            'Environment' => environment
          }
        }
      end

      # Build a PKI Request message.
      def request_message(operation)
        message = request_header
        message[operation.request_message_name] = operation.request_message_contents(@customer_id, request_id, timestamp, environment)
        message
      end

      DEFAULT_ENVIRONMENT = 'production'.freeze

      def environment
        @options['pki_environment', DEFAULT_ENVIRONMENT]
      end

      DEFAULT_INTERFACE_VERSION = '1'.freeze

      def interface_version
        @options['pki_interface_version', DEFAULT_INTERFACE_VERSION]
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
