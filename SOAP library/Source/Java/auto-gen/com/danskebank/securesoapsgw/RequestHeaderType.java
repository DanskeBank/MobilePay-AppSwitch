
package com.danskebank.securesoapsgw;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RequestHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestHeaderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SenderId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SignerId1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SignerId2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SignerId3" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DBCryptId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RequestId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestHeaderType", propOrder = {
    "senderId",
    "signerId1",
    "signerId2",
    "signerId3",
    "dbCryptId",
    "requestId",
    "timestamp",
    "language"
})
public class RequestHeaderType {

    @XmlElement(name = "SenderId", required = true)
    protected String senderId;
    @XmlElement(name = "SignerId1", required = true)
    protected String signerId1;
    @XmlElement(name = "SignerId2", required = true)
    protected String signerId2;
    @XmlElement(name = "SignerId3", required = true)
    protected String signerId3;
    @XmlElement(name = "DBCryptId", required = true)
    protected String dbCryptId;
    @XmlElement(name = "RequestId", required = true)
    protected String requestId;
    @XmlElement(name = "Timestamp", required = true)
    protected String timestamp;
    @XmlElement(name = "Language", required = true)
    protected String language;

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
     * Gets the value of the signerId1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignerId1() {
        return signerId1;
    }

    /**
     * Sets the value of the signerId1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignerId1(String value) {
        this.signerId1 = value;
    }

    /**
     * Gets the value of the signerId2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignerId2() {
        return signerId2;
    }

    /**
     * Sets the value of the signerId2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignerId2(String value) {
        this.signerId2 = value;
    }

    /**
     * Gets the value of the signerId3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignerId3() {
        return signerId3;
    }

    /**
     * Sets the value of the signerId3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignerId3(String value) {
        this.signerId3 = value;
    }

    /**
     * Gets the value of the dbCryptId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBCryptId() {
        return dbCryptId;
    }

    /**
     * Sets the value of the dbCryptId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBCryptId(String value) {
        this.dbCryptId = value;
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
     *     {@link String }
     *     
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
