package dk.danskebank.mobilePay.soap;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.rs.security.xml.AbstractXmlSigInHandler;
import org.apache.cxf.rs.security.xml.XmlEncInInterceptor;
import org.apache.cxf.staxutils.W3CDOMStreamReader;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSSecurityEngine;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.processor.SignatureProcessor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import dk.danskebank.mobilePay.pki.exceptions.InvalidSignatureException;

public class SoapSigInInterceptor extends AbstractXmlSigInHandler implements
		PhaseInterceptor<Message>, CallbackHandler {

	private SoapXMLSignerInterface signer;
	private SoapXMLEncryptInterface xmlEncryptor;

	public SoapSigInInterceptor(SoapXMLSignerInterface signer, SoapXMLEncryptInterface xmlEncryptor) {
		Security.addProvider(new BouncyCastleProvider());
		this.signer = signer;
		this.xmlEncryptor = xmlEncryptor;
		Logger.getLogger(SignatureProcessor.class.getName()).setLevel(Level.ALL);;
	}

	@Override
	public void handleFault(Message msg) {
	}

	public void handleMessage(Message message) throws Fault {
		DocumentBuilder builder;
		Document doc = null;
		try{
			InputStream content = message.getContent(InputStream.class);
			content.reset();
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setValidating(false);
			factory.setIgnoringComments(true);
			factory.setNamespaceAware(true);
			factory.setIgnoringElementContentWhitespace(true);
			builder = factory.newDocumentBuilder();
			
			doc = builder.parse(content);
		} catch (IOException e){
			throwFault("Could not parse message", e);
		} catch (SAXException e) {
			throwFault("Could not parse message", e);
		} catch (ParserConfigurationException e) {
			throwFault("Could not parse message", e);
		}
		if (doc == null) {
			return;
		}

		WSSecurityEngine engine = new WSSecurityEngine();
		try {
			RequestData req = new RequestData();
			req.setMsgContext(message);
			req.setAllowRSA15KeyTransportAlgorithm(true);
	        req.setWssConfig(engine.getWssConfig());
	        req.setDecCrypto(this.xmlEncryptor.getCrypto());
	        req.setSigVerCrypto(this.signer.getCrypto());
	        req.setUse200512Namespace(false);
	        req.setCallbackHandler(this);
	        engine.processSecurityHeader(doc, req);
		} catch (WSSecurityException e) {
			throwFault("Could not verify XML", e);
		}
		
		Element actualBody = getBody(doc.getDocumentElement());
        Document newDoc = DOMUtils.createDocument();
        newDoc.adoptNode(actualBody);
		
		message.setContent(XMLStreamReader.class, 
                new W3CDOMStreamReader(actualBody));
		message.setContent(InputStream.class, null);		}
	
	private Element getBody(Element documentElement) {
		Element body = DOMUtils.getFirstChildWithName(documentElement, "http://www.w3.org/2003/05/soap-envelope", "Body");
		return (Element) body.getFirstChild();
	}

	@Override
	protected void throwFault(String error, Exception ex) {
		throw new InvalidSignatureException(error, ex);
	};

	@Override
	public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
		return null;
	}

	@Override
	public Set<String> getAfter() {
        return Collections.singleton(XmlEncInInterceptor.class.getName());
	}

	@Override
	public Set<String> getBefore() {
		return Collections.emptySet();
	}

	@Override
	public String getId() {
		return "DBXMLSigIntercept";
	}

	@Override
	public String getPhase() {
		return Phase.UNMARSHAL;
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
	}

}
