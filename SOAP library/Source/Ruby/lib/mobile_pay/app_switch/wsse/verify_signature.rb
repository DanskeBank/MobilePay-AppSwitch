module MobilePay
  module AppSwitch
    class WSSE
      # Validating DBG AppSwitch signed messages.
      class VerifySignature < Akami::WSSE::VerifySignature
        attr_accessor :decrypter

        # Remove `&:noblanks` parameter to Nokogiri for successful `SignedInfo` verification.
        def initialize(xml)
          @document = Nokogiri::XML(xml.to_s)
        end

        # Namespace for WS XENC.
        ENCRYPTED_DATA_NAMESPACE = 'http://www.w3.org/2001/04/xmlenc#'.freeze

        def namespaces
          @namespaces ||= super.merge(
            xenc: ENCRYPTED_DATA_NAMESPACE
          )
        end

        private

        def verify
          document.xpath('//wse:Security/ds:Signature/ds:SignedInfo/ds:Reference', namespaces).each do |ref|
            digest_algorithm = ref.at_xpath('//ds:DigestMethod', namespaces)['Algorithm']
            element_id = ref.attributes['URI'].value[1..-1] # strip leading '#'
            element = document.at_xpath(%(//*[@wsu:Id="#{element_id}"]), namespaces)

            # If encrypted, decrypt and replace content
            encrypted_data = element.at_xpath('xenc:EncryptedData', namespaces)
            decrypter.decrypt!(encrypted_data) if encrypted_data

            unless supplied_digest(element) == generate_digest(element, digest_algorithm)
              fail InvalidDigest, "Invalid Digest for #{element_id}"
            end
          end

          data = canonicalize(signed_info)
          signature = Base64.decode64(signature_value)
          signature_algorithm = document.at_xpath('//wse:Security/ds:Signature/ds:SignedInfo/ds:SignatureMethod', namespaces)['Algorithm']
          signature_digester = digester_for_signature_method(signature_algorithm)
          certificate.public_key.verify(signature_digester, signature, data) || fail(InvalidSignedValue, 'Could not verify the signature value!')
        end
      end
    end
  end
end
