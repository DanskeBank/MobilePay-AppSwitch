using System;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Channels;
using DB.SoapLibrary.WCF;

namespace DB.SoapLibrary
{
    public class SoapConnection<TClient, TProxy>
        where TClient : ClientBase<TProxy>
        where TProxy : class
    {
        private readonly TClient client;

        public delegate TOutput InvokeProxy<out TOutput>(TClient client);

        public SoapConnection(string address)
        {
            Binding binding = DanskeBankBinding.CreateCustomBinding();
                
            var endpointAddress = new EndpointAddress(new Uri(address));

            client = (TClient)Activator.CreateInstance(typeof(TClient), binding, endpointAddress);
            client.Endpoint.Behaviors.Add(new InspectorBehavior());

            // Remove VsDebuggerCausalityData element from soap header
            var vs = client.Endpoint.Behaviors.Where(i => i.GetType().Namespace.Contains("VisualStudio")).ToList();
            if (vs.Any())
                client.Endpoint.Behaviors.Remove(vs.FirstOrDefault());
        }

        public TOutput Send<TOutput>(InvokeProxy<TOutput> invokeProxy)
        {
            var result = invokeProxy(client);
            client.Close();

            return result;
        }
    }
}
