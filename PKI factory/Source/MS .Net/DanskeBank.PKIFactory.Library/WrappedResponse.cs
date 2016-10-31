using DanskeBank.PKIFactory.Library.SoapService;

namespace DanskeBank.PKIFactory.Library
{
    public class WrappedResponse<T> where T : class
    {
        #region Properties
        public T Response { get; private set; }
        public PKIFactoryServiceFaultDetailType Error { get; private set; }
        public bool IsSuccessful { get { return Error == null; } }
        #endregion

        #region Constructors

        private WrappedResponse(T model, PKIFactoryServiceFaultDetailType error)
        {
            Response = model;
            Error = error;
        }

        public WrappedResponse(T response)
            : this(response, null)
        {
        }

        public WrappedResponse(PKIFactoryServiceFaultDetailType error)
            : this(null, error)
        {
        }

        #endregion
    }
}
