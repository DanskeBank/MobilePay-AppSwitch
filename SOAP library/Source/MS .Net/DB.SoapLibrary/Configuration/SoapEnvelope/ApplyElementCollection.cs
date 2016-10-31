using System.Collections.Generic;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.SoapEnvelope
{
    public class ApplyElementCollection : ConfigurationElementCollection, IEnumerable<ApplyElement>
    {
        private const string CertificateTag = "Apply";

        public override ConfigurationElementCollectionType CollectionType
        {
            get { return ConfigurationElementCollectionType.BasicMap; }
        }

        protected override string ElementName
        {
            get { return CertificateTag; }
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new ApplyElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((ApplyElement)element).Usage;
        }

        public new IEnumerator<ApplyElement> GetEnumerator()
        {
            int count = Count;
            for (int i = 0; i < count; i++)
                yield return BaseGet(i) as ApplyElement;
        }
    }
}
