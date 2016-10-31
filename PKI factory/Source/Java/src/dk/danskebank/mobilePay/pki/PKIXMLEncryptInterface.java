package dk.danskebank.mobilePay.pki;

import javax.xml.bind.JAXBElement;

import org.w3._2001._04.xmlenc_.EncryptedDataType;
import org.w3c.dom.Document;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;

public interface PKIXMLEncryptInterface {

	public abstract JAXBElement<EncryptedDataType> encryptXml(Document document)
			throws CouldNotEncryptException;

}