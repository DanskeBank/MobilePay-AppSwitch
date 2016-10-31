package dk.danskebank.mobilePay.soap;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.w3c.dom.Document;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;

public interface SoapXMLSignerInterface {

	public abstract void validateTrust(X509Certificate cert, PublicKey publicKey)
			throws XMLSecurityException;

	public abstract Crypto getCrypto();

	public abstract Document signBody(Document document) throws CouldNotSignException;

	public abstract Document signEncryptedBody(Document encryptedDoc) throws CouldNotSignException;

}	