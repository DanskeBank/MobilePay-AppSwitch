using System.Configuration;

namespace DB.SoapLibrary.Configuration.SoapEnvelope
{
    public class ApplyElement : ConfigurationElement
    {
        private const string UsageTag = "usage";
        private const string AlgorithmTag = "algorithm";

        [ConfigurationProperty(UsageTag, IsRequired = true)]
        public Usage Usage
        {
            get { return (Usage)base[UsageTag]; }
        }

        [ConfigurationProperty(AlgorithmTag, IsRequired = true)]
        public Algorithm Algorithm
        {
            get { return (Algorithm)base[AlgorithmTag]; }
        }
    }
}
