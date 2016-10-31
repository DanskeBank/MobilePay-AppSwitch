module MobilePay
  module PKI
    class WSSE
      # Validating DBG PKI signed messages.
      #
      # Override parts of super class to handle PKI request signature.
      # The `ds:Signature` element is placed inside Request instead of below '//wse:Security/'.
      class VerifySignature < Akami::WSSE::VerifySignature
        # Remove `&:noblanks` parameter to Nokogiri for successful `SignedInfo` verification.
        def initialize(xml)
          @document = Nokogiri::XML(xml.to_s)
        end

        # Override to fix xpath.
        def certificate
          certificate_value = document.at_xpath('//ds:Signature/ds:KeyInfo/ds:X509Data/ds:X509Certificate', namespaces).text.strip
          OpenSSL::X509::Certificate.new Base64.decode64(certificate_value)
        end

        protected

        # Override to fix xpaths and remove Signature element before veifying digest.
        def verify
          document.xpath('//ds:Signature/ds:SignedInfo/ds:Reference', namespaces).each do |ref|
            digest_algorithm = ref.at_xpath('//ds:DigestMethod', namespaces)['Algorithm']
            element_id = ref.attributes['URI'].value[1..-1] # strip leading '#'
            doc2 = document.dup # don't change original document
            element = doc2.at_xpath(%(//*[@xml:id="#{element_id}"]), namespaces)

            # Remove `ds:Signature` element before verifying digest.
            element.search('//ds:Signature', namespaces).each(&:remove)

            unless supplied_digest(element) == generate_digest(element, digest_algorithm)
              fail InvalidDigest, "Invalid Digest for #{element_id}"
            end
          end

          data = canonicalize(signed_info)
          signature = Base64.decode64(signature_value)
          signature_algorithm = document.at_xpath('//ds:Signature/ds:SignedInfo/ds:SignatureMethod', namespaces)['Algorithm']
          signature_digester = digester_for_signature_method(signature_algorithm)

          certificate.public_key.verify(signature_digester, signature, data) || fail(InvalidSignedValue, 'Could not verify the signature value')
        end

        # Override to fix xpath.
        def signed_info
          document.at_xpath('//ds:Signature/ds:SignedInfo', namespaces)
        end

        # Override to fix attribute name.
        def supplied_digest(element)
          element = document.at_xpath(element, namespaces) if element.is_a? String
          find_digest_value element.attributes['id'].value
        end

        # Override to fix xpath.
        def signature_value
          element = document.at_xpath('//ds:Signature/ds:SignatureValue', namespaces)
          element ? element.text : ''
        end

        # Override to fix xpath.
        def find_digest_value(id)
          document.at_xpath(%(//ds:Signature/ds:SignedInfo/ds:Reference[@URI="##{id}"]/ds:DigestValue), namespaces).text
        end
      end
    end
  end
end
