using System;
using System.Security.Cryptography.Xml;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public class MyXmlDsigExcC14NTransform : XmlDsigExcC14NTransform
    {
        public override void LoadInput(Object obj)
        {
            XmlElement root = ((XmlDocument)obj).DocumentElement;
            if (root.Name == "SignedInfo")
                root.RemoveAttribute("xml:id");
            base.LoadInput(obj);
        }
    }
}
