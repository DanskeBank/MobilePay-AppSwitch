package dk.danskebank.mobilePay.pki;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.UUID;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.rs.security.common.TrustValidator;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;

public class PKIXMLSoftwareSigner implements PKIXMLSignerInterface {
	
	private Crypto crypto;

	private X509Certificate bankSigningCertificate;

	public PKIXMLSoftwareSigner(Crypto crypto) {
		this.setCrypto(crypto);
	}
	
	@Override
	public Document signDocument(Document document)
			throws CouldNotSignException {

		try {
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node nodeToSign = (Node) xPath.evaluate("//*[substring(local-name(), string-length(local-name()) - string-length('Request') +1) = 'Request']",document, XPathConstants.NODE);
			Document docWithSignature = createSignature(nodeToSign);
//			PKIImplementation.printDocument(docWithSignature);
			return docWithSignature;
		} catch (XPathExpressionException e) {
			throw new CouldNotSignException(e);
		}
	}

	private Document createSignature(Node nodeToSign) throws CouldNotSignException {

		try {
			PrivateKey privateKey = getCrypto().getPrivateKey("", null);
			X509Certificate[] issuerCerts = getCrypto().getX509Certificates(null);

			String sigAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
			String pubKeyAlgo = issuerCerts[0].getPublicKey().getAlgorithm();
			if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
				sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_DSA;
			}

			String id = "Ref"+UUID.randomUUID().toString();
			String referenceId = "#" + id;

			XMLSignature sig = prepareEnvelopedSignature((Element)nodeToSign, id, referenceId, sigAlgo);


			sig.addKeyInfo(issuerCerts[0]);
			sig.addKeyInfo(issuerCerts[0].getPublicKey());
			sig.sign(privateKey);
			return sig.getElement().getOwnerDocument();
		} catch (XMLSecurityException e) {
			throw new CouldNotSignException(e);
		}
	}


	private XMLSignature prepareEnvelopedSignature(Element node, 
			String id, 
			String referenceURI,
			String sigAlgo)throws CouldNotSignException {
		try {
			node.setAttribute("xml:id", id);    
			node.setIdAttribute("xml:id", true);

			XMLSignature sig = new XMLSignature(node.getOwnerDocument(), "", sigAlgo, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
			node.appendChild(sig.getElement());
			Transforms transforms = new Transforms(node.getOwnerDocument());
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
			sig.addDocument(referenceURI, transforms, Constants.ALGO_ID_DIGEST_SHA1);
			return sig;
		} catch (XMLSecurityException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	@Override
	public void validateTrust(X509Certificate cert, PublicKey publicKey) throws XMLSecurityException {
		new TrustValidator().validateTrust(getCrypto(), cert,
				publicKey);
		if(this.bankSigningCertificate != null && !this.bankSigningCertificate.equals(cert)){
			throw new XMLSecurityException("The BankSigning certificate does not match the certificate used to sign the message");
		}

	}

	public Crypto getCrypto() {
		return crypto;
	}

	public void setCrypto(Crypto crypto) {
		this.crypto = crypto;
	}

	public void setBankSigningCertificate(X509Certificate bankSigningCertificate) {
		this.bankSigningCertificate = bankSigningCertificate;
	}

	public void setBankSigningCertificate(byte[] bankSigningCertificate) {
		X509Certificate x509Cert;
		try {
			CertificateFactory certFactory = CertificateFactory
					.getInstance("X.509");
			x509Cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(
							bankSigningCertificate));
		} catch (CertificateException e) {
			throw new IllegalStateException(
					"Could not parse BankRootCertificate", e);
		}
		this.bankSigningCertificate = x509Cert;
	}

}
