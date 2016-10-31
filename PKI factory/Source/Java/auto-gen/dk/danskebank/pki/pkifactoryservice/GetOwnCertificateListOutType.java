
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListResponse;


/**
 * <p>Java class for GetOwnCertificateListOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOwnCertificateListOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}GetOwnCertificateListResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOwnCertificateListOutType", propOrder = {
    "responseHeader",
    "getOwnCertificateListResponse"
})
public class GetOwnCertificateListOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "GetOwnCertificateListResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected GetOwnCertificateListResponse getOwnCertificateListResponse;

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
     * Gets the value of the getOwnCertificateListResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetOwnCertificateListResponse }
     *     
     */
    public GetOwnCertificateListResponse getGetOwnCertificateListResponse() {
        return getOwnCertificateListResponse;
    }

    /**
     * Sets the value of the getOwnCertificateListResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetOwnCertificateListResponse }
     *     
     */
    public void setGetOwnCertificateListResponse(GetOwnCertificateListResponse value) {
        this.getOwnCertificateListResponse = value;
    }

}
