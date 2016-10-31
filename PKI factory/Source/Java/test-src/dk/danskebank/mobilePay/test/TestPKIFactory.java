package dk.danskebank.mobilePay.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import dk.danskebank.mobilePay.pki.PKICrypto;
import dk.danskebank.mobilePay.pki.PKIImplementation;
import dk.danskebank.mobilePay.pki.PKIXMLSignerInterface;
import dk.danskebank.mobilePay.pki.PKIXMLSoftwareEncrypt;
import dk.danskebank.mobilePay.pki.PKIXMLSoftwareSigner;
import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusResponse;
import dk.danskebank.pki.pkifactoryservice.elements.CreateCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.GetBankCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListResponse;
import dk.danskebank.pki.pkifactoryservice.elements.KeyGeneratorTypeType;
import dk.danskebank.pki.pkifactoryservice.elements.RenewCertificateResponse;

public class TestPKIFactory {

	private static final String GENERATED_SIGNING_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientGeneratedSigning.pfx";

	private static final String GENERATED_ENCRYPTION_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientGeneratedEncryption.pfx";

	private static final String VERIFIED_SIGNING_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientVerifiedSigning.jks";
	
	private static final String VERIFIED_ENCRYPTION_KEYSTORE_PATH = "C:/Users/DB/Desktop/test/ClientVerifiedEncryption.jks";

	private static final String FIRST_TIME_PINCODE = "0000";
	
	private static final String KEYSTORE_PASSWORD = "123456";

	static String customerId = "4A1234";
	
	private static String certificateSerialNo;

	public static String bankProdRootCertificate = "-----BEGIN CERTIFICATE-----\n" + 
			"MIIEPzCCAyegAwIBAgIEQjoxcjANBgkqhkiG9w0BAQsFADCBmDEQMA4GA1UEAxMH\n" + 
			"REJHUk9PVDELMAkGA1UEBhMCREsxEzARBgNVBAcTCkNvcGVuaGFnZW4xEDAOBgNV\n" + 
			"BAgTB0Rlbm1hcmsxGjAYBgNVBAoTEURhbnNrZSBCYW5rIEdyb3VwMRowGAYDVQQL\n" + 
			"ExFEYW5za2UgQmFuayBHcm91cDEYMBYGA1UEBRMPNjExMjYyMjgxMTEwMDAyMB4X\n" + 
			"DTEwMTAyNzAwMDAwMFoXDTIwMTAyNzAwMDAwMFowgZgxEDAOBgNVBAMTB0RCR1JP\n" + 
			"T1QxCzAJBgNVBAYTAkRLMRMwEQYDVQQHEwpDb3BlbmhhZ2VuMRAwDgYDVQQIEwdE\n" + 
			"ZW5tYXJrMRowGAYDVQQKExFEYW5za2UgQmFuayBHcm91cDEaMBgGA1UECxMRRGFu\n" + 
			"c2tlIEJhbmsgR3JvdXAxGDAWBgNVBAUTDzYxMTI2MjI4MTExMDAwMjCCASAwDQYJ\n" + 
			"KoZIhvcNAQEBBQADggENADCCAQgCggEBAKWRtTRCXNEn5Hj+tA0vVg8VKUi/HnFg\n" + 
			"ioZW/eyaF4gWvR4PNXXJJOS31VNHnb2SQHPLt3ac+5icH7vLu/OtS5rvnDiDFMg+\n" + 
			"TomVDrur6RtlsZNLnihZiaSaooI49+ERTz6vcCjST7xbfhmC03LUhE8eBKI1U70c\n" + 
			"x/lQ55UQKZvIAIbCVaZEks95VS4uJpwnU4M8glNIVGSvJhIUj/LIkSIcqBiryq/t\n" + 
			"9FRVtRl1gVhwKdi8A5O9hp4t3dBIdOanaup2UEL4lp7izzgt2rkMeuyQ1ZjHsN7L\n" + 
			"mDsfjoFcYx/8CID9LBwRCN2p+YCuoWUjuorrdU/2eit2lNh6ypiF6WECAQOjgZAw\n" + 
			"gY0wHQYDVR0OBBYEFIT65b/ekUlm38WKUsOzt7MgHMdtMA4GA1UdDwEB/wQEAwIB\n" + 
			"BjASBgNVHRMBAf8ECDAGAQH/AgEBMEgGA1UdHwRBMD8wPaA7oDmGN2h0dHA6Ly9v\n" + 
			"bmxpbmUuZGFuc2tlYmFuay5jb20vcGtpL0RCR1JPT1RfMTExMTExMDAwMi5jcmww\n" + 
			"DQYJKoZIhvcNAQELBQADggEBAFjnBPCos7jMMLc3FqyQUMt/HJGKgJDrhYiPZBo9\n" + 
			"njGkH52Urryqw1sbT3wXA1NuzbjHE3xTUD+5jNPCncYqML9xqQjSQkBcb9eJfHZ+\n" + 
			"asiclsO38cSn2qriJPIrCREPOpRVqrGQRbZQhmDiB198hpAdLp38khJon/gXbR7u\n" + 
			"9e0rN8MIM4sXn+lFuQIWiPuv+3llGSoLlIxJnjiQQ9FDjhwN5U+N1N2aHaLc5AHu\n" + 
			"4X/qRutLCy7AYUJZMPBoakPLscYceW2Ztvx4VAyOXgHDdvmz0Bd58XWOs1A9bNMZ\n" + 
			"FeYAB14D9yQRCkXYLhr6sm8HuyqaIkGChFpNb+Gf8gcPvtw=\n" + 
			"-----END CERTIFICATE-----\n" + 
			"";

	private static PKCS10CertificationRequest signingRequest;
	private static PKCS10CertificationRequest encryptRequest;

	public TestPKIFactory() {
	}
	
	public static void main(String[] args) {		
		try {
			TestPKIFactory factory = new TestPKIFactory();
			PKIImplementation pkiFactory = new PKIImplementation(customerId);
			pkiFactory.setBankRootCertificate(bankProdRootCertificate.getBytes());
			PKIXMLSignerInterface signer = factory.setupKeys();
			pkiFactory.setXmlSigner(signer);
			GetBankCertificateResponse bankCertificateOutput = pkiFactory.getBankCertificate();
			signer.setBankSigningCertificate(bankCertificateOutput.getBankSigningCert());
			pkiFactory.setXmlEncryptor(new PKIXMLSoftwareEncrypt(bankCertificateOutput.getBankEncryptionCert()));
			factory.storeBankEncryptionAndSigningCert(bankCertificateOutput.getBankEncryptionCert(), bankCertificateOutput.getBankSigningCert());
			File verifiedKeystore = new File(VERIFIED_SIGNING_KEYSTORE_PATH);
			if(!verifiedKeystore.exists()){
				CreateCertificateResponse createCertificateResponse = pkiFactory.createCertificate(KeyGeneratorTypeType.SOFTWARE, FIRST_TIME_PINCODE, signingRequest, encryptRequest);
				byte[] signingCert = createCertificateResponse.getSigningCert();
				byte[] encryptionCert = createCertificateResponse.getEncryptionCert();
				pkiFactory.setXmlSigner(factory.storeNewKeys(signingCert, encryptionCert, signer));
			} else {
				RenewCertificateResponse renewCertificate = pkiFactory.renewCertificate(KeyGeneratorTypeType.SOFTWARE, signingRequest, encryptRequest);
				byte[] signingCert = renewCertificate.getSigningCert();
				byte[] encryptionCert = renewCertificate.getEncryptionCert();
				pkiFactory.setXmlSigner(factory.storeNewKeys(signingCert, encryptionCert, signer));
			}
			String serialNo = certificateSerialNo;
			CertificateStatusResponse certificateStatus = pkiFactory.certificateStatus(serialNo , KeyGeneratorTypeType.SOFTWARE);
			GetOwnCertificateListResponse ownCertificateList = pkiFactory.getOwnCertificateList(KeyGeneratorTypeType.SOFTWARE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storeBankEncryptionAndSigningCert(byte[] encryptionCert, byte[] signingCert) throws GeneralSecurityException, IOException{
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encryptionCert));

		File keystorePath = new File(VERIFIED_ENCRYPTION_KEYSTORE_PATH);
		KeyStore keystore = java.security.KeyStore.getInstance("PKCS12");
		if(keystorePath.exists()){
			keystore.load(new FileInputStream(VERIFIED_ENCRYPTION_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		} else {
			keystore.load(null, null);
		}
		keystore.setCertificateEntry("BankEncryption", certificate);
		keystore.store(new FileOutputStream(VERIFIED_ENCRYPTION_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

		X509Certificate signingCertificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signingCert));

		KeyStore signingKeystore = java.security.KeyStore.getInstance("PKCS12");
		signingKeystore.load(new FileInputStream(VERIFIED_SIGNING_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		signingKeystore.setCertificateEntry("BankSigning", signingCertificate);
		signingKeystore.store(new FileOutputStream(VERIFIED_SIGNING_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());


	}

	private PKIXMLSignerInterface storeNewKeys(byte[] signingCert, byte[] encryptionCert, PKIXMLSignerInterface signer) throws GeneralSecurityException, IOException {
		String entryName = "sign_entryName";
		KeyStore keystore = java.security.KeyStore.getInstance("PKCS12");
		keystore.load(new java.io.FileInputStream(GENERATED_SIGNING_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(entryName, new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
		PrivateKey myPrivateKey = pkEntry.getPrivateKey();

		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");

		X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signingCert));
		PKICrypto crypto = (PKICrypto) signer.getCrypto();
		crypto.setX509Certificate(certificate);
		crypto.setPrivateKey(myPrivateKey);
		
		certificateSerialNo = certificate.getSerialNumber().toString();

		KeyStore verifiedSigningStore = KeyStore.getInstance("PKCS12");

		File verifiedKeystore = new File(VERIFIED_SIGNING_KEYSTORE_PATH);
		if(verifiedKeystore.exists()){
			verifiedSigningStore.load(new FileInputStream(VERIFIED_SIGNING_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());
		} else {
			verifiedSigningStore.load(null,null);
		}
		verifiedSigningStore.setKeyEntry("PrivateKey", myPrivateKey, KEYSTORE_PASSWORD.toCharArray(), new java.security.cert.Certificate[] {certificate});
		verifiedSigningStore.setCertificateEntry("VerifiedSigning", certificate);
		verifiedSigningStore.store(new FileOutputStream(VERIFIED_SIGNING_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

		X509Certificate encryptionCertificate = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(encryptionCert));

		String encryptionEntry = "crypt_entryName";
		keystore.load(new java.io.FileInputStream(GENERATED_ENCRYPTION_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(encryptionEntry, new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));

		KeyStore verifiedEncryptionStore = KeyStore.getInstance("PKCS12");
		verifiedEncryptionStore.load(new FileInputStream(VERIFIED_ENCRYPTION_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());
		verifiedEncryptionStore.setKeyEntry("PrivateKey", pkEntry.getPrivateKey(), KEYSTORE_PASSWORD.toCharArray(), new java.security.cert.Certificate[] {encryptionCertificate});
		verifiedEncryptionStore.setCertificateEntry("VerifiedEncryption", encryptionCertificate);
		verifiedEncryptionStore.store(new FileOutputStream(VERIFIED_ENCRYPTION_KEYSTORE_PATH), KEYSTORE_PASSWORD.toCharArray());

		return signer;
	}

	private PKIXMLSignerInterface setupKeys() throws GeneralSecurityException, IOException {

		Security.addProvider(new BouncyCastleProvider());

		//Setup Signing
		String entryName = "sign_entryName";
		KeyStore keystore = java.security.KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream(GENERATED_SIGNING_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(entryName, new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
		PrivateKey myPrivateKey = pkEntry.getPrivateKey();
		X509Certificate cert = (X509Certificate) keystore.getCertificate(entryName) ;
		signingRequest = new PKCS10CertificationRequest("MD5WithRSA", cert.getSubjectX500Principal(), cert.getPublicKey(), null, myPrivateKey);

		//Setup Encryption
		String encryptionEntry = "crypt_entryName";
		keystore.load(new FileInputStream(GENERATED_ENCRYPTION_KEYSTORE_PATH),KEYSTORE_PASSWORD.toCharArray());
		X509Certificate encryptionCert = (X509Certificate) keystore.getCertificate(encryptionEntry) ;
		pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(encryptionEntry, new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
		encryptRequest = new PKCS10CertificationRequest("MD5WithRSA", encryptionCert.getSubjectX500Principal(), encryptionCert.getPublicKey(), null, pkEntry.getPrivateKey());

		File verifiedKeystore = new File(VERIFIED_SIGNING_KEYSTORE_PATH);
		if(verifiedKeystore.exists()){
			KeyStore verifiedSigningStore = KeyStore.getInstance("PKCS12");
			verifiedSigningStore.load(new FileInputStream(verifiedKeystore), KEYSTORE_PASSWORD.toCharArray());
			PrivateKeyEntry verifiedSigningPk = (KeyStore.PrivateKeyEntry) verifiedSigningStore.getEntry("PrivateKey", new java.security.KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray()));
			X509Certificate verifiedSigningCert = (X509Certificate) verifiedSigningStore.getCertificate("VerifiedSigning") ;
			X509Certificate bankSigningCert = (X509Certificate) verifiedSigningStore.getCertificate("BankSigning") ;

			PKICrypto crypto = new PKICrypto();
			PKIXMLSoftwareSigner signer = new PKIXMLSoftwareSigner(crypto);
			signer.setBankSigningCertificate(bankSigningCert);

			crypto.setX509Certificate(verifiedSigningCert);
			crypto.setPrivateKey(verifiedSigningPk.getPrivateKey());
			return signer;
		} else {

			PKICrypto crypto = new PKICrypto();
			PKIXMLSoftwareSigner signer = new PKIXMLSoftwareSigner(crypto);
			crypto.setPrivateKey(myPrivateKey);

			return signer;
		}
	}
}