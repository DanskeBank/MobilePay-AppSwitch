module MobilePay
  module PKI
    # Check Bank Root Certificate CRL list.
    class CRL
      attr_accessor :certificate

      def initialize(certificate)
        self.certificate = certificate
      end

      def validate
        crl_data = `curl #{crl_url}`
        crl = OpenSSL::X509::CRL.new(crl_data)
        fail 'Invalid CRL; cannot verify signature!' unless crl.verify(certificate.public_key)

        crl
      end

      def revoked
        crl.revoked
      end

      protected

      # Get CRL url from root certificate.
      def crl_url
        crl_extension = certificate.extensions.detect { |ext| ext.oid == 'crlDistributionPoints' }
        fail 'CRLDistributionPoints extension not found on root certificate!' unless crl_extension

        crl_extension.value =~ /URI:(\S+)/
        Regexp.last_match(1)
      end
    end
  end
end
