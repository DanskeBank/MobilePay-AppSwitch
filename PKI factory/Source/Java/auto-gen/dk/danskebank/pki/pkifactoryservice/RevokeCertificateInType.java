
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateRequest;


/**
 * <p>Java class for RevokeCertificateInType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RevokeCertificateInType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RequestHeader" type="{http://danskebank.dk/PKI/PKIFactoryService}RequestHeaderType"/&gt;
 *         &lt;element ref="{http://danskebank.dk/PKI/PKIFactoryService/elements}RevokeCertificateRequest"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RevokeCertificateInType", propOrder = {
    "requestHeader",
    "revokeCertificateRequest"
})
public class RevokeCertificateInType {

    @XmlElement(name = "RequestHeader", required = true)
    protected RequestHeaderType requestHeader;
    @XmlElement(name = "RevokeCertificateRequest", namespace = "http://danskebank.dk/PKI/PKIFactoryService/elements", required = true)
    protected RevokeCertificateRequest revokeCertificateRequest;

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
     * Gets the value of the revokeCertificateRequest property.
     * 
     * @return
     *     possible object is
     *     {@link RevokeCertificateRequest }
     *     
     */
    public RevokeCertificateRequest getRevokeCertificateRequest() {
        return revokeCertificateRequest;
    }

    /**
     * Sets the value of the revokeCertificateRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link RevokeCertificateRequest }
     *     
     */
    public void setRevokeCertificateRequest(RevokeCertificateRequest value) {
        this.revokeCertificateRequest = value;
    }

}
