package dk.danskebank.mobilePay.pki;

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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.w3._2001._04.xmlenc_.EncryptedDataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;

public class PKIXMLSoftwareEncrypt implements PKIXMLEncryptInterface {
	
	private byte[] cert;


	public PKIXMLSoftwareEncrypt(byte[] bankEncryptionCert) {
		this.cert = bankEncryptionCert;
	}
	
	/* (non-Javadoc)
	 * @see dk.trifork.mobilePay.pki.PKIXMLEncryptInterface#encryptXml(org.w3c.dom.Document)
	 */
	@Override
	public JAXBElement<EncryptedDataType> encryptXml(Document document) throws CouldNotEncryptException {

		try {
			Key symmetricKey = GenerateDataEncryptionKey();

			/*
			 * Get a certificate to be used for encrypting the symmetric key. We use
			 * the bank encryption certificate for this.
			 */
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate encryptionCert = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(
							cert));

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
			Element rootElement = document.getDocumentElement();

			/*
			 * doFinal - do the encryption of the xml. "false" below indicates that
			 * we want to encrypt the element itself, not just the content.
			 */
			xmlCipher.doFinal(document, rootElement, false);

			JAXBContext unMarshalContext = JAXBContext
					.newInstance(EncryptedDataType.class);
			Unmarshaller unmarshaller = unMarshalContext.createUnmarshaller();
			@SuppressWarnings("unchecked")
			JAXBElement<EncryptedDataType> data = (JAXBElement<EncryptedDataType>) unmarshaller
					.unmarshal(document.getFirstChild());
			return data;
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
