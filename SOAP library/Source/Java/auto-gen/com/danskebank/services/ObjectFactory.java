
package com.danskebank.services;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.danskebank.services package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _FaultDetails_QNAME = new QName("http://www.danskebank.com/services/", "FaultDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.danskebank.services
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Refund }
     * 
     */
    public Refund createRefund() {
        return new Refund();
    }

    /**
     * Create an instance of {@link DacRefundInput }
     * 
     */
    public DacRefundInput createDacRefundInput() {
        return new DacRefundInput();
    }

    /**
     * Create an instance of {@link RefundResponse }
     * 
     */
    public RefundResponse createRefundResponse() {
        return new RefundResponse();
    }

    /**
     * Create an instance of {@link DacRefundOutput }
     * 
     */
    public DacRefundOutput createDacRefundOutput() {
        return new DacRefundOutput();
    }

    /**
     * Create an instance of {@link ServiceFault }
     * 
     */
    public ServiceFault createServiceFault() {
        return new ServiceFault();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceFault }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.danskebank.com/services/", name = "FaultDetails")
    public JAXBElement<ServiceFault> createFaultDetails(ServiceFault value) {
        return new JAXBElement<ServiceFault>(_FaultDetails_QNAME, ServiceFault.class, null, value);
    }

}
