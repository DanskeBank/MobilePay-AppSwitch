require 'yaml'

module MobilePay
  # Handle configuration `Options` from YAML file.
  class Options
    # Load `Options` from YAML file.
    def initialize(options_file = nil)
      @options_file = options_file || DEFAULT_OPTIONS_FILE
      @root_key = DEFAULT_ROOT_KEY
    end

    # Get 'key'.
    def [](key, default_value = nil)
      value = local_options[key] || default_value

      return value unless value.nil?

      fail MobilePay::MissingOptionError, "MobilePay Missing configuration option: '#{key}'"
    end

    # Set 'key' and persist changes.
    def []=(key, value)
      local_options[key] = value

      store
    end

    private

    DEFAULT_ROOT_KEY = 'mobile_pay'.freeze

    # Get `Options` below (optional) `root_key`.
    def local_options
      @root_key ? options[@root_key] ||= {} : options
    end

    # Get and cache the `Options` hash.
    def options
      @options ||= load
    end

    DEFAULT_OPTIONS_FILE = './config/options.yml'.freeze

    # Load and parse `Options` YAML file.
    def load
      if File.exist?(@options_file)
        begin
          YAML.parse(File.read(@options_file)).to_ruby
        rescue
          {}
        end
      else
        {}
      end
    end

    # Write updated `Options` to YAML file.
    def store
      File.open @options_file, 'w' do |file|
        file.write YAML.dump(options)
      end
    end
  end
end
