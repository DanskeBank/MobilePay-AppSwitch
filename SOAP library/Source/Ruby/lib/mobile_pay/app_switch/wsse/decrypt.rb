module MobilePay
  module AppSwitch
    class WSSE
      # Decrypt content using certs' private key and replace encrypted data element.
      class Decrypt
        attr_accessor :certs

        def initialize(certs)
          @certs = certs
        end

        def document=(document)
          @document = Nokogiri::XML(document.to_s)
        end

        def decrypt!(encrypted_data)
          # key_algorithm_url = encrypted_data.at_xpath('//xenc:EncryptionMethod', namespaces)['Algorithm']
          verify_key_identifier!(encrypted_data)

          cipher_algorithm = cipher_algorithm(encrypted_data)
          key = encryption_key(encrypted_data)

          # initialization vector (8 bytes) is prepended cipher_value
          iv_cipher_value = encrypted_data.xpath('//xenc:CipherData/xenc:CipherValue', namespaces).last.text
          iv, cipher_value = split_iv_cipher_value(iv_cipher_value)

          cipher = cipher(cipher_algorithm, key, iv)
          decrypted_xml = decrypt_data(cipher, cipher_value)
          encrypted_data.replace(decrypted_xml)
        end

        private

        # Namespace for WS DSIG.
        DS_NAMESPACE = 'http://www.w3.org/2000/09/xmldsig#'.freeze

        # Namespace for WS XENC.
        ENCRYPTED_DATA_NAMESPACE = 'http://www.w3.org/2001/04/xmlenc#'.freeze

        def namespaces
          @namespaces ||= {
            ds: DS_NAMESPACE,
            wsse: Akami::WSSE::WSE_NAMESPACE,
            xenc: ENCRYPTED_DATA_NAMESPACE
          }
        end

        # Get Cipher.
        def cipher(algorithm, key, iv)
          @cipher ||= OpenSSL::Cipher.new(algorithm).tap do |cipher|
            cipher.decrypt

            cipher.key = key
            cipher.iv = iv
          end
        end

        def cipher_algorithm_for_cipher(algorithm_url)
          cipher_algorithm_mapping = {
            'http://www.w3.org/2001/04/xmlenc#tripledes-cbc' => 'DES-EDE3-CBC'
          }
          cipher_algorithm_mapping[algorithm_url] || algorithm_url
        end

        # http://timolshansky.com/2011/10/23/ruby-triple-des-encryption.html
        def decrypt_data(cipher, encrypted_data)
          output = cipher.update(encrypted_data)
          output << cipher.final
          output
        end

        # Get cipher algorithm.
        def cipher_algorithm(encrypted_data)
          cipher_algorithm_url = encrypted_data.xpath('//xenc:EncryptionMethod', namespaces).last['Algorithm']
          cipher_algorithm_for_cipher(cipher_algorithm_url)
        end

        # Get decrypted `ephermal key`.
        def encryption_key(encrypted_key)
          encrypted_key_text = encrypted_key.at_xpath('//xenc:CipherData/xenc:CipherValue', namespaces).text
          certs.private_key.private_decrypt(Base64.decode64(encrypted_key_text))
        end

        # Find correct `encrypted_key` element.
        def find_encrypted_key(encrypted_data)
          encrypted_data_id = encrypted_data['Id']
          @document.xpath('//xenc:EncryptedKey', namespaces).detect do |encrypted_key|
            encrypted_key.at_xpath('//xenc:ReferenceList/xenc:DataReference', namespaces)['URI'] == "##{encrypted_data_id}"
          end
        end

        # Get `key-identifier` for `Certificate`.
        def key_identifier(certificate)
          pkey = certificate.public_key
          seq = OpenSSL::ASN1::Sequence([OpenSSL::ASN1::Integer.new(pkey.n),
                                         OpenSSL::ASN1::Integer.new(pkey.e)])
          digest = OpenSSL::Digest::SHA1.new
          Digest::SHA1.digest(seq.to_der)
        end

        # Split 8 byte Initialization Vector (IV) from Cipher Value.
        def split_iv_cipher_value(data)
          bytes = Base64.decode64(data).bytes # or: unpack('C*')
          iv = bytes[0..7].pack('C*')
          cipher_value = bytes[8..-1].pack('C*')

          return iv, cipher_value
        end

        # Verify `KeyIdentifier` is same as our local certificate.
        def verify_key_identifier!(encrypted_data)
          encrypted_key = find_encrypted_key(encrypted_data)
          key_identifier = encrypted_key.at_xpath('//ds:KeyInfo/wsse:SecurityTokenReference/wsse:KeyIdentifier', namespaces).text

          key_identifier(certs.cert) == Base64.decode64(key_identifier) || fail(MobilePay::Error, 'Invalid key identifier!')
        end
      end
    end
  end
end
