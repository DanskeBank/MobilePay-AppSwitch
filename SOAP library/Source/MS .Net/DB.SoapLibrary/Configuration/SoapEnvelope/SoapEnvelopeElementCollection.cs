using System.Collections.Generic;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.SoapEnvelope
{
    public class SoapEnvelopeElementCollection : ConfigurationElementCollection, IEnumerable<SoapEnvelopeElement>
    {
        const string SoapEnvelopeTag = "SoapEnvelope";

        public override ConfigurationElementCollectionType CollectionType
        {
            get { return ConfigurationElementCollectionType.BasicMap; }
        }

        protected override string ElementName
        {
            get { return SoapEnvelopeTag; }
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new SoapEnvelopeElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((SoapEnvelopeElement)element).Direction;
        }

        public new IEnumerator<SoapEnvelopeElement> GetEnumerator()
        {
            int count = Count;
            for (int i = 0; i < count; i++)
                yield return BaseGet(i) as SoapEnvelopeElement;
        }
    }
}
