
package dk.danskebank.pki.pkifactoryservice.elements;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KeyGeneratorTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="KeyGeneratorTypeType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="HSM"/&gt;
 *     &lt;enumeration value="software"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "KeyGeneratorTypeType")
@XmlEnum
public enum KeyGeneratorTypeType {

    HSM("HSM"),
    @XmlEnumValue("software")
    SOFTWARE("software");
    private final String value;

    KeyGeneratorTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KeyGeneratorTypeType fromValue(String v) {
        for (KeyGeneratorTypeType c: KeyGeneratorTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
