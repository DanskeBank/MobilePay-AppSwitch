using System.Configuration;
using System.Linq;

namespace DB.SoapLibrary.Configuration.SoapEnvelope
{
    public class SoapEnvelopeElement : ConfigurationElement
    {
        const string DirectionTag = "direction";

        [ConfigurationProperty(DirectionTag, IsRequired = true)]
        public Direction Direction
        {
            get { return (Direction)base[DirectionTag]; }
        }

        [ConfigurationProperty("", IsDefaultCollection = true)]
        public ApplyElementCollection Elements
        {
            get { return (ApplyElementCollection)base[""]; }
        }

        public ApplyElement ApplyElement(Usage usage)
        {
            return Elements.Single(apply => apply.Usage == usage);
        }
    }

    public enum Direction
    {
        Outgoing,
        Incoming
    }
}
