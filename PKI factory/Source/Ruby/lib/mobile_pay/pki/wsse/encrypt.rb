module MobilePay
  module PKI
    class WSSE
      # WSSE - Encrypt request-body.
      class Encrypt
        include Akami::C14nHelper

        # Initialize with Encryption Certificate.
        def initialize(certificate)
          @certificate = certificate
        end

        # Set the document to be encrypted.
        def document=(document)
          @document = Nokogiri::XML(document.to_s)
        end

        # Replace `ApplicationResponse` with `EncryptedData`
        # - The EncryptedData contains the actual encrypted data (in a CipherData subelement)
        #   as well as sub-elements necessary for decryption (EncryptionMethod and KeyInfo)
        def to_xml
          encrypted_xml = Gyoku.xml(to_token)
          body.replace(encrypted_xml)
          @document.to_xml
        end

        protected

        # Get the document's body element to be encrypted.
        def body
          @body_element ||= @document.at_xpath("//*[substring(local-name(), string-length(local-name()) - string-length('Request') +1) = 'Request']")
        end

        # Encrypted data type is XML Element.
        ENCRYPTED_DATA_TYPE = 'http://www.w3.org/2001/04/xmlenc#Element'.freeze

        # Get the encrypted data type; either `Content` or `Element`.
        def encrypted_data_type
          ENCRYPTED_DATA_TYPE
        end

        private

        # Cipher algorithm: Three key triple DES EDE in CBC mode (3DES).
        CIPHER_ALGORITHM = 'des-ede3-cbc'.freeze

        # Namespace for WS DSIG.
        DS_NAMESPACE = 'http://www.w3.org/2000/09/xmldsig#'.freeze

        # Namespace for WS XENC.
        ENCRYPTED_DATA_NAMESPACE = 'http://www.w3.org/2001/04/xmlenc#'.freeze

        # Encryption algorithm specification: 3DES.
        ENCRYPTION_ALGORITHM = 'http://www.w3.org/2001/04/xmlenc#tripledes-cbc'.freeze

        # Key transport algorithm specification: RSA v1.5.
        KEY_TRANSPORT_ALGORITHM = 'http://www.w3.org/2001/04/xmlenc#rsa-1_5'.freeze

        KEY_TRANSPORT_RECIPIENT = 'name:DanskeBankCryptCERT'.freeze

        # Get Cipher for encrypting body content.
        # Also create random `ephermal key` and `initialization vector` (IV).
        def cipher
          @cipher ||= OpenSSL::Cipher.new(CIPHER_ALGORITHM).tap do |cipher|
            cipher.encrypt
            @cipher_key = cipher.random_key
            @cipher_iv = cipher.random_iv
          end
        end

        # Encrypt (RSA v1.5) ephemeral key using Encryption Certificate's Public Key.
        def encrypted_key
          cipher && @certificate.public_key.public_encrypt(@cipher_key)
        end

        # Encrypt (3DES) request body using ephemeral key, and prepend (IV).
        def encrypted_body
          cipher && @cipher_iv + cipher.update(canonicalize(body)) + cipher.final
        end

        # Create tokenized `EncryptedData` element.
        def to_token
          {
            'xenc:EncryptedData' => {
              'xenc:EncryptionMethod' => {},
              'dsig:KeyInfo' => {
                'xenc:EncryptedKey' => {
                  'xenc:EncryptionMethod' => {},
                  'dsig:KeyInfo' => {
                    'dsig:X509Data' => {
                      'dsig:X509Certificate' => Base64.encode64(@certificate.to_der).delete("\n")
                    }
                  },
                  'xenc:CipherData' => {
                    'xenc:CipherValue' => Base64.encode64(encrypted_key).delete("\n")
                  },
                  :attributes! => { 'xenc:EncryptionMethod' => { 'Algorithm' => KEY_TRANSPORT_ALGORITHM } }
                },
                :attributes! => { 'xenc:EncryptedKey' => { 'Recipient' => KEY_TRANSPORT_RECIPIENT } }
              },
              'xenc:CipherData' => {
                'xenc:CipherValue' => Base64.encode64(encrypted_body).delete("\n")
              },
              :attributes! => { 'xenc:EncryptionMethod' => { 'Algorithm' => ENCRYPTION_ALGORITHM },
                                'dsig:KeyInfo' => { 'xmlns:dsig' => DS_NAMESPACE } }
            },
            :attributes! => { 'xenc:EncryptedData' => {
              'Type' => encrypted_data_type,
              'xmlns:xenc' => ENCRYPTED_DATA_NAMESPACE }
            }
          }
        end
      end
    end
  end
end
