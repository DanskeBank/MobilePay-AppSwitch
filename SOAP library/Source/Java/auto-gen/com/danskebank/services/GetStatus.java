
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
 *         &lt;element name="dacGetStatusInput" type="{http://www.danskebank.com/services/}dacGetStatusInput"/&gt;
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
    "dacGetStatusInput"
})
@XmlRootElement(name = "GetStatus")
public class GetStatus {

    @XmlElement(required = true)
    protected DacGetStatusInput dacGetStatusInput;

    /**
     * Gets the value of the dacGetStatusInput property.
     * 
     * @return
     *     possible object is
     *     {@link DacGetStatusInput }
     *     
     */
    public DacGetStatusInput getDacGetStatusInput() {
        return dacGetStatusInput;
    }

    /**
     * Sets the value of the dacGetStatusInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacGetStatusInput }
     *     
     */
    public void setDacGetStatusInput(DacGetStatusInput value) {
        this.dacGetStatusInput = value;
    }

}
