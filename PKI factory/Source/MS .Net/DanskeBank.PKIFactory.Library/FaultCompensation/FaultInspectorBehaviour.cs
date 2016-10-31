using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;

namespace DanskeBank.PKIFactory.Library.FaultCompensation
{
    public class FaultInspectorBehaviour : IEndpointBehavior
    {
        private SoapFaultInspector myMessageInspector = new SoapFaultInspector();
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
            clientRuntime.MessageInspectors.Add(myMessageInspector);
        }
    }
}
