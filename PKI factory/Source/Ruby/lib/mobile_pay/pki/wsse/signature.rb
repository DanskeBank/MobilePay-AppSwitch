require 'akami/wsse/signature'
require 'gyoku'

module MobilePay
  module PKI
    class WSSE
      # Create XMLDSIG Enveloped style signature for SOAP request.
      class Signature < Akami::WSSE::Signature
        # Get the XMl document.

        EnvelopedSignature = 'http://www.w3.org/2000/09/xmldsig#enveloped-signature'.freeze

        # Get the entire signed document.
        def signed_document
          document_with_signature(signed_info_xml, key_info_xml, signature_value_xml)
        end

        # Returns XML namespaces that are used internally for document querying.
        def namespaces
          @namespaces ||= {
            wse: Akami::WSSE::WSE_NAMESPACE,
            ds:  'http://www.w3.org/2000/09/xmldsig#',
            wsu: Akami::WSSE::WSU_NAMESPACE
          }
        end

        private

        # Get XML document with (manually packed) `Signature` element.
        def document_with_signature(signed_info, key_info = nil, signature_value = nil)
          signed_info = Nokogiri::XML(signed_info)
          signed_info_document = @document.to_xml.split(%r{(</elem:)}m).insert(-3, %(<Signature xmlns="#{SignatureNamespace}">\n#{canonicalize(signed_info)}\n#{signature_value}#{key_info}</Signature>)).join
          Nokogiri::XML(signed_info_document)
        end

        def the_signature
          fail MissingCertificate, 'Expected a private_key for signing' unless certs.private_key
          signed_info = document_with_signature(signed_info_xml).at_xpath('//ds:Signature/ds:SignedInfo', namespaces)
          signed_info = signed_info ? canonicalize(signed_info) : ''
          signature = certs.private_key.sign(OpenSSL::Digest::SHA1.new, signed_info)
          Base64.encode64(signature).delete("\n")
        end

        # Calculate digest of correct request body section.
        def body_digest
          # body = canonicalize(@document.at_xpath("//env:Envelope/env:Body"))
          body = canonicalize(@document.at_xpath("//*[substring(local-name(), string-length(local-name()) - string-length('Request') +1) = 'Request']"))
          Base64.encode64(OpenSSL::Digest::SHA1.digest(body)).strip
        end

        # Generate KeyInfo element in required format.
        def key_info
          {
            'KeyInfo' => {
              'X509Data' => {
                'X509Certificate' => Base64.encode64(certs.cert.to_der).delete("\n"),
                'X509IssuerSerial' => {
                  'X509IssuerName' => "serialNumber=#{certs.cert.serial}, #{certs.cert.issuer}",
                  'X509SerialNumber' => certs.cert.serial.to_s
                }
              }
            }
          }
        end

        # Force `SignedInfo` element's Reference URI to blank.
        def signed_info
          super.tap do |si|
            si['SignedInfo'][:attributes!]['Reference']['URI'] = ['']
          end
        end

        # Override to fix `Transforms` element to include `enveloped-signature`.
        def signed_info_transforms
          {
            'Transforms' => {
              'Transform/' => [
                { :@Algorithm => EnvelopedSignature, :content! => nil },
                { :@Algorithm => ExclusiveXMLCanonicalizationAlgorithm, :content! => nil }
              ]
            }
          }
        end

        # Get XMl for `KeyInfo` element.
        def key_info_xml
          Gyoku.xml(key_info)
        end

        # Get XML for `SignatureValue` element.
        def signature_value_xml
          Gyoku.xml(signature_value)
        end

        # Get XMl for `SignedInfo` element.
        def signed_info_xml
          Gyoku.xml(signed_info)
        end
      end
    end
  end
end
