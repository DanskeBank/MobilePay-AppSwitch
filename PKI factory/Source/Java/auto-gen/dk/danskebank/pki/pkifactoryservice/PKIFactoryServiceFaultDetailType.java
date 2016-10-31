
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PKIFactoryServiceFaultDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PKIFactoryServiceFaultDetailType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CustomerIdType"/&gt;
 *         &lt;element name="SenderId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CustomerIdType"/&gt;
 *         &lt;element name="RequestId" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}RequestIdType"/&gt;
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="InterfaceVersion" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}InterfaceVersionType"/&gt;
 *         &lt;element name="ReturnCode" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}ReturncodeType"/&gt;
 *         &lt;element name="ReturnText" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}ReturnTextType"/&gt;
 *         &lt;element name="AdditionalReturnText" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}AdditionalReturnTextType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PKIFactoryServiceFaultDetailType", propOrder = {
    "customerId",
    "senderId",
    "requestId",
    "timestamp",
    "interfaceVersion",
    "returnCode",
    "returnText",
    "additionalReturnText"
})
public class PKIFactoryServiceFaultDetailType {

    @XmlElement(name = "CustomerId", required = true)
    protected String customerId;
    @XmlElement(name = "SenderId", required = true)
    protected String senderId;
    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
    @XmlElement(name = "Timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlElement(name = "InterfaceVersion", required = true)
    protected String interfaceVersion;
    @XmlElement(name = "ReturnCode", required = true)
    protected String returnCode;
    @XmlElement(name = "ReturnText", required = true)
    protected String returnText;
    @XmlElement(name = "AdditionalReturnText")
    protected String additionalReturnText;

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
     * Gets the value of the senderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * Sets the value of the senderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderId(String value) {
        this.senderId = value;
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
     * Gets the value of the interfaceVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    /**
     * Sets the value of the interfaceVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceVersion(String value) {
        this.interfaceVersion = value;
    }

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
     * Gets the value of the additionalReturnText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionalReturnText() {
        return additionalReturnText;
    }

    /**
     * Sets the value of the additionalReturnText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionalReturnText(String value) {
        this.additionalReturnText = value;
    }

}
