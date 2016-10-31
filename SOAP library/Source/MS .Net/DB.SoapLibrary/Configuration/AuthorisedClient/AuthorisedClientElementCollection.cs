using System.Collections.Generic;
using System.Configuration;

namespace DB.SoapLibrary.Configuration.AuthorisedClient
{
    public class AuthorisedClientElementCollection : ConfigurationElementCollection, IEnumerable<AuthorisedClientElement>
    {
        const string AuthorizedClientTag = "AuthorisedClient";

        public override ConfigurationElementCollectionType CollectionType
        {
            get { return ConfigurationElementCollectionType.BasicMap; }
        }

        protected override string ElementName
        {
            get { return AuthorizedClientTag; }
        }

        protected override ConfigurationElement CreateNewElement()
        {
            return new AuthorisedClientElement();
        }

        protected override object GetElementKey(ConfigurationElement element)
        {
            return ((AuthorisedClientElement)element).Id;
        }

        public new IEnumerator<AuthorisedClientElement> GetEnumerator()
        {
            int count = Count;
            for (int i = 0; i < count; i++)
                yield return BaseGet(i) as AuthorisedClientElement;
        }
    }
}
