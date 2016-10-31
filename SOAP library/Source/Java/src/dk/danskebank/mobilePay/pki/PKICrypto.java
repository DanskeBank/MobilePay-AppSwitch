package dk.danskebank.mobilePay.pki;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.security.auth.callback.CallbackHandler;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.ext.WSSecurityException.ErrorCode;

/**
 * Implementation of Crypto providing only the necessary methods needed for PKI
 * @author mbjerg
 *
 */

public class PKICrypto implements Crypto {

	private PrivateKey privateKey;
	private X509Certificate cert;

	@Override
	public PrivateKey getPrivateKey(String identifier, String password)
			throws WSSecurityException {
		return this.privateKey;
	}

	@Override
	public X509Certificate[] getX509Certificates(CryptoType cryptoType)
			throws WSSecurityException {
		return new X509Certificate[] {this.cert};
	}

	@Override
	public String getX509Identifier(X509Certificate cert)
			throws WSSecurityException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public void verifyTrust(X509Certificate[] certs, boolean enableRevocation,
			Collection<Pattern> subjectCertConstraints) throws WSSecurityException {
		if(certs.length > 0){
			X509Certificate cert = certs[0];
		}
		//TODO Validate trust
	}
	
	@Override
	public PrivateKey getPrivateKey(X509Certificate cert, CallbackHandler callbackhandler)
			throws WSSecurityException {
		return privateKey;
	}

	@Override
	public byte[] getSKIBytesFromCert(X509Certificate cert)
			throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}


	@Override
	public X509Certificate loadCertificate(InputStream input)
			throws WSSecurityException {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			return (X509Certificate) certFactory.generateCertificate(input);
		} catch (CertificateException e) {
			throw new WSSecurityException(ErrorCode.INVALID_SECURITY_TOKEN, e);
		}
	}

	@Override
	public void setCertificateFactory(CertificateFactory factory) {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public void setCryptoProvider(String provider) {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public void setDefaultX509Identifier(String identifier) {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public void verifyDirectTrust(X509Certificate[] certs)
			throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public void verifyTrust(PublicKey key) throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public byte[] getBytesFromCertificates(X509Certificate[] certs)
			throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public CertificateFactory getCertificateFactory()
			throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public X509Certificate[] getCertificatesFromBytes(byte[] data)
			throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public String getCryptoProvider() {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	@Override
	public String getDefaultX509Identifier() throws WSSecurityException {
		// For our purposes we do not need to implement this.
		throw new UnsupportedOperationException("This method is not supported.");
	}

	public void setPrivateKey(PrivateKey myPrivateKey) {
		this.privateKey = myPrivateKey;
	}
	
	public void setX509Certificate(X509Certificate cert) {
		this.cert = cert;
	}

}
