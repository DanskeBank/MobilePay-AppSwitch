
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
 *         &lt;element name="dacRefund_Output" type="{http://www.danskebank.com/services/}dacRefund_Output"/&gt;
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
    "dacRefundOutput"
})
@XmlRootElement(name = "RefundResponse")
public class RefundResponse {

    @XmlElement(name = "dacRefund_Output", required = true)
    protected DacRefundOutput dacRefundOutput;

    /**
     * Gets the value of the dacRefundOutput property.
     * 
     * @return
     *     possible object is
     *     {@link DacRefundOutput }
     *     
     */
    public DacRefundOutput getDacRefundOutput() {
        return dacRefundOutput;
    }

    /**
     * Sets the value of the dacRefundOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacRefundOutput }
     *     
     */
    public void setDacRefundOutput(DacRefundOutput value) {
        this.dacRefundOutput = value;
    }

}
