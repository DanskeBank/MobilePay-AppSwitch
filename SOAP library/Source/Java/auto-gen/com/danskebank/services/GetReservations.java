
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
 *         &lt;element name="dacGetReservationsInput" type="{http://www.danskebank.com/services/}dacGetReservationsInput"/&gt;
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
    "dacGetReservationsInput"
})
@XmlRootElement(name = "GetReservations")
public class GetReservations {

    @XmlElement(required = true)
    protected DacGetReservationsInput dacGetReservationsInput;

    /**
     * Gets the value of the dacGetReservationsInput property.
     * 
     * @return
     *     possible object is
     *     {@link DacGetReservationsInput }
     *     
     */
    public DacGetReservationsInput getDacGetReservationsInput() {
        return dacGetReservationsInput;
    }

    /**
     * Sets the value of the dacGetReservationsInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link DacGetReservationsInput }
     *     
     */
    public void setDacGetReservationsInput(DacGetReservationsInput value) {
        this.dacGetReservationsInput = value;
    }

}
