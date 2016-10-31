using System;
using System.Configuration;
using System.Linq;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class AuthorisedClientElement : ConfigurationElement
    {
        const string IdTag = "id";

        [ConfigurationProperty(IdTag, IsRequired = true)]
        public string Id
        {
            get { return (string)base[IdTag]; }
        }

        [ConfigurationProperty("", IsDefaultCollection = true)]
        public CertificateElementCollection Elements
        {
            get { return (CertificateElementCollection)base[""]; }
        }

        public CertificateElement CertificateElement(Owner owner, Usage usage)
        {
            try
            {
                var CertificateElement = Elements.Single(certificate =>
                    certificate.Owner == owner &&
                    certificate.Usage == usage);

                return CertificateElement;
            }
            catch (InvalidOperationException ioe)
            {
                throw new CertificateConfigurationException(ioe, owner, usage);
            }
        }
    }
}
