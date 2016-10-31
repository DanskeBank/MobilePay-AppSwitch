package dk.danskebank.mobilePay.pki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.security.cert.CRLReason;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.Data;
import javax.xml.crypto.NodeSetData;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReference;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.bouncycastle.jce.PKCS10CertificationRequest;

import dk.danskebank.mobilePay.pki.exceptions.XmlException;
import org.w3._2001._04.xmlenc_.EncryptedDataType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;
import dk.danskebank.pki.pkifactoryservice.CertificateStatusInType;
import dk.danskebank.pki.pkifactoryservice.CertificateStatusOutType;
import dk.danskebank.pki.pkifactoryservice.CreateCertificateInType;
import dk.danskebank.pki.pkifactoryservice.CreateCertificateOutType;
import dk.danskebank.pki.pkifactoryservice.GetBankCertificateInType;
import dk.danskebank.pki.pkifactoryservice.GetBankCertificateOutType;
import dk.danskebank.pki.pkifactoryservice.GetOwnCertificateListInType;
import dk.danskebank.pki.pkifactoryservice.GetOwnCertificateListOutType;
import dk.danskebank.pki.pkifactoryservice.PKIFactoryServiceFault;
import dk.danskebank.pki.pkifactoryservice.PkiService;
import dk.danskebank.pki.pkifactoryservice.PkiServicePortType;
import dk.danskebank.pki.pkifactoryservice.RenewCertificateInType;
import dk.danskebank.pki.pkifactoryservice.RenewCertificateOutType;
import dk.danskebank.pki.pkifactoryservice.RequestHeaderType;
import dk.danskebank.pki.pkifactoryservice.RevokeCertificateInType;
import dk.danskebank.pki.pkifactoryservice.RevokeCertificateOutType;
import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusRequest;
import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusResponse;
import dk.danskebank.pki.pkifactoryservice.elements.CreateCertificateRequest;
import dk.danskebank.pki.pkifactoryservice.elements.CreateCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.EnvironmentType;
import dk.danskebank.pki.pkifactoryservice.elements.GetBankCertificateRequest;
import dk.danskebank.pki.pkifactoryservice.elements.GetBankCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListRequest;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListResponse;
import dk.danskebank.pki.pkifactoryservice.elements.KeyGeneratorTypeType;
import dk.danskebank.pki.pkifactoryservice.elements.RenewCertificateRequest;
import dk.danskebank.pki.pkifactoryservice.elements.RenewCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateRequest;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateRequest.RevokeAll;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateResponse;

public class PKIImplementation {

	private static EnvironmentType ENVIRONMENT = EnvironmentType.PRODUCTION;

	static {
		/* init security library */
		org.apache.xml.security.Init.init();
	}

	private String customerId;
	private X509Certificate bankRootX509Certificate;
	private PKIXMLSignerInterface xmlSigner;
	private PKIXMLEncryptInterface xmlEncryptor;

	public PKIXMLSignerInterface getXmlSigner() {
		return xmlSigner;
	}

	public void setXmlSigner(PKIXMLSignerInterface xmlSigner) {
		this.xmlSigner = xmlSigner;
	}

	public PKIXMLEncryptInterface getXmlEncryptor() {
		return xmlEncryptor;
	}

	public void setXmlEncryptor(PKIXMLEncryptInterface xmlEncryptor) {
		this.xmlEncryptor = xmlEncryptor;
	}

	public PKIImplementation(String customerId) {
		this.customerId = customerId;
	}

	public GetBankCertificateResponse getBankCertificate() throws PKIFactoryServiceFault {
		if (customerId == null) {
			throw new IllegalStateException("customerId is not set");
		}
		if (bankRootX509Certificate == null) {
			throw new IllegalStateException("bankRootCertificate is not set");
		}

		PkiServicePortType port = getPkiServicePort();
		GetBankCertificateInType in = new GetBankCertificateInType();
		RequestHeaderType requestHeader = null;
		XMLGregorianCalendar calendar = null;
		try {
			requestHeader = getRequestHeader();
			calendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException(e);
		}
		in.setRequestHeader(requestHeader);
		GetBankCertificateRequest getBankCertificateRequest = new GetBankCertificateRequest();
		getBankCertificateRequest.setBankRootCertificateSerialNo(bankRootX509Certificate.getSerialNumber().toString());
		getBankCertificateRequest.setRequestId(requestHeader.getRequestId());
		getBankCertificateRequest.setTimestamp(calendar);
		in.setGetBankCertificateRequest(getBankCertificateRequest);
		GetBankCertificateOutType out = port.getBankCertificate(in);
		verifyBankCertificates(out);

		return out.getGetBankCertificateResponse();
	}

	private void verifyBankCertificates(GetBankCertificateOutType out) {
		try {
			X500Principal bankRootPrincipal = bankRootX509Certificate.getSubjectX500Principal();
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate signX509Cert = (X509Certificate) certFactory.generateCertificate(
					new ByteArrayInputStream(out.getGetBankCertificateResponse().getBankSigningCert()));
			X500Principal issuerPrincipal = signX509Cert.getIssuerX500Principal();
			if (!issuerPrincipal.equals(bankRootPrincipal)) {
				throw new IllegalStateException("The bankSigningCertificate is not issued by the bankRootCertificate");
			}
			X509Certificate encryptX509Cert = (X509Certificate) certFactory.generateCertificate(
					new ByteArrayInputStream(out.getGetBankCertificateResponse().getBankEncryptionCert()));
			issuerPrincipal = encryptX509Cert.getIssuerX500Principal();
			if (!issuerPrincipal.equals(bankRootPrincipal)) {
				throw new IllegalStateException(
						"The bankEncryptionCertificate is not issued by the bankRootCertificate");
			}

		} catch (CertificateException e) {
			throw new IllegalStateException("Could not parse BankRootCertificate", e);
		}

	}

	public CreateCertificateResponse createCertificate(KeyGeneratorTypeType keyGeneratorType, String pin,
			PKCS10CertificationRequest signingRequest, PKCS10CertificationRequest encryptionRequest)
			throws PKIFactoryServiceFault, CouldNotEncryptException {
		if (customerId == null) {
			throw new IllegalStateException("customerId is not set");
		}
		if (signingRequest == null) {
			throw new IllegalStateException("signingCertificate is not set");
		}
		if (encryptionRequest == null) {
			throw new IllegalStateException("encryptionRequest is not set");
		}
		if (pin == null) {
			throw new IllegalStateException("pin is not set");
		}
		if (xmlEncryptor == null) {
			throw new IllegalStateException("xmlEncryptor is not set");
		}
		try {
			PkiServicePortType port = getPkiServicePort();
			CreateCertificateInType in = new CreateCertificateInType();
			CreateCertificateRequest request = new CreateCertificateRequest();
			request.setCustomerId(customerId);
			request.setEnvironment(ENVIRONMENT);
			request.setKeyGeneratorType(keyGeneratorType);
			request.setPIN(pin);
			XMLGregorianCalendar calendar;
			try {
				in.setRequestHeader(getRequestHeader());
				request.setRequestId(in.getRequestHeader().getRequestId());
				calendar = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
			} catch (DatatypeConfigurationException e) {
				throw new IllegalStateException(e);
			}
			request.setTimestamp(calendar);

			request.setSigningCertPKCS10(signingRequest.getEncoded());
			request.setEncryptionCertPKCS10(encryptionRequest.getEncoded());
			Document doc = getDocument(request);
			JAXBElement<EncryptedDataType> data = xmlEncryptor.encryptXml(doc);
			in.setEncryptedData(data.getValue());
			CreateCertificateOutType out = port.createCertificate(in);
			return out.getCreateCertificateResponse();
		} catch (XmlException e) {
			throw new IllegalStateException(e);
		}
	}

	public RenewCertificateResponse renewCertificate(KeyGeneratorTypeType keyGeneratorType,
			PKCS10CertificationRequest signingRequest, PKCS10CertificationRequest encryptionRequest)
			throws PKIFactoryServiceFault, CouldNotSignException, CouldNotEncryptException {
		if (customerId == null) {
			throw new IllegalStateException("customerId is not set");
		}
		if (signingRequest == null) {
			throw new IllegalStateException("signingRequest is not set");
		}
		if (encryptionRequest == null) {
			throw new IllegalStateException("encryptionRequest is not set");
		}
		if (xmlEncryptor == null) {
			throw new IllegalStateException("xmlEncryptor is not set");
		}
		try {
			PkiServicePortType port = getPkiServicePort();
			RenewCertificateInType in = new RenewCertificateInType();
			in.setRequestHeader(getRequestHeader());

			RenewCertificateRequest request = new RenewCertificateRequest();
			request.setCustomerId(customerId);
			request.setEnvironment(ENVIRONMENT);
			request.setKeyGeneratorType(keyGeneratorType);
			request.setRequestId(in.getRequestHeader().getRequestId());
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
			request.setTimestamp(calendar);
			request.setEncryptionCertPKCS10(encryptionRequest.getEncoded());
			request.setSigningCertPKCS10(signingRequest.getEncoded());
			Document doc = getDocument(request);
			doc = xmlSigner.signDocument(doc);
			JAXBElement<EncryptedDataType> encryptXml = xmlEncryptor.encryptXml(doc);
			in.setEncryptedData(encryptXml.getValue());
			RenewCertificateOutType out = port.renewCertificate(in);
			return out.getRenewCertificateResponse();

		} catch (XmlException e) {
			throw new CouldNotSignException(e);
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}

	public RevokeCertificateResponse revokeCertificate(KeyGeneratorTypeType keyGeneratorType,
			ArrayList<String> serialNos, CRLReason reason)
			throws PKIFactoryServiceFault, CouldNotSignException, CouldNotEncryptException {
		if (customerId == null) {
			throw new IllegalStateException("customerId is not set");
		}
		if (serialNos.size() == 0) {
			throw new IllegalStateException("no serialnumbers are set");
		}
		if (xmlEncryptor == null) {
			throw new IllegalStateException("xmlEncryptor is not set");
		}
		try {
			PkiServicePortType port = getPkiServicePort();
			RevokeCertificateInType in = new RevokeCertificateInType();
			in.setRequestHeader(getRequestHeader());

			RevokeCertificateRequest request = new RevokeCertificateRequest();
			request.setCustomerId(customerId);
			request.setEnvironment(ENVIRONMENT);
			request.setKeyGeneratorType(keyGeneratorType);
			request.setRequestId(in.getRequestHeader().getRequestId());
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
			request.setTimestamp(calendar);
			request.setCRLReason(getReasonFromCRLReason(reason));

			request.getCertificateSerialNo().addAll(serialNos);
			in.setRevokeCertificateRequest(request);
			RevokeCertificateOutType out = port.revokeCertificate(in);
			return out.getRevokeCertificateResponse();
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}

	public RevokeCertificateResponse revokeAllCertificates(KeyGeneratorTypeType keyGeneratorType,
			ArrayList<String> exceptSerialNos, CRLReason reason)
			throws PKIFactoryServiceFault, CouldNotSignException, CouldNotEncryptException {
		if (customerId == null) {
			throw new IllegalStateException("customerId is not set");
		}
		if (xmlEncryptor == null) {
			throw new IllegalStateException("xmlEncryptor is not set");
		}
		try {
			PkiServicePortType port = getPkiServicePort();
			RevokeCertificateInType in = new RevokeCertificateInType();
			in.setRequestHeader(getRequestHeader());

			RevokeCertificateRequest request = new RevokeCertificateRequest();
			request.setCustomerId(customerId);
			request.setEnvironment(ENVIRONMENT);
			request.setKeyGeneratorType(keyGeneratorType);
			request.setRequestId(in.getRequestHeader().getRequestId());
			XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
			request.setTimestamp(calendar);
			request.setCRLReason(getReasonFromCRLReason(reason));
			RevokeAll revokeAll = new RevokeAll();
			revokeAll.getExceptCertificateSerialNo().addAll(exceptSerialNos);
			request.setRevokeAll(revokeAll);
			in.setRevokeCertificateRequest(request);
			RevokeCertificateOutType out = port.revokeCertificate(in);
			return out.getRevokeCertificateResponse();
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}

	private BigInteger getReasonFromCRLReason(CRLReason reason) {
		switch (reason) {
		case KEY_COMPROMISE:
			return new BigInteger("1");
		case CA_COMPROMISE:
			return new BigInteger("2");
		case AFFILIATION_CHANGED:
			return new BigInteger("3");
		case SUPERSEDED:
			return new BigInteger("4");
		case CESSATION_OF_OPERATION:
			return new BigInteger("5");
		case PRIVILEGE_WITHDRAWN:
			return new BigInteger("9");
		default:
			return BigInteger.ZERO;
		}
	}

	public GetOwnCertificateListResponse getOwnCertificateList(KeyGeneratorTypeType keyGeneratorType)
			throws PKIFactoryServiceFault, CouldNotSignException {
		try {
			PkiServicePortType port = getPkiServicePort();
			GetOwnCertificateListInType in = new GetOwnCertificateListInType();
			in.setRequestHeader(getRequestHeader());

			GetOwnCertificateListRequest request = new GetOwnCertificateListRequest();
			request.setCustomerId(customerId);
			request.setKeyGeneratorType(keyGeneratorType);
			request.setRequestId(in.getRequestHeader().getRequestId());
			request.setTimestamp(in.getRequestHeader().getTimestamp());
			in.setGetOwnCertificateListRequest(request);
			GetOwnCertificateListOutType out = port.getOwnCertificateList(in);
			return out.getGetOwnCertificateListResponse();
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}

	}

	public CertificateStatusResponse certificateStatus(ArrayList<String> serialNumbers,
			KeyGeneratorTypeType keyGeneratorType) throws CouldNotSignException, PKIFactoryServiceFault {
		try {
			PkiServicePortType port = getPkiServicePort();
			CertificateStatusInType in = new CertificateStatusInType();
			in.setRequestHeader(getRequestHeader());

			CertificateStatusRequest request = new CertificateStatusRequest();
			request.setCustomerId(customerId);
			request.setKeyGeneratorType(keyGeneratorType);
			request.getCertificateSerialNo().addAll(serialNumbers);
			request.setRequestId(in.getRequestHeader().getRequestId());
			request.setTimestamp(in.getRequestHeader().getTimestamp());
			in.setCertificateStatusRequest(request);
			CertificateStatusOutType out = port.certificateStatus(in);
			return out.getCertificateStatusResponse();
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}

	}

	protected RequestHeaderType getRequestHeader() throws DatatypeConfigurationException {
		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setCustomerId(customerId);
		requestHeader.setEnvironment(ENVIRONMENT);
		requestHeader.setInterfaceVersion("1");
		requestHeader.setRequestId((System.currentTimeMillis() % 1000000000) + "");
		requestHeader.setSenderId(customerId);
		XMLGregorianCalendar calendar = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar((GregorianCalendar) Calendar.getInstance());
		requestHeader.setTimestamp(calendar);
		return requestHeader;
	}

	protected Document getDocument(Object request) throws XmlException {
		Document document;
		try {
			JAXBContext context = JAXBContext.newInstance(request.getClass());
			Marshaller m = context.createMarshaller();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();

			m.marshal(request, document);
		} catch (JAXBException | ParserConfigurationException e) {
			throw new XmlException(e);
		}
		return document;
	}

	protected Object getRequestWithSignature(Document document, Object request) throws XmlException {
		try {
			JAXBContext context = JAXBContext.newInstance(request.getClass());
			Unmarshaller m = context.createUnmarshaller();
			return m.unmarshal(document);
		} catch (JAXBException e) {
			throw new XmlException(e);
		}

	}

	protected PkiServicePortType getPkiServicePort() {
		if (this.xmlSigner == null) {
			throw new IllegalStateException("xmlSigner is not set");
		}
		URL wsdlLocation = PkiService.class.getClassLoader().getResource("resources/wsdl/pki/PKIService.wsdl");
		PkiService service = new PkiService(wsdlLocation, PkiService.SERVICE);
		PkiServicePortType port = service.getPkiServiceHttpPort();
		Client client = ClientProxy.getClient(port);
		client.getInInterceptors().add(new StoreMessageInInterceptor());
		client.getInInterceptors().add(new XmlSigInInterceptor(this.xmlSigner));
		client.getInInterceptors().add(new PKIFaultInterceptor());
		client.getOutInterceptors().add(new SAAJOutInterceptor());
		client.getOutInterceptors().add(new XmlSigOutInterceptor(this.xmlSigner));
		HTTPConduit http = (HTTPConduit) client.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

		httpClientPolicy.setConnectionTimeout(36000);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setReceiveTimeout(32000);

		http.setClient(httpClientPolicy);
		return port;
	}

	public static void printDocument(Document doc) {
		System.out.println();
		System.out.println("===================================");
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
			System.out.println();
			System.out.println("===================================");

		} catch (IOException e) {
			System.out.println(e);
		} catch (TransformerException e) {
			System.out.println(e);
		}

	}

	public static class NodeSetURIDereferencer implements URIDereferencer {
		private Node data = null;

		public NodeSetURIDereferencer(Node node) {
			data = node;
		}

		public Data dereference(URIReference ref, XMLCryptoContext ctxt) {
			return new NodeSetData() {
				public Iterator<Node> iterator() {
					return Collections.singletonList(data).iterator();
				}
			};
		}
	}

	public void setBankRootCertificate(byte[] bankRootCertificate) {
		if (bankRootCertificate == null) {
			throw new IllegalStateException("BankRootCertificate is not set");
		}
		X509Certificate x509Cert;
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			x509Cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(bankRootCertificate));
		} catch (CertificateException e) {
			throw new IllegalStateException("Could not parse BankRootCertificate", e);
		}
		this.bankRootX509Certificate = x509Cert;
	}

}
