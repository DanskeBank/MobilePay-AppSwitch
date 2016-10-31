module MobilePay
  module PKI
    # Accessing PKCS#12 keystore files.
    class Keystore
      attr_accessor :pkcs12

      # MobilePay::Pki::Keystore.new('./_data/ClientVerifiedEncryption.pfx', 'password')
      def initialize(pkcs12_path, password)
        @pkcs12_path = pkcs12_path
        @password = password
      end

      # Returns an <tt>OpenSSL::X509::Certificate</tt> for the +keystore+.
      def cert
        pkcs12.certificate
      end

      # Returns an <tt>OpenSSL::PKey::RSA</tt> for the +keystore+.
      def private_key
        pkcs12.key
      end

      # Generate new `Keystore` with specified subject `name`.
      def generate(name)
        key = generate_key
        certificate = generate_certificate(name, key)

        # http://stackoverflow.com/questions/16589722/openssl-equivalent-command-in-ruby
        @pkcs12 = OpenSSL::PKCS12.create(@password, name, key, certificate)
      end

      # Read a PKCS#12 keystore from file in DER format.
      def load
        @pkcs12 = OpenSSL::PKCS12.new(File.binread(@pkcs12_path), @password)
      end

      # Update `Keystore` with CA-signed certificate and save to file.
      def update(key, certificate, ca_certs)
        name = certificate.subject.to_s

        @pkcs12 = OpenSSL::PKCS12.create(@password, name, key, certificate, ca_certs)
        write
      end

      # Write keystore to PKCS#12 file in DER format.
      def write
        backup(@pkcs12_path)

        File.open(@pkcs12_path, 'wb') do |file|
          file << @pkcs12.to_der
        end
      end

      private

      # Generate certificates with 2 years validity
      CERTIFICATE_VALIDITY = 2 * 365 * 24 * 60 * 60

      # Use 2048 as a for-now reasonable key length
      PRIVATE_KEY_LEN = 2048

      # Keep numbered backups of old files.
      def backup(filename)
        return unless File.exist?(filename)

        last_backup_file = Dir.glob("#{filename}-*").sort.last
        last_backup_no = last_backup_file ? last_backup_file.match(%r{-(\d{4})$})[1].to_i : 0

        backup_no = (last_backup_no + 1).to_s.rjust(4, "0")
        FileUtils.cp(filename, [filename, backup_no].join('-'))
      end

      # Generate (signing request) Certificate signed with Key.
      #
      # Parameters:
      #   `name` :: distinguished name (String or Array) for `Certificate#subject` and `Certificate#issuer` fields.
      #   `key` :: key pair for signing certificate
      #
      # http://ruby-doc.org/stdlib-1.9.3/libdoc/openssl/rdoc/OpenSSL/X509/Certificate.html
      def generate_certificate(name, key)
        now = Time.now.utc

        cert = OpenSSL::X509::Certificate.new
        cert.version = 2 # cf. RFC 5280 - to make it a "v3" certificate
        cert.serial = now.to_i
        cert.subject = OpenSSL::X509::Name.parse(name)
        cert.issuer = cert.subject # generate as self-signed
        cert.public_key = key.public_key
        cert.not_before = now
        cert.not_after = cert.not_before + CERTIFICATE_VALIDITY

        ef = OpenSSL::X509::ExtensionFactory.new
        ef.subject_certificate = cert
        ef.issuer_certificate = cert
        cert.add_extension(ef.create_extension('keyUsage', 'digitalSignature', true))
        cert.add_extension(ef.create_extension('subjectKeyIdentifier', 'hash', false))
        cert.sign(key, OpenSSL::Digest::SHA256.new)
      end

      # Generate a new RSA keypair.
      def generate_key
        OpenSSL::PKey::RSA.new(PRIVATE_KEY_LEN)
      end
    end
  end
end
