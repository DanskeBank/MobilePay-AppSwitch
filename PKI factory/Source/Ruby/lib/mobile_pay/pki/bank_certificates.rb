module MobilePay
  module PKI
    # Helper methods for Bank Certificates.
    #
    # Requires @options to be present and contain bank certificate paths.
    module BankCertificates
      def bank_root_certificate
        cert_helper.load(bank_root_cert_path)
      end

      def bank_encryption_certificate
        cert_helper.load(bank_encryption_cert_path)
      end

      def bank_signing_certificate
        cert_helper.load(bank_signing_cert_path)
      end

      def bank_encryption_cert_path
        @options['bank_encryption_certificate_path']
      end

      def bank_signing_cert_path
        @options['bank_signing_certificate_path']
      end

      def bank_root_cert_path
        @options['bank_root_certificate_path']
      end

      protected

      def cert_helper
        @cert_helper ||= Certificate.new
      end
    end
  end
end
