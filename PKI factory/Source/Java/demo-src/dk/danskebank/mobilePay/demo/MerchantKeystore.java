package dk.danskebank.mobilePay.demo;

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
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import dk.danskebank.mobilePay.pki.PKICrypto;
import dk.danskebank.mobilePay.pki.PKIXMLSoftwareSigner;

import dk.danskebank.mobilePay.pki.PKIXMLSignerInterface;

@SuppressWarnings("deprecation")
public class MerchantKeystore {

    private static final String GENERATED_ENCRYPTION_KEYSTORE_PATH = "keys/ClientGeneratedEncryption.pfx";
    private static final String GENERATED_SIGNING_KEYSTORE_PATH = "keys/ClientGeneratedSigning.pfx";
    private static final String ISSUED_ENCRYPTION_KEYSTORE_PATH = "keys/ClientIssuedEncryption.pfx";
    private static final String ISSUED_SIGNING_KEYSTORE_PATH = "keys/ClientIssuedSigning.pfx";
    private static final String BACKUP_ENCRYPTION_KEYSTORE_PATH = "keys/BackupEncryption.pfx";
    private static final String BACKUP_SIGNING_KEYSTORE_PATH = "keys/BackupSigning.pfx";
    private static final char[] KEYSTORE_PASSWORD = "12345678".toCharArray();
    
/*
 * WANING! 
        User PIN can only be used once during initial certificate issuing.
        User PIN will be invalidated and account locked if wrong PIN is entered 4 times 
 */
    private static final String CUSTOMER_ID = "";
    private static final String FIRST_TIME_PINCODE = "";
    
    private boolean firstTimeIssuingCert;

    private X509Certificate encryptionCert;
    private X509Certificate signingCert;
    private PrivateKey encryptionPrivateKey;
    private PrivateKey signingPrivateKey;
    private String encryptionEntry = "ClientEncryption";
    private String signingEntry = "ClientSigning";
    
    public MerchantKeystore()throws GeneralSecurityException, IOException{
        Security.addProvider(new BouncyCastleProvider());
        init();
    }
    
    private void init() throws GeneralSecurityException, IOException {
        
        System.out.println("Setting up merchant keystores.");

        firstTimeIssuingCert =  ! (new File(ISSUED_SIGNING_KEYSTORE_PATH)).exists();
        
        String encryptionPath;
        String signingPath;

        if(firstTimeIssuingCert){
            encryptionPath = GENERATED_ENCRYPTION_KEYSTORE_PATH;
            signingPath = GENERATED_SIGNING_KEYSTORE_PATH;
        }
        else{
            encryptionPath = ISSUED_ENCRYPTION_KEYSTORE_PATH;
            signingPath = ISSUED_SIGNING_KEYSTORE_PATH;
            
                                            // Store backup files in case issued ones
            java.nio.file.Files.copy(       // become corrupted in the renewal process
                    new java.io.File(ISSUED_ENCRYPTION_KEYSTORE_PATH).toPath(), 
                    new java.io.File(BACKUP_ENCRYPTION_KEYSTORE_PATH).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                    java.nio.file.LinkOption.NOFOLLOW_LINKS);
            java.nio.file.Files.copy( 
                    new java.io.File(ISSUED_SIGNING_KEYSTORE_PATH).toPath(), 
                    new java.io.File(BACKUP_SIGNING_KEYSTORE_PATH).toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                    java.nio.file.StandardCopyOption.COPY_ATTRIBUTES,
                    java.nio.file.LinkOption.NOFOLLOW_LINKS);
        }
        
        PrivateKeyEntry encryptionPrivateKeyEntry = getKeyEntry(encryptionPath);
        encryptionCert = (X509Certificate)encryptionPrivateKeyEntry.getCertificate();
        encryptionPrivateKey = encryptionPrivateKeyEntry.getPrivateKey();
        
        PrivateKeyEntry signingPrivateKeyEntry = getKeyEntry(signingPath);
        signingCert = (X509Certificate)signingPrivateKeyEntry.getCertificate();
        signingPrivateKey = signingPrivateKeyEntry.getPrivateKey();
    }
    
    private PrivateKeyEntry getKeyEntry(String pathToStore) throws GeneralSecurityException, IOException{
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(pathToStore), KEYSTORE_PASSWORD);
        String entry = keystore.aliases().nextElement();
        while(entry.equalsIgnoreCase(BankCertUtil.getEncryptionCertEntry())||
              entry.equalsIgnoreCase(BankCertUtil.getSigningCertEntry())){
            entry = keystore.aliases().nextElement();
        }
        return (KeyStore.PrivateKeyEntry) keystore.getEntry(entry, 
                new KeyStore.PasswordProtection(KEYSTORE_PASSWORD));
    }
    
    public PKIXMLSignerInterface getSoftwareSigner(){
        PKICrypto crypto = new PKICrypto();
        crypto.setPrivateKey(signingPrivateKey);
        if(!firstTimeIssuingCert){
            crypto.setX509Certificate(signingCert);
        }
        
        return new PKIXMLSoftwareSigner(crypto);
    }
    
    public PKCS10CertificationRequest getEncryptionCertRequest() throws GeneralSecurityException{
        return getCertRequest(encryptionCert, encryptionPrivateKey);
    }
    
    public PKCS10CertificationRequest getSigningCertRequest() throws GeneralSecurityException{
        return getCertRequest(signingCert, signingPrivateKey);
    }
    
    private PKCS10CertificationRequest getCertRequest(X509Certificate cert, PrivateKey privateKey)
            throws GeneralSecurityException{
        return new PKCS10CertificationRequest("MD5WithRSA", cert.getSubjectX500Principal(), 
                cert.getPublicKey(), null, privateKey);
    }
    
    public void storeMerchantCertificates(byte[] signingCert, byte[] encryptionCert) 
            throws GeneralSecurityException, IOException {
        
        System.out.println("Storing merchant certificates.");

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        
        X509Certificate encryptionCertificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(encryptionCert));
        X509Certificate signingCertificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(signingCert));
        
        this.encryptionCert = encryptionCertificate;
        this.signingCert = signingCertificate;
        
        KeyStore issuedEncryptionStore = KeyStore.getInstance("PKCS12");
        KeyStore issuedSigningStore = KeyStore.getInstance("PKCS12");

        if(firstTimeIssuingCert){
            issuedEncryptionStore.load(null, null);
            issuedSigningStore.load(null, null);
        }
        else{
            issuedEncryptionStore.load(new FileInputStream(ISSUED_ENCRYPTION_KEYSTORE_PATH), 
                    KEYSTORE_PASSWORD);
            issuedSigningStore.load(new FileInputStream(ISSUED_SIGNING_KEYSTORE_PATH), 
                    KEYSTORE_PASSWORD);
            clearStore(issuedEncryptionStore);
            clearStore(issuedSigningStore);
        }
        
        issuedEncryptionStore.setKeyEntry(encryptionEntry, encryptionPrivateKey, KEYSTORE_PASSWORD, 
                new Certificate[] {encryptionCertificate});
        
        issuedSigningStore.setKeyEntry(signingEntry, signingPrivateKey, KEYSTORE_PASSWORD, 
                new Certificate[] {signingCertificate});

        issuedSigningStore.store(new FileOutputStream(ISSUED_SIGNING_KEYSTORE_PATH), 
                KEYSTORE_PASSWORD);
        issuedEncryptionStore.store(new FileOutputStream(ISSUED_ENCRYPTION_KEYSTORE_PATH), 
                KEYSTORE_PASSWORD);
    }


    private void clearStore(KeyStore keystore) throws GeneralSecurityException {
        while(keystore.aliases().hasMoreElements()){
            keystore.deleteEntry(keystore.aliases().nextElement());
        }
    }

    public boolean isFirstTimeIssuingCert() {
        return firstTimeIssuingCert;
    }

    public String getCustomerId() {
        return CUSTOMER_ID;
    }

    public static String getIssuedSigningKeystorePath() {
        return ISSUED_SIGNING_KEYSTORE_PATH;
    }

    public static String getIssuedEncryptionKeystorePath() {
        return ISSUED_ENCRYPTION_KEYSTORE_PATH;
    }
    
    public static String getGeneratedEncryptionKeystorePath() {
		return GENERATED_ENCRYPTION_KEYSTORE_PATH;
	}

	public static String getGeneratedSigningKeystorePath() {
		return GENERATED_SIGNING_KEYSTORE_PATH;
	}

	public static char[] getKeystorePassword() {
        return KEYSTORE_PASSWORD;
    }

    public String getFirstTimePincode() {
        return FIRST_TIME_PINCODE;
    }

    public X509Certificate getEncryptionCert() {
        return encryptionCert;
    }

    public X509Certificate getSigningCert() {
        return signingCert;
    }
}
