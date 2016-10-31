package dk.danskebank.mobilePay.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.danskebank.services.DacGetStatusOutput;

import dk.danskebank.mobilePay.pki.PKICrypto;
import dk.danskebank.mobilePay.soap.SoapImplementation;
import dk.danskebank.mobilePay.soap.SoapXMLEncryptInterface;
import dk.danskebank.mobilePay.soap.SoapXMLSignerInterface;
import dk.danskebank.mobilePay.soap.SoapXMLSoftwareEncrypt;
import dk.danskebank.mobilePay.soap.SoapXMLSoftwareSigner;

public class TestSoapFactory {

	private static final String VERIFIED_SIGNING_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientVerifiedSigning.jks";
	
	private static final String VERIFIED_ENCRYPTION_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientVerifiedEncryption.jks";
	
	private static final String KEYSTORE_PASSWORD = "123456";

	private static byte[] bankEncryptionCertificate;

	static String customerId = "4A1234";

	private static SoapXMLSignerInterface signer;
	private static SoapXMLEncryptInterface encrypter;

	public TestSoapFactory() {
	}

	public static void main(String[] args) {		
		try {
			TestSoapFactory factory = new TestSoapFactory();
			factory.setupKeys();

			SoapImplementation soapFactory = new SoapImplementation(customerId);
			soapFactory.setXmlSigner(signer);
			soapFactory.setXmlEncryptor(encrypter);
			
			DacGetStatusOutput statusOutput = soapFactory.getStatus("Merchant", "Order", null,null,null, true);
			System.out.println(statusOutput.getReasonCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setupKeys() throws GeneralSecurityException, IOException {

		Security.addProvider(new BouncyCastleProvider());

		//Setup Signing
		File verifiedKeystore = new File(VERIFIED_SIGNING_KEYSTORE_PATH);
		KeyStore verifiedSigningStore = KeyStore.getInstance("PKCS12");
		verifiedSigningStore.load(new FileInputStream(verifiedKeystore), KEYSTORE_PASSWORD.toCharArray());
		PrivateKeyEntry verifiedSigningPk = (KeyStore.PrivateKeyEntry) verifiedSigningStore.getEntry("PrivateKey", new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
		X509Certificate verifiedSigningCert = (X509Certificate) verifiedSigningStore.getCertificate("VerifiedSigning") ;

		File verifiedEncKeystore = new File(VERIFIED_ENCRYPTION_KEYSTORE_PATH);
		KeyStore verifiedEncryptionStore = KeyStore.getInstance("PKCS12");
		verifiedEncryptionStore.load(new FileInputStream(verifiedEncKeystore), KEYSTORE_PASSWORD.toCharArray());
		bankEncryptionCertificate = ((X509Certificate) verifiedEncryptionStore.getCertificate("BankEncryption")).getEncoded() ;
		X509Certificate encCertificate = (X509Certificate) verifiedEncryptionStore.getCertificate("VerifiedEncryption");
		PrivateKeyEntry encPrivateKey = (PrivateKeyEntry) verifiedEncryptionStore.getEntry("PrivateKey",new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
			
		PKICrypto crypto = new PKICrypto();
		signer = new SoapXMLSoftwareSigner(crypto);
		crypto.setX509Certificate(verifiedSigningCert);
		crypto.setPrivateKey(verifiedSigningPk.getPrivateKey());
		
		PKICrypto encCrypto = new PKICrypto();
		encrypter = new SoapXMLSoftwareEncrypt(bankEncryptionCertificate, encCrypto);
		encCrypto.setX509Certificate(encCertificate);
		encCrypto.setPrivateKey(encPrivateKey.getPrivateKey());
	}
}