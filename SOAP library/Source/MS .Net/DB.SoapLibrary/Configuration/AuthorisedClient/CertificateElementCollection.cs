using System.Collections.Generic;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class CertificateElementCollection : ConfigurationElementCollection, IEnumerable<CertificateElement>
    {
        private const string CertificateTag = "Certificate";

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
            return new CertificateElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((CertificateElement)element).Owner + ((CertificateElement)element).Usage.ToString();
        }

        public IEnumerator<CertificateElement> GetEnumerator()
        {
            int count = Count;
            for (int i = 0; i < count; i++)
                yield return BaseGet(i) as CertificateElement;
        }
    }
}
