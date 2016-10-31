
package dk.danskebank.pki.pkifactoryservice.elements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CustomerIdType"/&gt;
 *         &lt;element name="KeyGeneratorType" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}KeyGeneratorTypeType"/&gt;
 *         &lt;element name="EncryptionCertPKCS10" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}Pkcs10requestType"/&gt;
 *         &lt;element name="SigningCertPKCS10" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}Pkcs10requestType"/&gt;
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="RequestId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}RequestIdType"/&gt;
 *         &lt;element name="Environment" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}EnvironmentType" minOccurs="0"/&gt;
 *         &lt;element name="PIN" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}string10"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}id"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "customerId",
    "keyGeneratorType",
    "encryptionCertPKCS10",
    "signingCertPKCS10",
    "timestamp",
    "requestId",
    "environment",
    "pin"
})
@XmlRootElement(name = "CreateCertificateRequest")
public class CreateCertificateRequest {

    @XmlElement(name = "CustomerId", required = true)
    protected String customerId;
    @XmlElement(name = "KeyGeneratorType", required = true)
    @XmlSchemaType(name = "string")
    protected KeyGeneratorTypeType keyGeneratorType;
    @XmlElement(name = "EncryptionCertPKCS10", required = true)
    protected byte[] encryptionCertPKCS10;
    @XmlElement(name = "SigningCertPKCS10", required = true)
    protected byte[] signingCertPKCS10;
    @XmlElement(name = "Timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
    @XmlElement(name = "Environment")
    @XmlSchemaType(name = "string")
    protected EnvironmentType environment;
    @XmlElement(name = "PIN", required = true)
    protected String pin;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the keyGeneratorType property.
     * 
     * @return
     *     possible object is
     *     {@link KeyGeneratorTypeType }
     *     
     */
    public KeyGeneratorTypeType getKeyGeneratorType() {
        return keyGeneratorType;
    }

    /**
     * Sets the value of the keyGeneratorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyGeneratorTypeType }
     *     
     */
    public void setKeyGeneratorType(KeyGeneratorTypeType value) {
        this.keyGeneratorType = value;
    }

    /**
     * Gets the value of the encryptionCertPKCS10 property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEncryptionCertPKCS10() {
        return encryptionCertPKCS10;
    }

    /**
     * Sets the value of the encryptionCertPKCS10 property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEncryptionCertPKCS10(byte[] value) {
        this.encryptionCertPKCS10 = value;
    }

    /**
     * Gets the value of the signingCertPKCS10 property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSigningCertPKCS10() {
        return signingCertPKCS10;
    }

    /**
     * Sets the value of the signingCertPKCS10 property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSigningCertPKCS10(byte[] value) {
        this.signingCertPKCS10 = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the environment property.
     * 
     * @return
     *     possible object is
     *     {@link EnvironmentType }
     *     
     */
    public EnvironmentType getEnvironment() {
        return environment;
    }

    /**
     * Sets the value of the environment property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnvironmentType }
     *     
     */
    public void setEnvironment(EnvironmentType value) {
        this.environment = value;
    }

    /**
     * Gets the value of the pin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPIN() {
        return pin;
    }

    /**
     * Sets the value of the pin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPIN(String value) {
        this.pin = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
