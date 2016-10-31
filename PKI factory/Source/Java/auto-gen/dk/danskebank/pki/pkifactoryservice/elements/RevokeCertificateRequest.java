
package dk.danskebank.pki.pkifactoryservice.elements;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="KeyGeneratorType" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}KeyGeneratorTypeType"/&gt;
 *         &lt;element name="CustomerId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CustomerIdType"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="RevokeAll"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                   &lt;sequence&gt;
 *                     &lt;element name="ExceptCertificateSerialNo" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="10" minOccurs="0"/&gt;
 *                   &lt;/sequence&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="CertificateSerialNo" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateIdType" maxOccurs="10"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="CRLReason" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CRLReasonType" minOccurs="0"/&gt;
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
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
    "keyGeneratorType",
    "customerId",
    "revokeAll",
    "certificateSerialNo",
    "crlReason",
    "timestamp",
    "requestId",
    "environment",
    "signature"
})
@XmlRootElement(name = "RevokeCertificateRequest")
public class RevokeCertificateRequest {

    @XmlElement(name = "KeyGeneratorType", required = true)
    @XmlSchemaType(name = "string")
    protected KeyGeneratorTypeType keyGeneratorType;
    @XmlElement(name = "CustomerId", required = true)
    protected String customerId;
    @XmlElement(name = "RevokeAll")
    protected RevokeCertificateRequest.RevokeAll revokeAll;
    @XmlElement(name = "CertificateSerialNo")
    protected List<String> certificateSerialNo;
    @XmlElement(name = "CRLReason")
    protected BigInteger crlReason;
    @XmlElement(name = "Timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
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
     * Gets the value of the revokeAll property.
     * 
     * @return
     *     possible object is
     *     {@link RevokeCertificateRequest.RevokeAll }
     *     
     */
    public RevokeCertificateRequest.RevokeAll getRevokeAll() {
        return revokeAll;
    }

    /**
     * Sets the value of the revokeAll property.
     * 
     * @param value
     *     allowed object is
     *     {@link RevokeCertificateRequest.RevokeAll }
     *     
     */
    public void setRevokeAll(RevokeCertificateRequest.RevokeAll value) {
        this.revokeAll = value;
    }

    /**
     * Gets the value of the certificateSerialNo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the certificateSerialNo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCertificateSerialNo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCertificateSerialNo() {
        if (certificateSerialNo == null) {
            certificateSerialNo = new ArrayList<String>();
        }
        return this.certificateSerialNo;
    }

    /**
     * Gets the value of the crlReason property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCRLReason() {
        return crlReason;
    }

    /**
     * Sets the value of the crlReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCRLReason(BigInteger value) {
        this.crlReason = value;
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
     *         &lt;element name="ExceptCertificateSerialNo" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="10" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "exceptCertificateSerialNo"
    })
    public static class RevokeAll {

        @XmlElement(name = "ExceptCertificateSerialNo")
        protected List<Object> exceptCertificateSerialNo;

        /**
         * Gets the value of the exceptCertificateSerialNo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the exceptCertificateSerialNo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getExceptCertificateSerialNo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Object }
         * 
         * 
         */
        public List<Object> getExceptCertificateSerialNo() {
            if (exceptCertificateSerialNo == null) {
                exceptCertificateSerialNo = new ArrayList<Object>();
            }
            return this.exceptCertificateSerialNo;
        }

    }

}
