
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.RenewCertificateResponse;


/**
 * <p>Java class for RenewCertificateOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RenewCertificateOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}RenewCertificateResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RenewCertificateOutType", propOrder = {
    "responseHeader",
    "renewCertificateResponse"
})
public class RenewCertificateOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "RenewCertificateResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected RenewCertificateResponse renewCertificateResponse;

    /**
     * Gets the value of the responseHeader property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseHeaderType }
     *     
     */
    public ResponseHeaderType getResponseHeader() {
        return responseHeader;
    }

    /**
     * Sets the value of the responseHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseHeaderType }
     *     
     */
    public void setResponseHeader(ResponseHeaderType value) {
        this.responseHeader = value;
    }

    /**
     * Gets the value of the renewCertificateResponse property.
     * 
     * @return
     *     possible object is
     *     {@link RenewCertificateResponse }
     *     
     */
    public RenewCertificateResponse getRenewCertificateResponse() {
        return renewCertificateResponse;
    }

    /**
     * Sets the value of the renewCertificateResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RenewCertificateResponse }
     *     
     */
    public void setRenewCertificateResponse(RenewCertificateResponse value) {
        this.renewCertificateResponse = value;
    }

}
