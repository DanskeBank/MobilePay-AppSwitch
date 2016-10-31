using System.ServiceModel.Dispatcher;

namespace DB.SoapLibrary.WCF
{
    public class ParameterInspector : IParameterInspector
    {
        public object BeforeCall(string operationName, object[] inputs)
        {
            return operationName;
        }

        public void AfterCall(string operationName, object[] outputs, object returnValue, object correlationState)
        {
        }
    }
}