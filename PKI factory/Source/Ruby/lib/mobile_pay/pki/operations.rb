require 'mobile_pay/pki/certificate'
require 'mobile_pay/pki/bank_certificates'
require 'mobile_pay/pki/keystore'
require 'mobile_pay/pki/wsse'
require 'mobile_pay/pki/request'
require 'mobile_pay/pki/response'

module MobilePay
  module PKI
    # MobilePay::PKI SOAP Operations' base class.
    #
    # Subclasses *must* implement the following public methods:
    # - `request_message_name` :: SOAPAction value
    # - `request_message_contents` :: Hash for SOAP Request Body
    # - `handle_response` :: SOAP Response handler invoked for `@response` after signature validation and response validation
    # and the following protected methods:
    # - `name` :: SOAP Operation name
    # - `verify_response!` :: SOAP Response validation invoked for `@response` after signature validation; should raise an exception in case of validation failures
    # and *may* implement the following protected methods:
    # - `encrypt_request?` :: should we encrypt outgoing requests? (default: false)
    # - `sign_request?` :: should we sign outgoing requests? (default: true)
    class Operations
      include BankCertificates

      attr_accessor :params, :response

      def initialize(params)
        @params = params
      end

      # Invoke the SOAP Operation and verify response header, signatures etc.
      def call
        @response = client.call(name) do |locals|
          request_builder.build(self, client, name, customer_id, params[:request_id])
          request_builder.sign!(signing_certs) if sign_request?
          request_builder.encrypt!(bank_encryption_certificate) if encrypt_request?

          debug 'SOAP Request', request_builder.message_body
          locals.xml request_builder.message_body
        end

        debug 'SOAP Response', @response
        response_handler.verify!(@response, customer_id, request_builder.request_id)
        verify_response!
      end

      protected

      # Get a (cached) instance of the SOAP Client.
      def client
        @client ||= MobilePay::PKI::ClientFactory.build(options)
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

      # Print warning message.
      def warn(message)
        puts "WARN #{self.class.name} [#{Time.now}] #{message}"
      end

      # Get a (cached) instance of Options loaded from file.
      def options
        @options ||= MobilePay::Options.new(params[:options_file])
      end

      # By default, requests should not be encrypted.
      def encrypt_request?
        false
      end

      # By default, requests must be signed.
      def sign_request?
        true
      end

      # Load certificates for encrypting.
      def encryption_certs
        encryption_verified_keystore.tap(&:load)
      end

      # Load certificates for signing.
      def signing_certs
        signing_verified_keystore.tap(&:load)
      end

      def response_body_contents
        response_handler.response_body_contents
      end

      #
      # Our certificates
      #

      def encryption_generated_keystore
        MobilePay::PKI::Keystore.new(encryption_generated_keystore_path, keystore_password)
      end

      def encryption_verified_keystore
        Keystore.new(encryption_verified_keystore_path, keystore_password)
      end

      def signing_generated_keystore
        MobilePay::PKI::Keystore.new(signing_generated_keystore_path, keystore_password)
      end

      def signing_verified_keystore
        Keystore.new(signing_verified_keystore_path, keystore_password)
      end

      def encryption_generated_keystore_path
        options['client_generated_encryption_keystore_path']
      end

      def encryption_verified_keystore_path
        options['client_verified_encryption_keystore_path']
      end

      def signing_generated_keystore_path
        options['client_generated_signing_keystore_path']
      end

      def signing_verified_keystore_path
        options['client_verified_signing_keystore_path']
      end

      DEFAULT_KEY_GENERATOR_TYPE = 'software'.freeze

      def key_generator_type
        @options['pki_key_generator_type', DEFAULT_KEY_GENERATOR_TYPE]
      end

      private

      # Get `customer_id` from parameters with fall-back to Options.
      def customer_id
        (params[:customer_id] || options['customer_id']).to_s
      end

      # Get keystore password from parameters with fall-back to Options.
      def keystore_password
        params[:client_keystore_password] || options['client_keystore_password']
      end

      def request_builder
        @request_builder ||= Request.new(options)
      end

      def response_handler
        @response_handler ||= Response.new(options)
      end
    end
  end
end
