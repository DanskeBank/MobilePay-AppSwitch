package dk.danskebank.mobilePay.pki.exceptions;

import javax.xml.stream.XMLStreamException;

public class PKISoapFaultException extends RuntimeException {

	public PKISoapFaultException(String message) {
		super(message);
	}

	public PKISoapFaultException(XMLStreamException e) {
		super(e);
	}

}
