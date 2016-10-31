
package dk.danskebank.pki.pkifactoryservice.elements;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnvironmentType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EnvironmentType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="production"/&gt;
 *     &lt;enumeration value="customertest"/&gt;
 *     &lt;enumeration value="systemtest"/&gt;
 *     &lt;enumeration value="test"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EnvironmentType")
@XmlEnum
public enum EnvironmentType {

    @XmlEnumValue("production")
    PRODUCTION("production"),
    @XmlEnumValue("customertest")
    CUSTOMERTEST("customertest"),
    @XmlEnumValue("systemtest")
    SYSTEMTEST("systemtest"),
    @XmlEnumValue("test")
    TEST("test");
    private final String value;

    EnvironmentType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnvironmentType fromValue(String v) {
        for (EnvironmentType c: EnvironmentType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
