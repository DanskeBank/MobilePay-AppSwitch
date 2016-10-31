require 'savon'

module MobilePay
  module PKI
    # Factory for creating configured and ready-to-use SOAP Client.
    class ClientFactory
      # Build SOAP Client.
      #
      # WSDL will be read from the file.
      def self.build(options)
        wsdl_uri = options['pki_wsdl_uri']

        Savon.client do
          wsdl wsdl_uri

          # Add missing namespace
          namespaces 'xmlns:elem' => 'http://danskebank.dk/PKI/PKIFactoryService/elements'

          # Handle errors ourselves; otherwise we just get a SOAPFault
          raise_errors false

          # Default SOAP version is 1.1, upgrade to 1.2 (optional)
          # soap_version 2

          # Add WS-A headers?
          # use_wsa_headers = true
        end
      end
    end
  end
end
