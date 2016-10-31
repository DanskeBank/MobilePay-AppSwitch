
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
 *         &lt;element name="dacGetReservationsOutput" type="{http://www.danskebank.com/services/}dacGetReservationsOutput"/&gt;
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
    "dacGetReservationsOutput"
})
@XmlRootElement(name = "GetReservationsResponse")
public class GetReservationsResponse {

    @XmlElement(required = true)
    protected DacGetReservationsOutput dacGetReservationsOutput;

    /**
     * Gets the value of the dacGetReservationsOutput property.
     * 
     * @return
     *     possible object is
     *     {@link DacGetReservationsOutput }
     *     
     */
    public DacGetReservationsOutput getDacGetReservationsOutput() {
        return dacGetReservationsOutput;
    }

    /**
     * Sets the value of the dacGetReservationsOutput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacGetReservationsOutput }
     *     
     */
    public void setDacGetReservationsOutput(DacGetReservationsOutput value) {
        this.dacGetReservationsOutput = value;
    }

}
