
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
 *         &lt;element name="dacGetStatusOutput" type="{http://www.danskebank.com/services/}dacGetStatusOutput"/&gt;
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
    "dacGetStatusOutput"
})
@XmlRootElement(name = "GetStatusResponse")
public class GetStatusResponse {

    @XmlElement(required = true)
    protected DacGetStatusOutput dacGetStatusOutput;

    /**
     * Gets the value of the dacGetStatusOutput property.
     * 
     * @return
     *     possible object is
     *     {@link DacGetStatusOutput }
     *     
     */
    public DacGetStatusOutput getDacGetStatusOutput() {
        return dacGetStatusOutput;
    }

    /**
     * Sets the value of the dacGetStatusOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacGetStatusOutput }
     *     
     */
    public void setDacGetStatusOutput(DacGetStatusOutput value) {
        this.dacGetStatusOutput = value;
    }

}
