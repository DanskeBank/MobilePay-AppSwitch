package dk.danskebank.mobilePay.pki;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;

public interface PKIXMLSignerInterface {

	public abstract Document signDocument(Document document)
			throws CouldNotSignException;

	public abstract void validateTrust(X509Certificate cert, PublicKey publicKey)
			throws XMLSecurityException;

	public abstract Crypto getCrypto();

	public abstract void setBankSigningCertificate(byte[] cert);
	public abstract void setBankSigningCertificate(X509Certificate cert);

}	