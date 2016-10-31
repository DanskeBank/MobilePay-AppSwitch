using System.Configuration;
using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Channels;
using DB.SoapLibrary.Configuration.CertificateStore;
using DB.SoapLibrary.Configuration.SoapEnvelope;
using DB.SoapLibrary.WSSecurity;

namespace DB.SoapLibrary.WCF
{
    public class InspectorBehavior : IEndpointBehavior
    {
        private readonly ClientMessageInspector clientMessageInspector;

        public string LastRequestXML
        {
            get
            {
                return clientMessageInspector.LastRequestXML;
            }
        }

        public string LastResponseXML
        {
            get
            {
                return clientMessageInspector.LastResponseXML;
            }
        }

        public InspectorBehavior()
        {
            var soapConfig = (SoapEnvelopesSection)ConfigurationManager.GetSection("SoapEnvelopes");
            var certStore = CertificateStoreFactory.GetCertificateStore();
            var encryptXml = new EncryptXml(certStore, soapConfig);

            var signXml = new SignXml(certStore, soapConfig);

            clientMessageInspector = new ClientMessageInspector(encryptXml, signXml);
            new ParameterInspector();
        }

        public void AddBindingParameters(ServiceEndpoint endpoint, BindingParameterCollection bindingParameters)
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
            clientRuntime.MessageInspectors.Add(clientMessageInspector);
        }
    }
}
