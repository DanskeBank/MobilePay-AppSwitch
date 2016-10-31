using System;
using System.Configuration;

namespace Db.SoapLibrary.Specification
{
    public class EndpointConfiguration
    {
        public string RootUrl { get { return ConfigurationManager.AppSettings.Get("RootUrl"); } }
        public string GetStatusV02 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("GetStatusV02")); } }
        public string CancelV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("CancelV01")); } }
        public string CaptureV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("CaptureV01")); } }
        public string RefundV02 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("RefundV02")); } }
        public string RequestPaymentV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("RequestPaymentV01")); } }
        public string GetStatusV03 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("GetStatusV03")); } }
        public string CancelV02 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("CancelV02")); } }
        public string CaptureV02 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("CaptureV02")); } }
        public string RefundV03 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("RefundV03")); } }
        public string GetReservationsV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("GetReservationsV01")); } }
        public string SendV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("SendV01")); } }
        public string ReserveV01 { get { return String.Format(RootUrl, ConfigurationManager.AppSettings.Get("ReserveV01")); } }
    }
}
