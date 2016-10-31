using System.Security.Cryptography.Xml;
using System.Xml;

namespace DanskeBank.PKIFactory.Library.Cryptography
{
    public class SignedXmlWithId : SignedXml
    {
        public SignedXmlWithId(XmlDocument xml)
            : base(xml)
        {
        }

        public SignedXmlWithId(XmlElement xmlElement)
            : base(xmlElement)
        {
        }

        public override XmlElement GetIdElement(XmlDocument doc, string id)
        {
            XmlElement idElem = null;

            string[] xpaths = new[] { "//*[@id='{0}']", "//*[@xml:id='{0}']" };

            foreach (var path in xpaths)
            {
                idElem = doc.SelectSingleNode(string.Format(path, id), new XmlNamespaceManager(doc.NameTable)) as XmlElement;
                if (idElem != null)
                    return idElem;
            }
            return null;
        }
    }
}
