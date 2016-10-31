using System.ServiceModel.Channels;

namespace DB.SoapLibrary.WCF
{
    public class DanskeBankBinding
    {
        /// <summary>
        /// A basic WCF custom binding implementing SOAP 1.2 encoding over HTTPS.
        /// </summary>
        public static Binding CreateCustomBinding()
        {
            // Create an empty BindingElementCollection to populate, a custom binding will be created from it
            BindingElementCollection outputBec = new BindingElementCollection
            {
                new TextMessageEncodingBindingElement {
                    MessageVersion = MessageVersion.Soap12
                },
                new HttpsTransportBindingElement()
            };

            return new CustomBinding(outputBec);
        }
    }
}
