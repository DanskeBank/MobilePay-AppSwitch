require 'savon'
require 'mobile_pay/pki'
require 'mobile_pay/app_switch/wsse'
require 'mobile_pay/app_switch/request'
require 'mobile_pay/app_switch/response'

module MobilePay
  module AppSwitch
    # MobilePay SOAP Operations.
    #
    # Any `Operation` subclass *must* implement:
    # - `request_input_element` :: the SOAP Request Input element name
    # - `request_body_contents` :: Hash for SOAP Request Body
    # and the following protected methods:
    # - `name` :: SOAP Operation name
    # - `wsdl_file` :: file name for specific Operation's WSDL file.
    class Operations
      include MobilePay::PKI::BankCertificates

      attr_accessor :params, :response

      def initialize(params)
        @params = params
      end

      # Invoke the SOAP Operation and verify response header, signatures etc.
      def call
        @response = client.call(name) do |locals|
          request_builder.build(self, client, name, customer_id, params[:request_id])
          debug 'SOAP Request (pre sign/encrypt)', request_builder.message_body
          request_builder.sign_body!(signing_certs)
          request_builder.encrypt_body!(bank_encryption_certificate)
          request_builder.sign_encrypted_body!(signing_certs)

          debug 'SOAP Request (final)', request_builder.message_body
          locals.xml request_builder.message_body
        end

        debug 'SOAP Response', @response
        response_handler.verify!(@response, customer_id, request_builder.request_id)
        response_handler.response_body_contents
      end

      protected

      # Get SOAP Client for this Operation.
      def client
        wsdl_path = wsdl_uri

        Savon.client do
          wsdl wsdl_path

          # Add missing namespace(s)
          # namespaces('xmlns:wsse' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'xmlns:wsu' => 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd')

          # Handle errors ourselves; otherwise we just get a SOAPFault
          raise_errors false

          # Default SOAP version is 1.1, upgrade to 1.2 (optional)
          soap_version 2

          # Handle errors ourselves; otherwise we just get a SOAPFault
          # raise_errors false
        end
      end

      # Format a `date` in required format; `YYMMDD`.
      def format_date(date)
        date.to_date.to_s.delete('-')
      end

      # Get `date_from` from parameters.
      def date_from
        !!params[:date_from] ? format_date(params[:date_from]) : ''
      end

      # Get `date_to` from parameters.
      def date_to
        !!params[:date_to] ? format_date(params[:date_to]) : ''
      end

      # Get `order_id` from parameters.
      def order_id
        params[:order_id]
      end

      # Get `mp_customer_id` from parameters.
      def mp_customer_id
        params[:mp_customer_id]
      end

      # Print debug message, if debug is enabled.
      def debug(message, obj = nil)
        return unless debug?

        puts "DEBUG #{self.class.name} [#{Time.now}] #{message}#{': \'' + obj.to_s + '\'' if obj}"
      end

      # Check parameters to check whether debug is enabled.
      def debug?
        @debug ||= [true, 'Y'].include?(params[:debug])
      end

      # MobilePay test-mode?
      def mp_test
        @options['mp_test', 'N'] # enable: set option to 'Y'
      end

      # Get a (cached) instance of Options loaded from file.
      def options
        @options ||= MobilePay::Options.new(params[:options_file])
      end

      private

      # Get `customer_id` from parameters with fall-back to Options.
      def customer_id
        (params[:customer_id] || options['customer_id']).to_s
      end

      # Get `merchant_id` from parameters with fall-back to Options.
      def merchant_id
        (params[:merchant_id] || options['merchant_id']).to_s
      end

      def request_builder
        @request_builder ||= MobilePay::AppSwitch::Request.new(options)
      end

      def response_handler
        @response_handler ||= MobilePay::AppSwitch::Response.new(options).tap do |rh|
          rh.certificate = bank_signing_certificate
          rh.certs = encryption_certs
        end
      end

      # Load certificates for encryption validation.
      def encryption_certs
        encryption_verified_keystore_path = options['client_verified_encryption_keystore_path']
        MobilePay::PKI::Keystore.new(encryption_verified_keystore_path, keystore_password).tap(&:load)
      end

      # Load certificates for signing.
      def signing_certs
        signing_verified_keystore_path = options['client_verified_signing_keystore_path']
        MobilePay::PKI::Keystore.new(signing_verified_keystore_path, keystore_password).tap(&:load)
      end

      # Get keystore password from parameters with fall-back to Options.
      def keystore_password
        params[:client_keystore_password] || options['client_keystore_password']
      end

      # Get the WSDL URI for this SOAP Operation.
      def wsdl_uri
        File.realpath(File.join(File.expand_path(File.dirname(__FILE__)), '..', '..', '..', 'config', 'soapsg', wsdl_file))
      end
    end
  end
end
