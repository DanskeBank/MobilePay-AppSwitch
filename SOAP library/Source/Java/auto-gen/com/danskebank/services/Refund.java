
package com.danskebank.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="dacRefund_Input" type="{http://www.danskebank.com/services/}dacRefund_Input"/&gt;
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
    "dacRefundInput"
})
@XmlRootElement(name = "Refund")
public class Refund {

    @XmlElement(name = "dacRefund_Input", required = true)
    protected DacRefundInput dacRefundInput;

    /**
     * Gets the value of the dacRefundInput property.
     * 
     * @return
     *     possible object is
     *     {@link DacRefundInput }
     *     
     */
    public DacRefundInput getDacRefundInput() {
        return dacRefundInput;
    }

    /**
     * Sets the value of the dacRefundInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacRefundInput }
     *     
     */
    public void setDacRefundInput(DacRefundInput value) {
        this.dacRefundInput = value;
    }

}
