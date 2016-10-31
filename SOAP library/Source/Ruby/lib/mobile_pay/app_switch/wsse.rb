require 'mobile_pay/pki'
require 'securerandom'
require 'signer'

module MobilePay
  module AppSwitch
    # Implementation for SOAP WSSE (DBG flavour).
    class WSSE < Akami::WSSE
      require 'mobile_pay/app_switch/wsse/decrypt'
      require 'mobile_pay/app_switch/wsse/encrypt'
      require 'mobile_pay/app_switch/wsse/verify_signature'

      # Namespace for WS DSIG.
      DS_NAMESPACE = 'http://www.w3.org/2000/09/xmldsig#'.freeze

      # Namespace for WS XENC.
      ENCRYPTED_DATA_NAMESPACE = 'http://www.w3.org/2001/04/xmlenc#'.freeze

      # SOAP Envelope namespace.
      SOAP_ENVELOPE_NAMESPACE = 'http://www.w3.org/2003/05/soap-envelope'.freeze

      # Encrypt `document`'s `Body` element using receiver's certificate/public key.
      def encrypt_body!(certificate, document)
        encrypter = MobilePay::AppSwitch::WSSE::Encrypt.new(certificate)
        encrypter.document = document

        encrypter.to_xml
      end

      # Add business signature to `document`'s Body element using private key in `certs`.
      def sign_body!(certs, document)
        signer = Signer.new(document)
        signer.cert = certs.cert
        signer.private_key = certs.private_key
        signer.security_token_id = generate_element_id('IdX509')

        signer.document.xpath('//env:Body', env: SOAP_ENVELOPE_NAMESPACE).each do |node|
          signer.digest!(node)
        end

        signer.sign!(security_token: true)
        # add_signature_node_ids(signer.signature_node)

        signer.to_xml
      end

      # Add transport signature to encrypted body element using private key in `certs`.
      def sign_encrypted_body!(certs, document)
        signer = Signer.new(document)
        signer.cert = certs.cert
        signer.private_key = certs.private_key
        signer.security_token_id = generate_element_id('IdX509')

        # force new Signature node
        signer.signature_node = add_signature_node(signer.document)

        signer.document.xpath('//xenc:EncryptedData', xenc: ENCRYPTED_DATA_NAMESPACE).each do |node|
          signer.digest!(node)
        end

        signer.sign!(security_token: true)
        # add_signature_node_ids(signer.signature_node)
        fix_body_encrypted_data_id_namespace(signer.document)

        signer.to_xml
      end

      # Verify transport and business signatures, while decrypting document body.
      def verify_signed_encrypted!(certificate, certs, document)
        signature_verifier = signature_verifier(certs, document)

        signature_verifier.certificate.to_der == certificate.to_der || fail(MobilePay::Error, 'Unknown signing certificate!')
        signature_verifier.verify!

        signature_verifier.document
      end

      protected

      # Add `ds:Signature` node as first child to `wsse:Security` node.
      def add_signature_node(document)
        signature_node = Nokogiri::XML::Node.new('Signature', document)
        signature_node.default_namespace = DS_NAMESPACE
        security_node = document.xpath('//wsse:Security', wsse: Akami::WSSE::WSE_NAMESPACE).first
        security_node.child.add_previous_sibling(signature_node)
      end

      # `Signer` gem does not add IDs to elements in `Signature` node.
      def add_signature_node_ids(signature_node)
        signature_node['Id'] = generate_element_id('SIG')
        key_info_node = signature_node.at_xpath('//ds:KeyInfo', ds: DS_NAMESPACE)
        key_info_node['Id'] = generate_element_id('KI')
        security_token_ref_node = key_info_node.at_xpath('//wsse:SecurityTokenReference', wsse: WSE_NAMESPACE)
        wsu_ns = :wsu
        security_token_ref_node["#{wsu_ns}:Id"] = generate_element_id('STR')
      end

      # `Signer` gem adds Id to `EncryptedData` with wrong namespace `wsu:Id`;
      # change this to expected `Id`.
      def fix_body_encrypted_data_id_namespace(document)
        encrypted_data = document.at_xpath('//env:Body/xenc:EncryptedData', env: SOAP_ENVELOPE_NAMESPACE, xenc: ENCRYPTED_DATA_NAMESPACE)
        id = document.at_xpath('//env:Body/xenc:EncryptedData/@wsu:Id', env: SOAP_ENVELOPE_NAMESPACE, wsu: Akami::WSSE::WSU_NAMESPACE, xenc: ENCRYPTED_DATA_NAMESPACE).remove.to_s
        encrypted_data['Id'] = id
      end

      private

      # Generate a random UUID for XML elements like `SecurityToken`.
      # Note: adding a prefix as a xsd:Id must not start with [0-9]!
      def generate_element_id(prefix = 'Id')
        "#{prefix}-#{SecureRandom.uuid}"
      end

      # Create and initialize signature verifier with decrypter.
      def signature_verifier(certs, document)
        VerifySignature.new(document).tap do |signature_verifier|
          decrypter = Decrypt.new(certs)
          decrypter.document = document

          signature_verifier.decrypter = decrypter
        end
      end
    end
  end
end
