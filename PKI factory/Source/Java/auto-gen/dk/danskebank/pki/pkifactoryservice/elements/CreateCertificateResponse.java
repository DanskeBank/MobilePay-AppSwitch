
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
import org.w3._2000._09.xmldsig_.SignatureType;


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
 *         &lt;element name="ReturnCode" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}ReturncodeType"/&gt;
 *         &lt;element name="ReturnText" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}ReturnTextType"/&gt;
 *         &lt;element name="EncryptionCert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="SigningCert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="CACert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="RequestId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}RequestIdType"/&gt;
 *         &lt;element name="Environment" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}EnvironmentType" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/&gt;
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
    "returnCode",
    "returnText",
    "encryptionCert",
    "signingCert",
    "caCert",
    "requestId",
    "environment",
    "signature"
})
@XmlRootElement(name = "CreateCertificateResponse")
public class CreateCertificateResponse {

    @XmlElement(name = "ReturnCode", required = true)
    protected String returnCode;
    @XmlElement(name = "ReturnText", required = true)
    protected String returnText;
    @XmlElement(name = "EncryptionCert", required = true)
    protected byte[] encryptionCert;
    @XmlElement(name = "SigningCert", required = true)
    protected byte[] signingCert;
    @XmlElement(name = "CACert", required = true)
    protected byte[] caCert;
    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
    @XmlElement(name = "Environment")
    @XmlSchemaType(name = "string")
    protected EnvironmentType environment;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the returnCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnCode() {
        return returnCode;
    }

    /**
     * Sets the value of the returnCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnCode(String value) {
        this.returnCode = value;
    }

    /**
     * Gets the value of the returnText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnText() {
        return returnText;
    }

    /**
     * Sets the value of the returnText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnText(String value) {
        this.returnText = value;
    }

    /**
     * Gets the value of the encryptionCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEncryptionCert() {
        return encryptionCert;
    }

    /**
     * Sets the value of the encryptionCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEncryptionCert(byte[] value) {
        this.encryptionCert = value;
    }

    /**
     * Gets the value of the signingCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getSigningCert() {
        return signingCert;
    }

    /**
     * Sets the value of the signingCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setSigningCert(byte[] value) {
        this.signingCert = value;
    }

    /**
     * Gets the value of the caCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCACert() {
        return caCert;
    }

    /**
     * Sets the value of the caCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCACert(byte[] value) {
        this.caCert = value;
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
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
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
