using System;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class AuthorizedClientConfigurationException : ConfigurationErrorsException
    {
        public AuthorizedClientConfigurationException(Exception innerException, string authorizedClientConfigurationTag)
            : base(string.Format(
            "Could not resolve AuthorisedClient configuration section with name=\"{0}\", notice that the configuration is case sensative.\n", authorizedClientConfigurationTag) +
            "\nExample valid app configuration:" +
            "\n<configSections>" +
            "\n  <section name=\"AuthorisedClients\" type=\"DB.SoapLibrary.Configuration.AuthorisedClientsSection, DB.SoapLibrary\" />" +
            "\n</configSections>" +
            "\n<AuthorisedClients>" +
            "\n  <AuthorisedClient name=\"TEST\">" +
            "\n    <Certificate owner=\"Client\" usage=\"Signature\" name=\"PrivateSignCertFile\" path=\"Certificates/TEST/1E0629_sign.pfx\" password=\"Qwe123123\" />" +
            "\n    <Certificate owner=\"DB\" usage=\"Encryption\" name=\"DBCryptCertFile\" path=\"Certificates/TEST/DBGCrypt.crt\" />" +
            "\n  </AuthorisedClient>" +
            "\n</AuthorisedClients>", innerException)
        {
        }
    }
}
