package dk.danskebank.mobilePay.pki.exceptions;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

public class XmlException extends Exception {

	public XmlException(String message) {
		super(message);
	}

	public XmlException(JAXBException e) {
		super(e);
	}
	
	public XmlException(ParserConfigurationException e) {
		super(e);
	}
	
	public XmlException(Exception e) {
		super(e);
	}
}
