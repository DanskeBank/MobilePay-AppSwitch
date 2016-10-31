$:.push(File.dirname(__FILE__)+"/lib/")

require 'mobile_pay/pki'

#MobilePay::PKI.create_client_certificates(options_file: './config/options.yml', name: '/CN=DGITL')

MobilePay::PKI.get_bank_certificate(options_file: './config/options.yml')

#MobilePay::PKI.create_certificate(options_file: './config/options.yml', pin: 'PINK')

#MobilePay::PKI.renew_certificate(options_file: './config/options.yml')

#MobilePay::PKI.get_own_certificate_list(options_file: './config/options.yml')

#MobilePay::PKI.revoke_certificate(options_file: './config/options.yml', certificate_serial_no: ['5490000000014901', '5490000000014902', '5990000000015601', '5990000000015602'], crl_reason: 0)

#MobilePay::PKI.certificate_status(options_file: './config/options.yml', certificate_serial_no: ['2580000000013702', '2580000000013701'])

