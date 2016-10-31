
package dk.danskebank.pki.pkifactoryservice.elements;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CertificateStatusType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CertificateStatusType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CertificateSerialNo" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateIdType"/&gt;
 *         &lt;element name="CertificateType" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateTypeType"/&gt;
 *         &lt;element name="MatchingCertificateSerialNo" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CertificateIdType"/&gt;
 *         &lt;element name="Status"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="good"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="expires_soon"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="expired"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="revoked"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="revocationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *                           &lt;attribute name="CRLReason" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CRLReasonType" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/choice&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CertificateStatusType", propOrder = {
    "certificateSerialNo",
    "certificateType",
    "matchingCertificateSerialNo",
    "status"
})
public class CertificateStatusType {

    @XmlElement(name = "CertificateSerialNo", required = true)
    protected String certificateSerialNo;
    @XmlElement(name = "CertificateType", required = true)
    @XmlSchemaType(name = "string")
    protected CertificateTypeType certificateType;
    @XmlElement(name = "MatchingCertificateSerialNo", required = true)
    protected String matchingCertificateSerialNo;
    @XmlElement(name = "Status", required = true)
    protected CertificateStatusType.Status status;

    /**
     * Gets the value of the certificateSerialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertificateSerialNo() {
        return certificateSerialNo;
    }

    /**
     * Sets the value of the certificateSerialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertificateSerialNo(String value) {
        this.certificateSerialNo = value;
    }

    /**
     * Gets the value of the certificateType property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateTypeType }
     *     
     */
    public CertificateTypeType getCertificateType() {
        return certificateType;
    }

    /**
     * Sets the value of the certificateType property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateTypeType }
     *     
     */
    public void setCertificateType(CertificateTypeType value) {
        this.certificateType = value;
    }

    /**
     * Gets the value of the matchingCertificateSerialNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatchingCertificateSerialNo() {
        return matchingCertificateSerialNo;
    }

    /**
     * Sets the value of the matchingCertificateSerialNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatchingCertificateSerialNo(String value) {
        this.matchingCertificateSerialNo = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link CertificateStatusType.Status }
     *     
     */
    public CertificateStatusType.Status getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link CertificateStatusType.Status }
     *     
     */
    public void setStatus(CertificateStatusType.Status value) {
        this.status = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;element name="good"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="expires_soon"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="expired"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="revoked"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="revocationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
     *                 &lt;attribute name="CRLReason" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CRLReasonType" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/choice&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "good",
        "expiresSoon",
        "expired",
        "revoked"
    })
    public static class Status {

        protected CertificateStatusType.Status.Good good;
        @XmlElement(name = "expires_soon")
        protected CertificateStatusType.Status.ExpiresSoon expiresSoon;
        protected CertificateStatusType.Status.Expired expired;
        protected CertificateStatusType.Status.Revoked revoked;

        /**
         * Gets the value of the good property.
         * 
         * @return
         *     possible object is
         *     {@link CertificateStatusType.Status.Good }
         *     
         */
        public CertificateStatusType.Status.Good getGood() {
            return good;
        }

        /**
         * Sets the value of the good property.
         * 
         * @param value
         *     allowed object is
         *     {@link CertificateStatusType.Status.Good }
         *     
         */
        public void setGood(CertificateStatusType.Status.Good value) {
            this.good = value;
        }

        /**
         * Gets the value of the expiresSoon property.
         * 
         * @return
         *     possible object is
         *     {@link CertificateStatusType.Status.ExpiresSoon }
         *     
         */
        public CertificateStatusType.Status.ExpiresSoon getExpiresSoon() {
            return expiresSoon;
        }

        /**
         * Sets the value of the expiresSoon property.
         * 
         * @param value
         *     allowed object is
         *     {@link CertificateStatusType.Status.ExpiresSoon }
         *     
         */
        public void setExpiresSoon(CertificateStatusType.Status.ExpiresSoon value) {
            this.expiresSoon = value;
        }

        /**
         * Gets the value of the expired property.
         * 
         * @return
         *     possible object is
         *     {@link CertificateStatusType.Status.Expired }
         *     
         */
        public CertificateStatusType.Status.Expired getExpired() {
            return expired;
        }

        /**
         * Sets the value of the expired property.
         * 
         * @param value
         *     allowed object is
         *     {@link CertificateStatusType.Status.Expired }
         *     
         */
        public void setExpired(CertificateStatusType.Status.Expired value) {
            this.expired = value;
        }

        /**
         * Gets the value of the revoked property.
         * 
         * @return
         *     possible object is
         *     {@link CertificateStatusType.Status.Revoked }
         *     
         */
        public CertificateStatusType.Status.Revoked getRevoked() {
            return revoked;
        }

        /**
         * Sets the value of the revoked property.
         * 
         * @param value
         *     allowed object is
         *     {@link CertificateStatusType.Status.Revoked }
         *     
         */
        public void setRevoked(CertificateStatusType.Status.Revoked value) {
            this.revoked = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Expired {

            @XmlAttribute(name = "expiryDate")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar expiryDate;

            /**
             * Gets the value of the expiryDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getExpiryDate() {
                return expiryDate;
            }

            /**
             * Sets the value of the expiryDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setExpiryDate(XMLGregorianCalendar value) {
                this.expiryDate = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ExpiresSoon {

            @XmlAttribute(name = "expiryDate")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar expiryDate;

            /**
             * Gets the value of the expiryDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getExpiryDate() {
                return expiryDate;
            }

            /**
             * Sets the value of the expiryDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setExpiryDate(XMLGregorianCalendar value) {
                this.expiryDate = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="expiryDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Good {

            @XmlAttribute(name = "expiryDate")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar expiryDate;

            /**
             * Gets the value of the expiryDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getExpiryDate() {
                return expiryDate;
            }

            /**
             * Sets the value of the expiryDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setExpiryDate(XMLGregorianCalendar value) {
                this.expiryDate = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="revocationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
         *       &lt;attribute name="CRLReason" type="{http://danskebank.dk/PKI/PKIFactoryService/elements}CRLReasonType" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Revoked {

            @XmlAttribute(name = "revocationDate")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar revocationDate;
            @XmlAttribute(name = "CRLReason")
            protected BigInteger crlReason;

            /**
             * Gets the value of the revocationDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getRevocationDate() {
                return revocationDate;
            }

            /**
             * Sets the value of the revocationDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setRevocationDate(XMLGregorianCalendar value) {
                this.revocationDate = value;
            }

            /**
             * Gets the value of the crlReason property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getCRLReason() {
                return crlReason;
            }

            /**
             * Sets the value of the crlReason property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setCRLReason(BigInteger value) {
                this.crlReason = value;
            }

        }

    }

}
