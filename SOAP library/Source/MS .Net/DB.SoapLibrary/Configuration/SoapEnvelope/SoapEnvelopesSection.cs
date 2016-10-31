using System.Configuration;
using System.Linq;

namespace DB.SoapLibrary.Configuration.SoapEnvelope
{
    public class SoapEnvelopesSection : ConfigurationSection
    {

        [ConfigurationProperty("", IsDefaultCollection = true)]
        public SoapEnvelopeElementCollection Elements
        {
            get { return (SoapEnvelopeElementCollection)base[""]; }
        }

        public SoapEnvelopeElement SoapEnvelopeElement(Direction direction)
        {
            return Elements.Single(client => client.Direction == direction);
        }

        public ApplyElement ApplyElement(Direction direction, Usage usage)
        {
            return
                Elements.Single(client => client.Direction == direction)
                    .Elements.Single(apply => apply.Usage == usage);
        }
    }
}
