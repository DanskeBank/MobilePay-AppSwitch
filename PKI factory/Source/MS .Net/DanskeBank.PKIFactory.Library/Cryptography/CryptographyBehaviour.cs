using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;


namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public class CryptographyBehavior : IEndpointBehavior
    {
        private IPKIClient _client;

        public CryptographyBehavior(IPKIClient client)
        {
            _client = client;
        }

        public void AddBindingParameters(ServiceEndpoint endpoint, System.ServiceModel.Channels.BindingParameterCollection bindingParameters)
        {
        }

        public void ApplyDispatchBehavior(ServiceEndpoint endpoint, EndpointDispatcher endpointDispatcher)
        {
        }

        public void Validate(ServiceEndpoint endpoint)
        {
        }

        public void ApplyClientBehavior(ServiceEndpoint endpoint, ClientRuntime clientRuntime)
        {
            var encrypt = new EncryptXml(_client);
            clientRuntime.MessageInspectors.Add(new SignAndCryptRequestInspector(encrypt, _client));
            clientRuntime.MessageInspectors.Add(new SignatureValidationInspector(_client));
        }
    }
}
