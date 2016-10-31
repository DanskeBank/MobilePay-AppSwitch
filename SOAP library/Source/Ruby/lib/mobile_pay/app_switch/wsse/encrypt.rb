module MobilePay
  module AppSwitch
    class WSSE
      # WSSE - Encrypt request-body.
      class Encrypt < MobilePay::PKI::WSSE::Encrypt
        protected

        # Get the document's body content to be encrypted.
        def body
          @body_content ||= @document.at_xpath("//*[local-name() = 'Body']").child
        end

        # Encrypted data type is XML Content.
        ENCRYPTED_DATA_TYPE = 'http://www.w3.org/2001/04/xmlenc#Content'.freeze

        # Get the encrypted data type; either `Content` or `Element`.
        def encrypted_data_type
          ENCRYPTED_DATA_TYPE
        end
      end
    end
  end
end
