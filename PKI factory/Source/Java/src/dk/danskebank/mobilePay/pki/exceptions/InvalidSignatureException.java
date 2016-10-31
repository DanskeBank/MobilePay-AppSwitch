package dk.danskebank.mobilePay.pki.exceptions;

public class InvalidSignatureException extends RuntimeException {

	public InvalidSignatureException(String error, Exception ex) {
		super(error,ex);
	}

}
