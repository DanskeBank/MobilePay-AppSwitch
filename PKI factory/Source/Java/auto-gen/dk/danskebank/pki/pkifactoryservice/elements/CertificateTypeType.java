
package dk.danskebank.pki.pkifactoryservice.elements;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CertificateTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CertificateTypeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="signing"/&gt;
 *     &lt;enumeration value="encryption"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "CertificateTypeType")
@XmlEnum
public enum CertificateTypeType {

    @XmlEnumValue("signing")
    SIGNING("signing"),
    @XmlEnumValue("encryption")
    ENCRYPTION("encryption");
    private final String value;

    CertificateTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CertificateTypeType fromValue(String v) {
        for (CertificateTypeType c: CertificateTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
