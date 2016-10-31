using System;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class CertificateConfigurationException : ConfigurationErrorsException
    {
        public CertificateConfigurationException(Exception innerException, Owner owner, Usage usage)
            : base(string.Format(
            "Could not resolve Certificate configuration section with owner=\"{0}\" and usage=\"{1}\", notice that the configuration is case sensative.\n", owner, usage) +
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
