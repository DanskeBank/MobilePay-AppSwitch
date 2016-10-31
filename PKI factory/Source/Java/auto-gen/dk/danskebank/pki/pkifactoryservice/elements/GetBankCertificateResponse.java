
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
 *         &lt;element name="BankEncryptionCert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="BankSigningCert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="BankRootCert" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateType"/&gt;
 *         &lt;element name="RequestId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}RequestIdType"/&gt;
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
    "bankEncryptionCert",
    "bankSigningCert",
    "bankRootCert",
    "requestId",
    "signature"
})
@XmlRootElement(name = "GetBankCertificateResponse")
public class GetBankCertificateResponse {

    @XmlElement(name = "ReturnCode", required = true)
    protected String returnCode;
    @XmlElement(name = "ReturnText", required = true)
    protected String returnText;
    @XmlElement(name = "BankEncryptionCert", required = true)
    protected byte[] bankEncryptionCert;
    @XmlElement(name = "BankSigningCert", required = true)
    protected byte[] bankSigningCert;
    @XmlElement(name = "BankRootCert", required = true)
    protected byte[] bankRootCert;
    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
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
     * Gets the value of the bankEncryptionCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBankEncryptionCert() {
        return bankEncryptionCert;
    }

    /**
     * Sets the value of the bankEncryptionCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBankEncryptionCert(byte[] value) {
        this.bankEncryptionCert = value;
    }

    /**
     * Gets the value of the bankSigningCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBankSigningCert() {
        return bankSigningCert;
    }

    /**
     * Sets the value of the bankSigningCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBankSigningCert(byte[] value) {
        this.bankSigningCert = value;
    }

    /**
     * Gets the value of the bankRootCert property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getBankRootCert() {
        return bankRootCert;
    }

    /**
     * Sets the value of the bankRootCert property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setBankRootCert(byte[] value) {
        this.bankRootCert = value;
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
