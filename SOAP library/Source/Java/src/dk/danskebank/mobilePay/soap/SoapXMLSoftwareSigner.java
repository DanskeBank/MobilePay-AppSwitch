package dk.danskebank.mobilePay.soap;

import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.rs.security.common.TrustValidator;
import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;

public class SoapXMLSoftwareSigner implements SoapXMLSignerInterface {

	private Crypto crypto;
	private WSSecSignature builder;
	private WSSecHeader secHeader;


	public SoapXMLSoftwareSigner(Crypto crypto) {
		this.setCrypto(crypto);
	}

	@Override
	public Document signBody(Document document) throws CouldNotSignException {
		try {
			builder = new WSSecSignature();
			builder.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);	
			builder.setSigCanonicalization(WSConstants.C14N_OMIT_COMMENTS);
			builder.setAddInclusivePrefixes(false);
			secHeader = new WSSecHeader(document);
			secHeader.insertSecurityHeader();
			builder.build(document, crypto, secHeader);
			builder.prependBSTElementToHeader(secHeader);
			return document;
		} catch (WSSecurityException e) {
			throw new CouldNotSignException(e);
		}

	}


	@Override
	public Document signEncryptedBody(Document document) throws CouldNotSignException {
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			Element encryptedData = (Element) xPath.evaluate("//*[local-name() = 'EncryptedData']",document, XPathConstants.NODE);
			encryptedData.setAttribute("Id", "Encrypted");    
			encryptedData.setIdAttribute("Id", true);
			
			builder.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);	
			builder.setSigCanonicalization(WSConstants.C14N_OMIT_COMMENTS);
			builder.prepare(document, crypto, secHeader);

			XMLSignatureFactory factory = XMLSignatureFactory.getInstance("DOM");
			Reference reference = factory.newReference("#Encrypted", factory.newDigestMethod(DigestMethod.SHA1, null));

			builder.computeSignature(Collections.singletonList(reference));
			WSEncryptionPart encryptedPart = new WSEncryptionPart("Encrypted");
			builder.addReferencesToSign(Collections.singletonList(encryptedPart), secHeader);
			builder.prependBSTElementToHeader(secHeader);
			return document;
		} catch (WSSecurityException e) {
			throw new CouldNotSignException(e);
		} catch (XPathExpressionException e) {
			throw new CouldNotSignException(e);
		} catch (GeneralSecurityException e) {
			throw new CouldNotSignException(e);
		}
	}

	@Override
	public void validateTrust(X509Certificate cert, PublicKey publicKey) throws XMLSecurityException {
		new TrustValidator().validateTrust(getCrypto(), cert,
				publicKey);
	}

	public Crypto getCrypto() {
		return crypto;
	}

	public void setCrypto(Crypto crypto) {
		this.crypto = crypto;
	}

}
