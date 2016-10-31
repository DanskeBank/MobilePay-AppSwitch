using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.FaultCompensation
{
    public class XmlSoapProxyReader : XmlTextReader
    {
        // Code originally inspired from http://stackoverflow.com/a/13418860
        // But cleaned up before import

        private List<object> _propNames;

        public XmlSoapProxyReader(Stream input)
            : base(input)
        {
            _propNames = new List<object>();
        }

        public string ProxyNamespace { get; set; }

        private Type _proxyType;
        public Type ProxyType
        {
            get { return _proxyType; }
            set
            {
                _proxyType = value;

                // Read all properties on the provided type
                PropertyInfo[] properties = _proxyType.GetProperties();
                foreach (PropertyInfo p in properties)
                {
                    _propNames.Add(p.Name);
                }
            }
        }

        public override string NamespaceURI
        {
            // Retrieve the "corrected" namespace for the item being read currently
            get
            {
                object localname = LocalName;
                if (_propNames.Contains(localname))
                    return ProxyNamespace;
                else
                    return string.Empty;
            }
        }
    }
}
