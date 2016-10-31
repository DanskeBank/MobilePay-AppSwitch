package dk.danskebank.mobilePay.pki.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.Test;

import dk.danskebank.mobilePay.demo.BankCertUtil;
import dk.danskebank.mobilePay.demo.DemoPKIFactory;
import dk.danskebank.mobilePay.demo.MerchantKeystore;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;
import dk.danskebank.pki.pkifactoryservice.PKIFactoryServiceFault;

@SuppressWarnings("deprecation")
public class DemoPKIFactoryTest {

	private File generatedEncryptionStoreFile = new File(MerchantKeystore.getGeneratedEncryptionKeystorePath());
	private File generatedSigningStoreFile = new File(MerchantKeystore.getGeneratedSigningKeystorePath());
	private File issuedEncryptionStoreFile = new File(MerchantKeystore.getIssuedEncryptionKeystorePath());
	private File issuedSigningStoreFile = new File(MerchantKeystore.getIssuedSigningKeystorePath());
	private char[] password = MerchantKeystore.getKeystorePassword();
	private String bankSigningEntry = BankCertUtil.getSigningCertEntry();
	private String bankEncryptionEntry = BankCertUtil.getEncryptionCertEntry();

	@Test
	public void testCreateRenewRevoke() throws GeneralSecurityException, IOException, PKIFactoryServiceFault,
			CouldNotEncryptException, CouldNotSignException {

		setUpKeys();
		assertTrue(generatedSigningStoreFile.exists());
		assertTrue(generatedEncryptionStoreFile.exists());
		assertFalse(issuedSigningStoreFile.exists());
		assertFalse(issuedEncryptionStoreFile.exists());


		DemoPKIFactory.main(new String[] { "-create" });
		assertTrue(issuedSigningStoreFile.exists());
		assertTrue(issuedEncryptionStoreFile.exists());

		X509Certificate issuedSigningCert = testStore(issuedSigningStoreFile, bankSigningEntry);
		X509Certificate issuedEncryptionCert = testStore(issuedEncryptionStoreFile, bankEncryptionEntry);

		deleteGeneratedKeys();
		assertFalse(generatedSigningStoreFile.exists());
		assertFalse(generatedEncryptionStoreFile.exists());
		
		DemoPKIFactory.main(new String[] { "-renew" });
		assertTrue(issuedSigningStoreFile.exists());
		assertTrue(issuedEncryptionStoreFile.exists());

		X509Certificate renewedSigningCert = testStore(issuedSigningStoreFile, bankSigningEntry);
		X509Certificate renewedEncryptionCert = testStore(issuedEncryptionStoreFile, bankEncryptionEntry);

		Date validFromDate = issuedSigningCert.getNotBefore();
		Date newValidFromDate = renewedSigningCert.getNotBefore();
		assertTrue(newValidFromDate.after(validFromDate));
		
		validFromDate = issuedEncryptionCert.getNotBefore();
		newValidFromDate = renewedEncryptionCert.getNotBefore();
		assertTrue(newValidFromDate.after(validFromDate));
		
		
		DemoPKIFactory.main(new String[] { "-status", 
				issuedSigningCert.getSerialNumber().toString(10),
				issuedEncryptionCert.getSerialNumber().toString(10), 
				renewedSigningCert.getSerialNumber().toString(10),
				renewedEncryptionCert.getSerialNumber().toString(10) });

		DemoPKIFactory.main(new String[] { "-revoke", 
				issuedSigningCert.getSerialNumber().toString(10),
				issuedEncryptionCert.getSerialNumber().toString(10), 
				renewedSigningCert.getSerialNumber().toString(10),
				renewedEncryptionCert.getSerialNumber().toString(10) });
	}

	public X509Certificate testStore(File keyStoreFile, String bankEntry) throws GeneralSecurityException, IOException,
			PKIFactoryServiceFault, CouldNotEncryptException, CouldNotSignException {

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream(keyStoreFile), password);
		Certificate bankCertificate = keyStore.getCertificate(bankEntry);
		assertNotNull("Missing bank certificate", bankCertificate);

		String merchantEntry = keyStore.aliases().nextElement();
		if (merchantEntry.equalsIgnoreCase(bankEntry)) {
			merchantEntry = keyStore.aliases().nextElement();
		}
		;
		Entry merchantKeyEntry = keyStore.getEntry(merchantEntry, new KeyStore.PasswordProtection(password));
		assertNotNull("Missing merchant key entry", merchantKeyEntry);

		X509Certificate merchantCertificate = (X509Certificate) keyStore.getCertificate(merchantEntry);
		assertNotNull("Missing merchant certificate", merchantCertificate);

		return merchantCertificate;
	}

	public void setUpKeys() throws IOException, InvalidKeyException, NoSuchAlgorithmException, IllegalStateException,
			NoSuchProviderException, SignatureException, KeyStoreException, CertificateException {
		deleteGeneratedKeys();
		deleteIssuedKeys();
		generateKeyStoreFile(generatedSigningStoreFile, "CN=DemoSigning");
		generateKeyStoreFile(generatedEncryptionStoreFile, "CN=DemoEncryption");
	}

	public void deleteGeneratedKeys() throws IOException {
		if (generatedSigningStoreFile.exists()) {
			generatedSigningStoreFile.delete();
		}
		if (generatedEncryptionStoreFile.exists()) {
			generatedEncryptionStoreFile.delete();
		}
	}
	
	public void deleteIssuedKeys() throws IOException {
		if (issuedSigningStoreFile.exists()) {
			issuedSigningStoreFile.delete();
		}
		if (issuedEncryptionStoreFile.exists()) {
			issuedEncryptionStoreFile.delete();
		}
	}

	public void generateKeyStoreFile(File keystoreFile, String cn)
			throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, NoSuchProviderException,
			SignatureException, KeyStoreException, CertificateException, IOException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		Certificate cert = createCertificate(cn, keyPair.getPublic(), keyPair.getPrivate());

		KeyStore outStore = KeyStore.getInstance("PKCS12");
		outStore.load(null, password);
		outStore.setKeyEntry("mykey", keyPair.getPrivate(), password, new Certificate[] { cert });
		OutputStream outputStream = new FileOutputStream(keystoreFile);
		outStore.store(outputStream, password);
		outputStream.flush();
		outputStream.close();
	}

	private X509Certificate createCertificate(String dn, PublicKey publicKey, PrivateKey privateKey)
			throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchProviderException,
			NoSuchAlgorithmException, SignatureException {
		Security.addProvider(new BouncyCastleProvider());
		X509V3CertificateGenerator certGenerator = new X509V3CertificateGenerator();
		certGenerator.setSerialNumber(BigInteger.valueOf(Math.abs(new Random().nextLong())));
		certGenerator.setIssuerDN(new X509Name(dn));
		certGenerator.setSubjectDN(new X509Name(dn));
		certGenerator.setNotBefore(Calendar.getInstance().getTime());
		certGenerator.setNotAfter(Calendar.getInstance().getTime());
		certGenerator.setPublicKey(publicKey);
		certGenerator.setSignatureAlgorithm("SHA1withRSA");
		X509Certificate certificate = (X509Certificate) certGenerator.generate(privateKey, "BC");
		return certificate;
	}

}
