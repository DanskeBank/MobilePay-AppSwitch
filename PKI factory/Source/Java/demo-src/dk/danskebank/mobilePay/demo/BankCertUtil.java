package dk.danskebank.mobilePay.demo;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import dk.danskebank.pki.pkifactoryservice.elements.GetBankCertificateResponse;

public class BankCertUtil {
    
    private static byte[] encryptionCert;
    
    private static byte[] signingCert;
    
    private static String encryptionCertEntry = "BankEncryption";
    
    private static String signingCertEntry = "BankSigning";

    public static byte[] getEncryptionCert() {
        return encryptionCert;
    }
    
    public static byte[] getSigningCert() {
        return signingCert;
    }
    
    public static String getEncryptionCertEntry() {
        return encryptionCertEntry;
    }

    public static String getSigningCertEntry() {
        return signingCertEntry;
    }

    public static void setBankCerts(GetBankCertificateResponse bankCertificateOutput) {
        signingCert = bankCertificateOutput.getBankSigningCert();
        encryptionCert = bankCertificateOutput.getBankEncryptionCert();
    }
    
    public static void storeBankCertificates(String pathToEncryptionStore, String pathToSigningStore, char[] password,
            boolean newStores) throws GeneralSecurityException, IOException{
        
        System.out.println("Storing bank certificates.");
        
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        KeyStore encryptionStore = KeyStore.getInstance("PKCS12");
        KeyStore signingStore = KeyStore.getInstance("PKCS12");

        if(newStores){
            encryptionStore.load(null, null);
            signingStore.load(null, null);
        } else {
            encryptionStore.load(new FileInputStream(pathToEncryptionStore), password);
            signingStore.load(new FileInputStream(pathToSigningStore), password);
        }

        X509Certificate encryptionCertificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(encryptionCert));
        X509Certificate signingCertificate = (X509Certificate) certFactory.generateCertificate(
                new ByteArrayInputStream(signingCert));
        
        encryptionStore.setCertificateEntry(encryptionCertEntry, encryptionCertificate);
        encryptionStore.store(new FileOutputStream(pathToEncryptionStore), password);

        signingStore.setCertificateEntry(signingCertEntry, signingCertificate);
        signingStore.store(new FileOutputStream(pathToSigningStore), password);
    }
    
      public static byte[] getProdRootCert() {
    	  StringBuilder sb = new StringBuilder();
    	  sb.append("-----BEGIN CERTIFICATE-----\n")
    	  .append("MIIEPzCCAyegAwIBAgIEQjoxcjANBgkqhkiG9w0BAQsFADCBmDEQMA4GA1UEAxMH\n")
    	  .append("REJHUk9PVDELMAkGA1UEBhMCREsxEzARBgNVBAcTCkNvcGVuaGFnZW4xEDAOBgNV\n")
    	  .append("BAgTB0Rlbm1hcmsxGjAYBgNVBAoTEURhbnNrZSBCYW5rIEdyb3VwMRowGAYDVQQL\n")
    	  .append("ExFEYW5za2UgQmFuayBHcm91cDEYMBYGA1UEBRMPNjExMjYyMjgxMTEwMDAyMB4X\n")
    	  .append("DTEwMTAyNzAwMDAwMFoXDTIwMTAyNzAwMDAwMFowgZgxEDAOBgNVBAMTB0RCR1JP\n") 
    	  .append("T1QxCzAJBgNVBAYTAkRLMRMwEQYDVQQHEwpDb3BlbmhhZ2VuMRAwDgYDVQQIEwdE\n") 
    	  .append("ZW5tYXJrMRowGAYDVQQKExFEYW5za2UgQmFuayBHcm91cDEaMBgGA1UECxMRRGFu\n") 
    	  .append("c2tlIEJhbmsgR3JvdXAxGDAWBgNVBAUTDzYxMTI2MjI4MTExMDAwMjCCASAwDQYJ\n") 
    	  .append("KoZIhvcNAQEBBQADggENADCCAQgCggEBAKWRtTRCXNEn5Hj+tA0vVg8VKUi/HnFg\n") 
    	  .append("ioZW/eyaF4gWvR4PNXXJJOS31VNHnb2SQHPLt3ac+5icH7vLu/OtS5rvnDiDFMg+\n") 
    	  .append("TomVDrur6RtlsZNLnihZiaSaooI49+ERTz6vcCjST7xbfhmC03LUhE8eBKI1U70c\n") 
    	  .append("x/lQ55UQKZvIAIbCVaZEks95VS4uJpwnU4M8glNIVGSvJhIUj/LIkSIcqBiryq/t\n") 
    	  .append("9FRVtRl1gVhwKdi8A5O9hp4t3dBIdOanaup2UEL4lp7izzgt2rkMeuyQ1ZjHsN7L\n") 
    	  .append("mDsfjoFcYx/8CID9LBwRCN2p+YCuoWUjuorrdU/2eit2lNh6ypiF6WECAQOjgZAw\n") 
    	  .append("gY0wHQYDVR0OBBYEFIT65b/ekUlm38WKUsOzt7MgHMdtMA4GA1UdDwEB/wQEAwIB\n") 
    	  .append("BjASBgNVHRMBAf8ECDAGAQH/AgEBMEgGA1UdHwRBMD8wPaA7oDmGN2h0dHA6Ly9v\n") 
    	  .append("bmxpbmUuZGFuc2tlYmFuay5jb20vcGtpL0RCR1JPT1RfMTExMTExMDAwMi5jcmww\n") 
    	  .append("DQYJKoZIhvcNAQELBQADggEBAFjnBPCos7jMMLc3FqyQUMt/HJGKgJDrhYiPZBo9\n") 
    	  .append("njGkH52Urryqw1sbT3wXA1NuzbjHE3xTUD+5jNPCncYqML9xqQjSQkBcb9eJfHZ+\n") 
    	  .append("asiclsO38cSn2qriJPIrCREPOpRVqrGQRbZQhmDiB198hpAdLp38khJon/gXbR7u\n") 
    	  .append("9e0rN8MIM4sXn+lFuQIWiPuv+3llGSoLlIxJnjiQQ9FDjhwN5U+N1N2aHaLc5AHu\n") 
    	  .append("4X/qRutLCy7AYUJZMPBoakPLscYceW2Ztvx4VAyOXgHDdvmz0Bd58XWOs1A9bNMZ\n") 
    	  .append("FeYAB14D9yQRCkXYLhr6sm8HuyqaIkGChFpNb+Gf8gcPvtw=\n")
    	  .append("-----END CERTIFICATE-----\n");
          return sb.toString().getBytes();
      }

      public static byte[] getSystRootCert() {
    	  StringBuilder sb = new StringBuilder();
    	  sb.append("-----BEGIN CERTIFICATE-----\n") 
    	  .append("MIIEPzCCAyegAwIBAgIEQjpYgjANBgkqhkiG9w0BAQsFADCBmDEQMA4GA1UEAxMHR\n")
    	  .append("EJHUk9PVDELMAkGA1UEBhMCREsxEzARBgNVBAcTCkNvcGVuaGFnZW4xEDAOBgNVBA\n")
    	  .append("gTB0Rlbm1hcmsxGjAYBgNVBAoTEURhbnNrZSBCYW5rIEdyb3VwMRowGAYDVQQLExF\n")
    	  .append("EYW5za2UgQmFuayBHcm91cDEYMBYGA1UEBRMPNjExMjYyMjgxMTIwMDAyMB4XDTEw\n")
    	  .append("MTAyNzAwMDAwMFoXDTIwMTAyNzAwMDAwMFowgZgxEDAOBgNVBAMTB0RCR1JPT1QxC\n")
    	  .append("zAJBgNVBAYTAkRLMRMwEQYDVQQHEwpDb3BlbmhhZ2VuMRAwDgYDVQQIEwdEZW5tYX\n")
    	  .append("JrMRowGAYDVQQKExFEYW5za2UgQmFuayBHcm91cDEaMBgGA1UECxMRRGFuc2tlIEJ\n")
    	  .append("hbmsgR3JvdXAxGDAWBgNVBAUTDzYxMTI2MjI4MTEyMDAwMjCCASAwDQYJKoZIhvcN\n")
    	  .append("AQEBBQADggENADCCAQgCggEBAMZ3byoFYnH65fkTBAsqYCQunGnQ1g0jiGM4iX4rx\n")
    	  .append("TZGnPhgw/dk3TAatZpCgrZ3aLFLmRsgFY9VBNudjwZUAZx5CSSWCsyrGQCeUclXjW\n")
    	  .append("BI+QwlZlEdU5EwtJh5SBOFGjPSOsJfT0I49lwgkZSbEGGhY+gFG6f3KI1JIcm+cFf\n")
    	  .append("za62iTD48XKezgtm/cWqxGYkcz8Kd5fLelbc+lzP8jLMJttxISxohsquBl06btBj8\n")
    	  .append("lLKvsWv5tnWJsIs9RZyxeIzvdnO4Pt3t9bNC6EUMzQK7tLdtJapo4x5eXIYJtIc8+\n")
    	  .append("N6u1ZiFaTH2Rya+bjJu/mVVF3XhDrKIzQdMdhTtz40CAQOjgZAwgY0wHQYDVR0OBB\n")
    	  .append("YEFOe9JMtpS9qOGZDajKq8jWah9G4NMA4GA1UdDwEB/wQEAwIBBjASBgNVHRMBAf8\n")
    	  .append("ECDAGAQH/AgEBMEgGA1UdHwRBMD8wPaA7oDmGN2h0dHA6Ly9vbmxpbmUuZGFuc2tl\n")
    	  .append("YmFuay5jb20vcGtpL0RCR1JPT1RfMTExMTEyMDAwMi5jcmwwDQYJKoZIhvcNAQELB\n")
    	  .append("QADggEBAGQd+pNQvefZTVXViRdOpdDcpQWmcUs1to97/H6zbW/Fi9SMz9xs2NgCHp\n")
    	  .append("YibimN1M2e4pFcw57zT9swxaEBjBjmdqQ2h/ciL7mh83qjHXP1LL/u9IWUMV/FWe/\n")
    	  .append("m+xeeW2Q/6jDRJWPn4VV579vmtr6x3GDW9rsKEVF97HKkcDg+y79aAjFPHcRlfamW\n")
    	  .append("jevOTW5CJJl9soS33nxgEsteoXHTgO1C0Xuk8an5OLdXEy/Fl7R1uFRb4//Q1d0LU\n")
    	  .append("3Vg3R/LUPkczddLZgGlnJyiPeQZIvLD+mZwXlgTenFf2iOIeUwsukWze7+deq8x4Q\n")
    	  .append("xYysq/pAtu6/kCbJAr8kprREQ=\n")
    	  .append("-----END CERTIFICATE-----\n");
          return sb.toString().getBytes();
      }
}
