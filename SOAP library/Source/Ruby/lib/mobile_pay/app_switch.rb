require "mobile_pay/app_switch/version"
require "mobile_pay/pki"
require 'mobile_pay/app_switch/operations'
require 'mobile_pay/app_switch/operations/cancel'
require 'mobile_pay/app_switch/operations/capture'
require 'mobile_pay/app_switch/operations/get_reservations'
require 'mobile_pay/app_switch/operations/get_status'
require 'mobile_pay/app_switch/operations/refund'

module MobilePay
  # MobilePay AppSwitch API SOAP Actions.
  #
  # Convenience methods to call SOAP operations.
  module AppSwitch
    # Call `Cancel` SOAP Action.
    #
    # Parameters hash:
    #   `:order_id` :: MobilePay OrderId
    #   `:mp_customer_id` :: MobilePay CustomerId
    #   `:date_from` :: TBC
    #   `:date_to` :: TBC
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:merchant_id` :: merchant_id (optional; otherwise use configuration value).
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/app_switch'
    #   response = MobilePay::AppSwitch.cancel(order_id: 1234, mp_customer_id: 112233)
    #   # => {:return_code=>"00", :reason_code=>"00", :original_transaction_id=>"APPDK0000000000#1234"}
    #   response = MobilePay::AppSwitch.cancel(order_id: 1234, mp_customer_id: 112233, date_from: Date.today - 7, date_to: Date.today)
    #   # => {:return_code=>"00", :reason_code=>"00", :original_transaction_id=>"APPDK0000000000#1234"}
    def self.cancel(params)
      MobilePay::AppSwitch::Operations::Cancel.new(params).call
    end

    # Call `Capture` SOAP Action.
    #
    # Parameters hash:
    #   `:order_id` :: MobilePay OrderId
    #   `:mp_customer_id` :: MobilePay CustomerId
    #   `:date_from` :: TBC
    #   `:date_to` :: TBC
    #   `:bulk_ref` :: TBC
    #   `:amount` :: Decimal amount
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:merchant_id` :: merchant_id (optional; otherwise use configuration value).
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/app_switch'
    #   response = MobilePay::AppSwitch.capture(order_id: 1234, mp_customer_id: 112233, bulk_ref: 'BR001', amount: 3)
    #   # => {:reason_code=>"00", :original_transaction_id=>"APPDK0000000000#1234", :remainder_amount=>"3.00", :return_code=>"00", :transaction_id=>"APPDK0000000000#1234"}
    #   response = MobilePay::AppSwitch.capture(order_id: 1234, mp_customer_id: 112233, bulk_ref: 'BR001', amount: 3.14, date_from: Date.today - 7, date_to: Date.today)
    #   # => puts response = MobilePay::AppSwitch.capture(order_id: 1234, mp_customer_id: 112233, bulk_ref: 'BR001', amount: 3.14, date_from: Date.today - 7, date_to: Date.today)
    def self.capture(params)
      MobilePay::AppSwitch::Operations::Capture.new(params).call
    end

    # Call `GetStatus` SOAP Action.
    #
    # Parameters hash:
    #   `:order_id` :: MobilePay OrderId
    #   `:mp_customer_id` :: MobilePay CustomerId
    #   `:date_from` :: TBC
    #   `:date_to` :: TBC
    #   `:extended_output` :: true for extended output (optional; default to false)
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:merchant_id` :: merchant_id (optional; otherwise use configuration value).
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/app_switch'
    #   response = MobilePay::AppSwitch.get_status(order_id: 1234, extended_output: true)
    #   # => {:return_code=>"00", :reason_code=>"00", :latest_payment_status=>nil, :original_transaction_id=>"00000000000000000000", :original_amount=>"0.00", :num_of_transactions=>"00"}
    # or
    #   response = MobilePay::AppSwitch.get_status(order_id: 1234, mp_customer_id: 112233, date_from: Date.today - 7, date_to: Date.today, extended_output: true)
    #   # => {:return_code=>"00", :reason_code=>"00", :latest_payment_status=>nil, :original_transaction_id=>"00000000000000000000", :original_amount=>"0.00", :num_of_transactions=>"00"}
    def self.get_status(params)
      MobilePay::AppSwitch::Operations::GetStatus.new(params).call
    end

    # Call `GetReservations` SOAP Action.
    #
    # Parameters hash:
    #   `:mp_customer_id` :: MobilePay CustomerId
    #   `:date_from` :: TBC
    #   `:date_to` :: TBC
    #   `:action_code` :: TBC
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:merchant_id` :: merchant_id (optional; otherwise use configuration value).
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/app_switch'
    #   response = MobilePay::AppSwitch.get_reservations(mp_customer_id: '+4521000000', action_code: 'S', date_from: Date.parse('2015-02-01'), date_to: Date.parse('2016-02-01'))
    #   # => {:return_code=>"00", :reason_code=>"00", :num_of_transactions=>"00"}
    def self.get_reservations(params)
      MobilePay::AppSwitch::Operations::GetReservations.new(params).call
    end

    # Call `Refund` SOAP Action.
    #
    # Parameters hash:
    #   `:order_id` :: MobilePay OrderId
    #   `:mp_customer_id` :: MobilePay CustomerId
    #   `:date_from` :: TBC
    #   `:date_to` :: TBC
    #   `:bulk_ref` :: TBC
    #   `:amount` :: Decimal amount
    #   `:client_keystore_password` :: keystore password (optional; otherwise use configuration)
    #   `:merchant_id` :: merchant_id (optional; otherwise use configuration value).
    #   `:options_file` :: path to `options` YAML file (optional).
    #   `:request_id` :: request_id (optional; will default to timestamp).
    #
    # Test in `bin/console` or `irb':
    #   require 'mobile_pay/app_switch'
    #   response = MobilePay::AppSwitch.refund(order_id: 1234, mp_customer_id: 'Customer', bulk_ref: 'BR001', amount: 3)
    #   # => {:return_code=>"00", :reason_code=>"00", :transaction_id=>"00000000000000000000", :original_transaction_id=>"00000000000000000000", :remainder_amount=>"0.00"}
    #   response = MobilePay::AppSwitch.refund(order_id: 1234, mp_customer_id: 'Customer', bulk_ref: 'BR001', amount: 3.14, date_from: Date.today, date_to: Date.today.next)
    #   # => {:return_code=>"00", :reason_code=>"00", :transaction_id=>"00000000000000000000", :original_transaction_id=>"00000000000000000000", :remainder_amount=>"0.00"}
    def self.refund(params)
      MobilePay::AppSwitch::Operations::Refund.new(params).call
    end
  end
end
