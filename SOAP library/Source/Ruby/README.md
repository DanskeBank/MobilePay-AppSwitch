# About Danske Bank's Ruby SOAP library

In this code base you will find:
+ a library to interact with Danske Bank's service gateway.
+ a specification of how to use the SOAP connection implemented as unit tests.

# MobilePay::AppSwitch

This Ruby gem implements the MobilePay AppSwitch API client.
The MobilePay AppSwitch API provides access to lifecycle management of MobilePay transactions using the PKI Client.

The AppSwitch client depends on [Ruby PKI Factory client](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/PKI%20factory/Source/Ruby)

Before using the PKI Client and the MobilePay AppSwitch client, it is strongly advised you are familiar with [MobilePay AppSwitch API Implementation Guide](https://github.com/DanskeBank/MobilePay-AppSwitch/blob/master/MobilePay%20AppSwitch%20API%20Implementation%20Guide.pdf).

## Installation

You need to have Ruby version 2.0 or later installed.  On Linux/UNIX, you can use the system's package manager or third-party tools (_chruby_ with _ruby-install_ or _rbenv_ / _rvm_).
On Windows machines, you can use _RubyInstaller_.
See [Installing Ruby](https://www.ruby-lang.org/en/documentation/installation/) for further information.

First clone the git repository to your local file system:

      $ git clone git@github.com:DanskeBank/MobilePay-SoapServices-SDK.git --depth 1

Add the following lines line to your application's Gemfile (adjust your path accordingly):

```ruby
gem 'mobile_pay-pki', path: './MobilePay-SoapServices-SDK/PKI factory/Source/Ruby'
gem 'mobile_pay-app_switch', path: './MobilePay-SoapServices-SDK/SOAP library/Source/Ruby/'
```

And then execute:

      $ bundle install

Or download the latest ruby gems from [mobile_pay-pki](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/PKI%20factory/Source/Ruby/pkg) and [mobile_pay-app_switch](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/SOAP%20library/Source/Ruby/pkg) and install them yourself as:

      $ gem install ./mobile_pay-pki-1.0.0
      $ gem install ./mobile_pay-app_switch-1.0.0

## Usage

Go to [Merchant APIs](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/Merchant%20APIs) for further documentation and resources.

Example of using the AppSwitch API client:

      require 'mobile_pay/app_switch'
      MobilePay::AppSwitch.cancel(options_file: '/path/to/options.yml', order_id: 12345678, mp_customer_id: 12345, date_from: Date.today - 7, date_to: Date.today)
      # => {:return_code=>"00", :reason_code=>"00", :original_transaction_id=>"APPDK0000000000#1234"}

or

      require 'mobile_pay/app_switch'
      MobilePay::AppSwitch.cancel(options_file: '/path/to/options.yml', client_keystore_password: 'some-secure-password', merchant_id: 'your-merchant-id', order_id: 12345678, mp_customer_id: 12345, date_from: Date.today - 7, date_to: Date.today)

See the documentation and example usage for each method in `lib/mobile_pay/app_switch.rb`.

**Note:** When in test mode (`mp_test: 'Y'` in options.yml), you can use the MerchantId `APPDK0000000000` for testing purpose (provided by parameter `merchant_id: "APPDK0000000000"` or add to options.yml).

### Error handling

There are several levels of errors; some will raise an exception (optionally containing `code` and `additional_text` attributes), other returns a response where return_code is not '00'.

If you encounter an error, for instance using a wrong `MerchantId`, AppSwitch API client will return a response like:

      {:return_code=>"04", :reason_code=>"02", :latest_payment_status=>nil, :original_transaction_id=>nil, :original_amount=>"0.00", :num_of_transactions=>"00"}

Please look up the error codes in [Merchant APIs](https://github.com/DanskeBank/MobilePay-AppSwitch/tree/master/Merchant%20APIs).


## Development

After checking out the repo, run `bin/setup` to install dependencies. Then, run `rake spec` to run the tests. You can also run `bin/console` for an interactive prompt that will allow you to experiment.

To install this gem onto your local machine, run `bundle exec rake install`. To release a new version, update the version number in `version.rb`, and then run `bundle exec rake release`, which will create a git tag for the version, push git commits and tags, and push the `.gem` file to [rubygems.org](https://rubygems.org).

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/DanskeBank/MobilePay-AppSwitch. This project is intended to be a safe, welcoming space for collaboration, and contributors are expected to adhere to the [Contributor Covenant](http://contributor-covenant.org) code of conduct.

## License

See the LICENSE.txt file.
