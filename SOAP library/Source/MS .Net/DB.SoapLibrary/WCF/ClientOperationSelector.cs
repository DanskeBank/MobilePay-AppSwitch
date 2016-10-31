using System.Reflection;
using System.ServiceModel.Dispatcher;

namespace DB.SoapLibrary.WCF
{
    public class ClientOperationSelector : IClientOperationSelector
    {
        private readonly IClientOperationSelector wrapped;

        public ClientOperationSelector(IClientOperationSelector wrapped)
        {
            this.wrapped = wrapped;
        }

        public string SelectOperation(MethodBase method, object[] parameters)
        {
            return wrapped.SelectOperation(method, parameters);
        }

        public bool AreParametersRequiredForSelection
        {
            get { return wrapped.AreParametersRequiredForSelection; }
        }
    }
}