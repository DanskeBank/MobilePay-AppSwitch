using System.Configuration;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class CertificateElement : ConfigurationElement
    {
        private const string OwnerTag = "owner";
        private const string UsageTag = "usage";
        private const string PathTag = "path";
        private const string PasswordTag = "password";

        [ConfigurationProperty(OwnerTag, IsRequired = true)]
        public Owner Owner
        {
            get { return (Owner)base[OwnerTag]; }
        }

        [ConfigurationProperty(UsageTag, IsRequired = true)]
        public Usage Usage
        {
            get { return (Usage)base[UsageTag]; }
        }

        [ConfigurationProperty(PathTag, IsRequired = true)]
        public string Path
        {
            get { return (string)base[PathTag]; }
        }

        [ConfigurationProperty(PasswordTag, IsRequired = false)]
        public string Password
        {
            get { return (string)base[PasswordTag]; }
        }
    }
}
