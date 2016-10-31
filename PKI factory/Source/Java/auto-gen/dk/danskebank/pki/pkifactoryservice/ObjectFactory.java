
package dk.danskebank.pki.pkifactoryservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.danskebank.pki.pkifactoryservice package. 
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

    private final static QName _CreateCertificateIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "CreateCertificateIn");
    private final static QName _CreateCertificateOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "CreateCertificateOut");
    private final static QName _RenewCertificateIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "RenewCertificateIn");
    private final static QName _RenewCertificateOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "RenewCertificateOut");
    private final static QName _RevokeCertificateIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "RevokeCertificateIn");
    private final static QName _RevokeCertificateOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "RevokeCertificateOut");
    private final static QName _CertificateStatusIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "CertificateStatusIn");
    private final static QName _CertificateStatusOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "CertificateStatusOut");
    private final static QName _GetOwnCertificateListIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "GetOwnCertificateListIn");
    private final static QName _GetOwnCertificateListOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "GetOwnCertificateListOut");
    private final static QName _GetBankCertificateIn_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "GetBankCertificateIn");
    private final static QName _GetBankCertificateOut_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "GetBankCertificateOut");
    private final static QName _PKIFactoryServiceFaultElement_QNAME = new QName("http://danskebank.dk/PKI/PKIFactoryService", "PKIFactoryServiceFaultElement");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.danskebank.pki.pkifactoryservice
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CreateCertificateInType }
     * 
     */
    public CreateCertificateInType createCreateCertificateInType() {
        return new CreateCertificateInType();
    }

    /**
     * Create an instance of {@link CreateCertificateOutType }
     * 
     */
    public CreateCertificateOutType createCreateCertificateOutType() {
        return new CreateCertificateOutType();
    }

    /**
     * Create an instance of {@link RenewCertificateInType }
     * 
     */
    public RenewCertificateInType createRenewCertificateInType() {
        return new RenewCertificateInType();
    }

    /**
     * Create an instance of {@link RenewCertificateOutType }
     * 
     */
    public RenewCertificateOutType createRenewCertificateOutType() {
        return new RenewCertificateOutType();
    }

    /**
     * Create an instance of {@link RevokeCertificateInType }
     * 
     */
    public RevokeCertificateInType createRevokeCertificateInType() {
        return new RevokeCertificateInType();
    }

    /**
     * Create an instance of {@link RevokeCertificateOutType }
     * 
     */
    public RevokeCertificateOutType createRevokeCertificateOutType() {
        return new RevokeCertificateOutType();
    }

    /**
     * Create an instance of {@link CertificateStatusInType }
     * 
     */
    public CertificateStatusInType createCertificateStatusInType() {
        return new CertificateStatusInType();
    }

    /**
     * Create an instance of {@link CertificateStatusOutType }
     * 
     */
    public CertificateStatusOutType createCertificateStatusOutType() {
        return new CertificateStatusOutType();
    }

    /**
     * Create an instance of {@link GetOwnCertificateListInType }
     * 
     */
    public GetOwnCertificateListInType createGetOwnCertificateListInType() {
        return new GetOwnCertificateListInType();
    }

    /**
     * Create an instance of {@link GetOwnCertificateListOutType }
     * 
     */
    public GetOwnCertificateListOutType createGetOwnCertificateListOutType() {
        return new GetOwnCertificateListOutType();
    }

    /**
     * Create an instance of {@link GetBankCertificateInType }
     * 
     */
    public GetBankCertificateInType createGetBankCertificateInType() {
        return new GetBankCertificateInType();
    }

    /**
     * Create an instance of {@link GetBankCertificateOutType }
     * 
     */
    public GetBankCertificateOutType createGetBankCertificateOutType() {
        return new GetBankCertificateOutType();
    }

    /**
     * Create an instance of {@link PKIFactoryServiceFaultDetailType }
     * 
     */
    public PKIFactoryServiceFaultDetailType createPKIFactoryServiceFaultDetailType() {
        return new PKIFactoryServiceFaultDetailType();
    }

    /**
     * Create an instance of {@link RequestHeaderType }
     * 
     */
    public RequestHeaderType createRequestHeaderType() {
        return new RequestHeaderType();
    }

    /**
     * Create an instance of {@link ResponseHeaderType }
     * 
     */
    public ResponseHeaderType createResponseHeaderType() {
        return new ResponseHeaderType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCertificateInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "CreateCertificateIn")
    public JAXBElement<CreateCertificateInType> createCreateCertificateIn(CreateCertificateInType value) {
        return new JAXBElement<CreateCertificateInType>(_CreateCertificateIn_QNAME, CreateCertificateInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateCertificateOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "CreateCertificateOut")
    public JAXBElement<CreateCertificateOutType> createCreateCertificateOut(CreateCertificateOutType value) {
        return new JAXBElement<CreateCertificateOutType>(_CreateCertificateOut_QNAME, CreateCertificateOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RenewCertificateInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "RenewCertificateIn")
    public JAXBElement<RenewCertificateInType> createRenewCertificateIn(RenewCertificateInType value) {
        return new JAXBElement<RenewCertificateInType>(_RenewCertificateIn_QNAME, RenewCertificateInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RenewCertificateOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "RenewCertificateOut")
    public JAXBElement<RenewCertificateOutType> createRenewCertificateOut(RenewCertificateOutType value) {
        return new JAXBElement<RenewCertificateOutType>(_RenewCertificateOut_QNAME, RenewCertificateOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RevokeCertificateInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "RevokeCertificateIn")
    public JAXBElement<RevokeCertificateInType> createRevokeCertificateIn(RevokeCertificateInType value) {
        return new JAXBElement<RevokeCertificateInType>(_RevokeCertificateIn_QNAME, RevokeCertificateInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RevokeCertificateOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "RevokeCertificateOut")
    public JAXBElement<RevokeCertificateOutType> createRevokeCertificateOut(RevokeCertificateOutType value) {
        return new JAXBElement<RevokeCertificateOutType>(_RevokeCertificateOut_QNAME, RevokeCertificateOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateStatusInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "CertificateStatusIn")
    public JAXBElement<CertificateStatusInType> createCertificateStatusIn(CertificateStatusInType value) {
        return new JAXBElement<CertificateStatusInType>(_CertificateStatusIn_QNAME, CertificateStatusInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CertificateStatusOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "CertificateStatusOut")
    public JAXBElement<CertificateStatusOutType> createCertificateStatusOut(CertificateStatusOutType value) {
        return new JAXBElement<CertificateStatusOutType>(_CertificateStatusOut_QNAME, CertificateStatusOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOwnCertificateListInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "GetOwnCertificateListIn")
    public JAXBElement<GetOwnCertificateListInType> createGetOwnCertificateListIn(GetOwnCertificateListInType value) {
        return new JAXBElement<GetOwnCertificateListInType>(_GetOwnCertificateListIn_QNAME, GetOwnCertificateListInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOwnCertificateListOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "GetOwnCertificateListOut")
    public JAXBElement<GetOwnCertificateListOutType> createGetOwnCertificateListOut(GetOwnCertificateListOutType value) {
        return new JAXBElement<GetOwnCertificateListOutType>(_GetOwnCertificateListOut_QNAME, GetOwnCertificateListOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBankCertificateInType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "GetBankCertificateIn")
    public JAXBElement<GetBankCertificateInType> createGetBankCertificateIn(GetBankCertificateInType value) {
        return new JAXBElement<GetBankCertificateInType>(_GetBankCertificateIn_QNAME, GetBankCertificateInType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetBankCertificateOutType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "GetBankCertificateOut")
    public JAXBElement<GetBankCertificateOutType> createGetBankCertificateOut(GetBankCertificateOutType value) {
        return new JAXBElement<GetBankCertificateOutType>(_GetBankCertificateOut_QNAME, GetBankCertificateOutType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PKIFactoryServiceFaultDetailType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://danskebank.dk/PKI/PKIFactoryService", name = "PKIFactoryServiceFaultElement")
    public JAXBElement<PKIFactoryServiceFaultDetailType> createPKIFactoryServiceFaultElement(PKIFactoryServiceFaultDetailType value) {
        return new JAXBElement<PKIFactoryServiceFaultDetailType>(_PKIFactoryServiceFaultElement_QNAME, PKIFactoryServiceFaultDetailType.class, null, value);
    }

}
