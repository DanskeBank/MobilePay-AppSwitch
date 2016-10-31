
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.GetBankCertificateResponse;


/**
 * <p>Java class for GetBankCertificateOutType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetBankCertificateOutType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}ResponseHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}GetBankCertificateResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetBankCertificateOutType", propOrder = {
    "responseHeader",
    "getBankCertificateResponse"
})
public class GetBankCertificateOutType {

    @XmlElement(name = "ResponseHeader", required = true)
    protected ResponseHeaderType responseHeader;
    @XmlElement(name = "GetBankCertificateResponse", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected GetBankCertificateResponse getBankCertificateResponse;

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
     * Gets the value of the getBankCertificateResponse property.
     * 
     * @return
     *     possible object is
     *     {@link GetBankCertificateResponse }
     *     
     */
    public GetBankCertificateResponse getGetBankCertificateResponse() {
        return getBankCertificateResponse;
    }

    /**
     * Sets the value of the getBankCertificateResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBankCertificateResponse }
     *     
     */
    public void setGetBankCertificateResponse(GetBankCertificateResponse value) {
        this.getBankCertificateResponse = value;
    }

}
