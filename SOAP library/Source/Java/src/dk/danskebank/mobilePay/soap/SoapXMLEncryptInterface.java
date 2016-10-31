package dk.danskebank.mobilePay.soap;

import org.apache.wss4j.common.crypto.Crypto;
import org.w3c.dom.Document;

import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;

public interface SoapXMLEncryptInterface {

	public abstract Document encryptXml(Document document)
			throws CouldNotEncryptException;
	public abstract Crypto getCrypto();

}