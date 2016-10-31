package dk.danskebank.mobilePay.soap;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.JAXBException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import dk.danskebank.mobilePay.pki.PKICrypto;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;

public class SoapXMLSoftwareEncrypt implements SoapXMLEncryptInterface {
	
	private byte[] bankEncryptionCert;
	private PKICrypto crypto;


	public SoapXMLSoftwareEncrypt(byte[] bankEncryptionCert, PKICrypto crypto) {
		this.bankEncryptionCert = bankEncryptionCert;
		this.crypto = crypto;
	}
	
	@Override
	public Crypto getCrypto() {
		return crypto;
	}
	
	@Override
	public Document encryptXml(Document document) throws CouldNotEncryptException {

		try {
			Key symmetricKey = GenerateDataEncryptionKey();

			/*
			 * Get a certificate to be used for encrypting the symmetric key. We use
			 * the bank encryption certificate for this.
			 */
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate encryptionCert = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(
							bankEncryptionCert));

			/* select asymmetric algorithm */
			String algorithmURI = XMLCipher.RSA_v1dot5;

			/* do asymmetric encryption of 3des key */
			XMLCipher keyCipher = XMLCipher.getInstance(algorithmURI);
			keyCipher.init(XMLCipher.WRAP_MODE, encryptionCert.getPublicKey());
			EncryptedKey encryptedKey = keyCipher
					.encryptKey(document, symmetricKey);

			/* create keyinfo element containing bank encryption cert */
			KeyInfo keyInfoRsa = new KeyInfo(document);
			X509Data x509data = new org.apache.xml.security.keys.content.X509Data(document);
			x509data.addCertificate(encryptionCert);
			keyInfoRsa.add(x509data);
			encryptedKey.setKeyInfo(keyInfoRsa);

			/* select symmetric algorithm and set it up */
			algorithmURI = XMLCipher.TRIPLEDES;
			XMLCipher xmlCipher = XMLCipher.getInstance(algorithmURI);
			xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

			/*
			 * Set keyinfo inside the encrypted data being prepared. Keyinfo
			 * contains encrypted 3des key
			 */
			EncryptedData encryptedData = xmlCipher.getEncryptedData();
			KeyInfo keyInfo3des = new KeyInfo(document);
			keyInfo3des.add(encryptedKey);
			encryptedData.setKeyInfo(keyInfo3des);

			/* Let us encrypt the root element of the document */
			XPath xPath = XPathFactory.newInstance().newXPath();
			Node nodeToSign = (Node) xPath.evaluate("//*[local-name() = 'Body']",document, XPathConstants.NODE);


			xmlCipher.doFinal(document, (Element) nodeToSign, true);

			return document;
		} catch (GeneralSecurityException e) {
			throw new CouldNotEncryptException(e);
		} catch (XMLSecurityException e) {
			throw new CouldNotEncryptException(e);
		} catch (JAXBException e) {
			throw new CouldNotEncryptException(e);
		} catch (Exception e) {
			throw new CouldNotEncryptException(e);
		}
	}

	protected SecretKey GenerateDataEncryptionKey()
			throws NoSuchAlgorithmException {
		String jceAlgorithmName = "DESede";
		KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
		return keyGenerator.generateKey();
	}


	protected static PublicKey getKey(String key) throws CouldNotEncryptException {
		try {
			byte[] byteKey = Base64.getMimeDecoder().decode(key);
			X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
			KeyFactory kf = KeyFactory.getInstance("RSA");

			return kf.generatePublic(X509publicKey);
		} catch(GeneralSecurityException e){
			throw new CouldNotEncryptException(e);
		}
	}
	
}
