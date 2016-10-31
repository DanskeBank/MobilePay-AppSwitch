
package dk.danskebank.pki.pkifactoryservice.elements;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dk.danskebank.pki.pkifactoryservice.elements package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dk.danskebank.pki.pkifactoryservice.elements
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RevokeCertificateRequest }
     * 
     */
    public RevokeCertificateRequest createRevokeCertificateRequest() {
        return new RevokeCertificateRequest();
    }

    /**
     * Create an instance of {@link CertificateStatusType }
     * 
     */
    public CertificateStatusType createCertificateStatusType() {
        return new CertificateStatusType();
    }

    /**
     * Create an instance of {@link CertificateStatusType.Status }
     * 
     */
    public CertificateStatusType.Status createCertificateStatusTypeStatus() {
        return new CertificateStatusType.Status();
    }

    /**
     * Create an instance of {@link CreateCertificateRequest }
     * 
     */
    public CreateCertificateRequest createCreateCertificateRequest() {
        return new CreateCertificateRequest();
    }

    /**
     * Create an instance of {@link CreateCertificateResponse }
     * 
     */
    public CreateCertificateResponse createCreateCertificateResponse() {
        return new CreateCertificateResponse();
    }

    /**
     * Create an instance of {@link RenewCertificateRequest }
     * 
     */
    public RenewCertificateRequest createRenewCertificateRequest() {
        return new RenewCertificateRequest();
    }

    /**
     * Create an instance of {@link RenewCertificateResponse }
     * 
     */
    public RenewCertificateResponse createRenewCertificateResponse() {
        return new RenewCertificateResponse();
    }

    /**
     * Create an instance of {@link RevokeCertificateRequest.RevokeAll }
     * 
     */
    public RevokeCertificateRequest.RevokeAll createRevokeCertificateRequestRevokeAll() {
        return new RevokeCertificateRequest.RevokeAll();
    }

    /**
     * Create an instance of {@link RevokeCertificateResponse }
     * 
     */
    public RevokeCertificateResponse createRevokeCertificateResponse() {
        return new RevokeCertificateResponse();
    }

    /**
     * Create an instance of {@link CertificateStatusRequest }
     * 
     */
    public CertificateStatusRequest createCertificateStatusRequest() {
        return new CertificateStatusRequest();
    }

    /**
     * Create an instance of {@link CertificateStatusResponse }
     * 
     */
    public CertificateStatusResponse createCertificateStatusResponse() {
        return new CertificateStatusResponse();
    }

    /**
     * Create an instance of {@link GetOwnCertificateListRequest }
     * 
     */
    public GetOwnCertificateListRequest createGetOwnCertificateListRequest() {
        return new GetOwnCertificateListRequest();
    }

    /**
     * Create an instance of {@link GetOwnCertificateListResponse }
     * 
     */
    public GetOwnCertificateListResponse createGetOwnCertificateListResponse() {
        return new GetOwnCertificateListResponse();
    }

    /**
     * Create an instance of {@link GetBankCertificateRequest }
     * 
     */
    public GetBankCertificateRequest createGetBankCertificateRequest() {
        return new GetBankCertificateRequest();
    }

    /**
     * Create an instance of {@link GetBankCertificateResponse }
     * 
     */
    public GetBankCertificateResponse createGetBankCertificateResponse() {
        return new GetBankCertificateResponse();
    }

    /**
     * Create an instance of {@link CertificateStatusType.Status.Good }
     * 
     */
    public CertificateStatusType.Status.Good createCertificateStatusTypeStatusGood() {
        return new CertificateStatusType.Status.Good();
    }

    /**
     * Create an instance of {@link CertificateStatusType.Status.ExpiresSoon }
     * 
     */
    public CertificateStatusType.Status.ExpiresSoon createCertificateStatusTypeStatusExpiresSoon() {
        return new CertificateStatusType.Status.ExpiresSoon();
    }

    /**
     * Create an instance of {@link CertificateStatusType.Status.Expired }
     * 
     */
    public CertificateStatusType.Status.Expired createCertificateStatusTypeStatusExpired() {
        return new CertificateStatusType.Status.Expired();
    }

    /**
     * Create an instance of {@link CertificateStatusType.Status.Revoked }
     * 
     */
    public CertificateStatusType.Status.Revoked createCertificateStatusTypeStatusRevoked() {
        return new CertificateStatusType.Status.Revoked();
    }

}
