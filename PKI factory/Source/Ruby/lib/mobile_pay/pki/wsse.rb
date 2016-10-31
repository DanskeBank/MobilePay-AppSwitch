require 'akami/wsse'

module MobilePay
  module PKI
    # Implementation for SOAP WSSE (DBG flavour).
    class WSSE < Akami::WSSE
      require 'mobile_pay/pki/wsse/encrypt'
      require 'mobile_pay/pki/wsse/signature'
      require 'mobile_pay/pki/wsse/verify_signature'

      # Encrypt `document` using certificate with public key.
      def encrypt!(certs, document)
        encrypter = MobilePay::PKI::WSSE::Encrypt.new(certs)
        encrypter.document = document
        encrypter.to_xml
      end

      # Add enveloped signature to `document` using private key in `certs`.
      def sign!(certs, document)
        self.sign_with = MobilePay::PKI::WSSE::Signature.new(certs)
        signature.document = document_xml(document)
        signature.signed_document.to_xml
      end

      private

      # Pre-format (indent et.al.) `Document` before signing.
      def document_xml(document)
        Nokogiri::XML(document).to_xml
      end
    end
  end
end
