using DanskeBank.PKIFactory.Library.SoapService;
using System;

namespace DanskeBank.PKIFactory.Library.FaultCompensation
{
    /// <summary>
    /// Represents a PKI Factory fault
    /// </summary>
    public class PKIFactoryFaultException : Exception
    {
        /// <summary>
        /// Fault details
        /// </summary>
        public PKIFactoryServiceFaultDetailType Details { get; set; }
        public PKIFactoryFaultException(PKIFactoryServiceFaultDetailType faultDetails)
        {
            Details = faultDetails;
        }
    }
}
