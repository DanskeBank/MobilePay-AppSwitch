require 'mobile_pay/pki/crl'

module MobilePay
  module PKI
    class Operations
      # Check CRL for bank certificates.
      class CheckCRL < MobilePay::PKI::Operations
        def call
          options && verify!(bank_root_certificate)
        end

        def handle_response
          fail 'Not a SOAP Operation.'
        end

        private

        # Generate new Key/Certificate and store as PKCS#12 keystore.
        def verify!(certificate)
          MobilePay::PKI::CRL.new(certificate).validate
        end

        # Get `name` parameter.
        def name
          params[:name]
        end
      end
    end
  end
end
