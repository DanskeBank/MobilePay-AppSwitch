
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusResponse;


/**
 * <p>Java class for CertificateStatusOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateStatusOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateStatusResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateStatusOutType", propOrder = {
    "responseHeader",
    "certificateStatusResponse"
})
public class CertificateStatusOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "CertificateStatusResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected CertificateStatusResponse certificateStatusResponse;

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
     * Gets the value of the certificateStatusResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateStatusResponse }
     *     
     */
    public CertificateStatusResponse getCertificateStatusResponse() {
        return certificateStatusResponse;
    }

    /**
     * Sets the value of the certificateStatusResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateStatusResponse }
     *     
     */
    public void setCertificateStatusResponse(CertificateStatusResponse value) {
        this.certificateStatusResponse = value;
    }

}
