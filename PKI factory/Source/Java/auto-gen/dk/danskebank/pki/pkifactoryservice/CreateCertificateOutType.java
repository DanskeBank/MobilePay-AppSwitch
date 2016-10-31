
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.CreateCertificateResponse;


/**
 * <p>Java class for CreateCertificateOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CreateCertificateOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}CreateCertificateResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateCertificateOutType", propOrder = {
    "responseHeader",
    "createCertificateResponse"
})
public class CreateCertificateOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "CreateCertificateResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected CreateCertificateResponse createCertificateResponse;

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
     * Gets the value of the createCertificateResponse property.
     * 
     * @return
     *     possible object is
     *     {@link CreateCertificateResponse }
     *     
     */
    public CreateCertificateResponse getCreateCertificateResponse() {
        return createCertificateResponse;
    }

    /**
     * Sets the value of the createCertificateResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateCertificateResponse }
     *     
     */
    public void setCreateCertificateResponse(CreateCertificateResponse value) {
        this.createCertificateResponse = value;
    }

}
