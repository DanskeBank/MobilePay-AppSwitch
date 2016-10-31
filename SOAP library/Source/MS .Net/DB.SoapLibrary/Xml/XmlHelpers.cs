using System;
using System.Xml;

namespace DB.SoapLibrary.Xml
{
    internal static class XmlHelpers
    {
        public static void AddTimestamp(XmlDocument xmlDoc)
        {
            XmlElement security = xmlDoc.GetElementsByTagName("h:Security")[0] as XmlElement;
            var timestamp = xmlDoc.CreateElement("Timestamp", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            var created = xmlDoc.CreateElement("Created", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            var dateTime = DateTime.Now;
            created.InnerText = dateTime.ToString("yyyy-MM-ddTHH:mm:ssZ");
            var expires = xmlDoc.CreateElement("Expires", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            expires.InnerText = dateTime.AddMinutes(5).ToString("yyyy-MM-ddTHH:mm:ssZ");
            timestamp.AppendChild(created);
            timestamp.AppendChild(expires);
            security.AppendChild(timestamp);
        }

        public static void AddNamespacesToEnvelope(XmlDocument xmlDoc)
        {
            XmlElement envelope = xmlDoc.GetElementsByTagName("s:Envelope")[0] as XmlElement;

            // Create WSSecurity namespace attribute
            var wssecurityAttr = xmlDoc.CreateAttribute("xmlns", "oas", "http://www.w3.org/2000/xmlns/");
            wssecurityAttr.Value = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

            // Create secure soap service gateway namespace attribute
            var secsoapsgwAttr = xmlDoc.CreateAttribute("xmlns", "sec", "http://www.w3.org/2000/xmlns/");
            secsoapsgwAttr.Value = "http://www.danskebank.com/SecureSoapSGW";

            // Create danske bank services namespace
            var danskeservicesAttr = xmlDoc.CreateAttribute("xmlns", "ser", "http://www.w3.org/2000/xmlns/");
            danskeservicesAttr.Value = "http://www.danskebank.com/services/";

            envelope.Attributes.Append(wssecurityAttr);
            envelope.Attributes.Append(secsoapsgwAttr);
            envelope.Attributes.Append(danskeservicesAttr);
        }

        public static void AddNamespaceToBody(XmlDocument xmlDoc)
        {
            XmlElement bodyElement = xmlDoc.GetElementsByTagName("s:Body")[0] as XmlElement;
            var nsAttr = xmlDoc.CreateAttribute("xmlns", "wsu", "http://www.w3.org/2000/xmlns/");
            nsAttr.Value = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
            bodyElement.Attributes.Append(nsAttr);
        }

        public static void ReplaceIdWithNamespaceId(XmlDocument xmlDoc)
        {
            XmlElement bodyElement = xmlDoc.GetElementsByTagName("s:Body")[0] as XmlElement;
            var idAttrValue = bodyElement.Attributes["Id"].Value;
            bodyElement.RemoveAttribute("Id");

            var idReplaceAttr = xmlDoc.CreateAttribute("wsu", "Id",
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
            idReplaceAttr.Value = idAttrValue;
            bodyElement.Attributes.Append(idReplaceAttr);
        }

        public static string AddIDToElement(XmlDocument xmlDoc, string elementName, string elementNamespace, string id)
        {
            XmlAttribute idAttribute = xmlDoc.CreateAttribute("Id");
            idAttribute.Value = id;

            XmlElement bodyElement = xmlDoc.GetElementsByTagName(elementName, elementNamespace)[0] as XmlElement;
            bodyElement.Attributes.Append(idAttribute);

            return idAttribute.Value;
        }
    }
}
