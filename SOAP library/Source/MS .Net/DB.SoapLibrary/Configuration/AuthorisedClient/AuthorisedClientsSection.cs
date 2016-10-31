using System;
using System.Configuration;
using System.Linq;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class AuthorisedClientsSection : ConfigurationSection
    {
        [ConfigurationProperty("", IsDefaultCollection = true)]
        public AuthorisedClientElementCollection Elements
        {
            get { return (AuthorisedClientElementCollection)base[""]; }
        }

        public static AuthorisedClientsSection Configuration
        {
            get { return ((AuthorisedClientsSection) ConfigurationManager.GetSection("AuthorisedClients")); }
        }

        public AuthorisedClientElement AuthorisedClientElement(string id)
        {
            try
            {
                return Elements.Single(client => client.Id == id);
            }
            catch (InvalidOperationException ioe)
            {
                throw new AuthorizedClientConfigurationException(ioe, id);
            }
        }
    }
}
