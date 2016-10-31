
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateResponse;


/**
 * <p>Java class for RevokeCertificateOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RevokeCertificateOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}RevokeCertificateResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RevokeCertificateOutType", propOrder = {
    "responseHeader",
    "revokeCertificateResponse"
})
public class RevokeCertificateOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "RevokeCertificateResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected RevokeCertificateResponse revokeCertificateResponse;

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
     * Gets the value of the revokeCertificateResponse property.
     * 
     * @return
     *     possible object is
     *     {@link RevokeCertificateResponse }
     *     
     */
    public RevokeCertificateResponse getRevokeCertificateResponse() {
        return revokeCertificateResponse;
    }

    /**
     * Sets the value of the revokeCertificateResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link RevokeCertificateResponse }
     *     
     */
    public void setRevokeCertificateResponse(RevokeCertificateResponse value) {
        this.revokeCertificateResponse = value;
    }

}
