
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListRequest;


/**
 * <p>Java class for GetOwnCertificateListInType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetOwnCertificateListInType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}RequestHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}GetOwnCertificateListRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetOwnCertificateListInType", propOrder = {
    "requestHeader",
    "getOwnCertificateListRequest"
})
public class GetOwnCertificateListInType {

    @XmlElement(name = "RequestHeader", required = true)
    protected RequestHeaderType requestHeader;
    @XmlElement(name = "GetOwnCertificateListRequest", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected GetOwnCertificateListRequest getOwnCertificateListRequest;

    /**
     * Gets the value of the requestHeader property.
     * 
     * @return
     *     possible object is
     *     {@link RequestHeaderType }
     *     
     */
    public RequestHeaderType getRequestHeader() {
        return requestHeader;
    }

    /**
     * Sets the value of the requestHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestHeaderType }
     *     
     */
    public void setRequestHeader(RequestHeaderType value) {
        this.requestHeader = value;
    }

    /**
     * Gets the value of the getOwnCertificateListRequest property.
     * 
     * @return
     *     possible object is
     *     {@link GetOwnCertificateListRequest }
     *     
     */
    public GetOwnCertificateListRequest getGetOwnCertificateListRequest() {
        return getOwnCertificateListRequest;
    }

    /**
     * Sets the value of the getOwnCertificateListRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetOwnCertificateListRequest }
     *     
     */
    public void setGetOwnCertificateListRequest(GetOwnCertificateListRequest value) {
        this.getOwnCertificateListRequest = value;
    }

}
