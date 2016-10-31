$:.push(File.dirname(__FILE__)+"/lib/")

require 'test/unit'
require 'mobile_pay/pki'
require 'date'

class PKIFactoryTest < Test::Unit::TestCase

	OPTIONS_PATH = './config/options_test.yml'
	PIN = 'PINK'

  def test_create_renew_revoke
    @options ||= MobilePay::Options.new(OPTIONS_PATH)
	
	set_up_keys()
	assert_true(File.exist?(@options['client_generated_signing_keystore_path']))
	assert_true(File.exist?(@options['client_generated_encryption_keystore_path']))
    assert_false(File.exist?(@options['client_verified_signing_keystore_path']))
	assert_false(File.exist?(@options['client_verified_encryption_keystore_path']))
	
	MobilePay::PKI.create_certificate(options_file: OPTIONS_PATH, pin: PIN)
	assert_true(File.exist?(@options['client_verified_signing_keystore_path']))
	assert_true(File.exist?(@options['client_verified_encryption_keystore_path']))
	
	verified_signing_cert = test_store(@options['client_verified_signing_keystore_path'])
	verified_encryption_cert = test_store(@options['client_verified_encryption_keystore_path'])
	
	delete_generated_keys()
	assert_false(File.exist?(@options['client_generated_signing_keystore_path']))
	assert_false(File.exist?(@options['client_generated_encryption_keystore_path']))
	
	MobilePay::PKI.renew_certificate(options_file: OPTIONS_PATH)
	assert_true(File.exist?(@options['client_verified_signing_keystore_path']))
	assert_true(File.exist?(@options['client_verified_encryption_keystore_path']))
	
	renewed_signing_cert = test_store(@options['client_verified_signing_keystore_path'])
	renewed_encryption_cert = test_store(@options['client_verified_encryption_keystore_path'])
	
	valid_from_time = verified_signing_cert.not_before()
	new_valid_from_time = renewed_signing_cert.not_before()
	assert_true(valid_from_time.to_f < new_valid_from_time.to_f)
	
	valid_from_time = verified_encryption_cert.not_before()
	new_valid_from_time = renewed_encryption_cert.not_before()
	assert_true(valid_from_time.to_f < new_valid_from_time.to_f)

	MobilePay::PKI.certificate_status(options_file: OPTIONS_PATH, certificate_serial_no: [
		verified_signing_cert.serial.to_s,
		verified_encryption_cert.serial.to_s,
		renewed_signing_cert.serial.to_s,
		renewed_encryption_cert.serial.to_s])
		
	MobilePay::PKI.revoke_certificate(options_file: OPTIONS_PATH, certificate_serial_no: [
		verified_signing_cert.serial.to_s,
		verified_encryption_cert.serial.to_s,
		renewed_signing_cert.serial.to_s,
		renewed_encryption_cert.serial.to_s], crl_reason: 0)
	
  end
  
  def set_up_keys	
	delete_generated_keys()
	delete_issued_keys()
	delete_bank_keys()
	MobilePay::PKI.create_client_certificates(options_file: OPTIONS_PATH, name: '/CN=DGITL')
	MobilePay::PKI.get_bank_certificate(options_file: OPTIONS_PATH)
  end
  
  def delete_generated_keys
	File.delete(@options['client_generated_signing_keystore_path']) if File.exist?(@options['client_generated_signing_keystore_path'])
	File.delete(@options['client_generated_encryption_keystore_path']) if File.exist?(@options['client_generated_encryption_keystore_path'])
  end
  
  def delete_issued_keys
	File.delete(@options['client_verified_signing_keystore_path']) if File.exist?(@options['client_verified_signing_keystore_path'])
	File.delete(@options['client_verified_encryption_keystore_path']) if File.exist?(@options['client_verified_encryption_keystore_path'])
  end
  
  def delete_bank_keys
	File.delete(@options['bank_signing_certificate_path']) if File.exist?(@options['bank_signing_certificate_path'])
	File.delete(@options['bank_encryption_certificate_path']) if File.exist?(@options['bank_encryption_certificate_path'])
  end
  
  def test_store(file_path)
	keystore = MobilePay::PKI::Keystore.new(file_path, @options['client_keystore_password'])
	keystore.load()
	assert_not_nil(keystore.private_key())
	assert_not_nil(keystore.cert())
	keystore.cert()
  end
  
end