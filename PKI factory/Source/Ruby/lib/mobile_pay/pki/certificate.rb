module MobilePay
  module PKI
    # Helper methods for file-based Certificates.
    class Certificate
      # Load `Certificate` from PEM file.
      def load(cert_path)
        OpenSSL::X509::Certificate.new(File.read(cert_path))
      end

      # Parse `Certificate` from Base64 encoded string.
      def parse(cert_string)
        OpenSSL::X509::Certificate.new(decode_cert_string(cert_string))
      end

      # Save `Certificate` to a file in PEM format.
      def save(cert_path, certificate)
        return unless changed?(cert_path, certificate)

        File.open(cert_path, 'w') do |file|
          file << certificate.to_pem
        end
      end

      # Verify that `Certificate` is signed by `ca_certificate`.
      def verify(certificate, ca_certificate)
        certificate.verify(ca_certificate.public_key)
      end

      private

      # Has the certificate changed from the one previously stored?
      def changed?(cert_path, certificate)
        return true unless File.exist?(cert_path)

        File.read(cert_path) != certificate.to_pem
      end

      # Decode a Base64 encoded string.
      def decode_cert_string(cert_string)
        Base64.decode64(cert_string)
      end
    end
  end
end
