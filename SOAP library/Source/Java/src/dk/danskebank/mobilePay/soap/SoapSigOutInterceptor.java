package dk.danskebank.mobilePay.soap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.dom.DOMSource;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.SoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.w3c.dom.Document;

public class SoapSigOutInterceptor implements SoapInterceptor, 
PhaseInterceptor<SoapMessage> {

	static SoapXMLSignerInterface signer;
	static SoapXMLEncryptInterface encrypter;

	public SoapSigOutInterceptor(SoapXMLSignerInterface signer, SoapXMLEncryptInterface encrypter) {
		SoapSigOutInterceptor.signer = signer;
		SoapSigOutInterceptor.encrypter = encrypter;
	}

	public void handleMessage(SoapMessage message) throws Fault {
		if (message.getExchange().get(Throwable.class) != null) {
			return;
		}

		message.getInterceptorChain().add(new XMLSigOutInternal());
	}

	@Override
	public void handleFault(SoapMessage message) {
	}

	@Override
	public Set<String> getAfter() {
		return Collections.singleton(SAAJOutInterceptor.class.getName());
	}

	@Override
	public Set<String> getBefore() {
		return Collections.emptySet();
	}

	@Override
	public String getId() {
		return "XmlsigOutInterceptor";
	}

	@Override
	public String getPhase() {
		return Phase.PRE_PROTOCOL;
	}

	@Override
	public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
		return null;
	}

	@Override
	public Set<URI> getRoles() {
		return null;
	}

	@Override
	public Set<QName> getUnderstoodHeaders() {
		return null;
	}

	final static class XMLSigOutInternal implements PhaseInterceptor<SoapMessage>{

		@Override
		public void handleMessage(SoapMessage message) throws Fault {

			try {
				Document doc = getDomDocument(message);
				if (doc == null) {
					return;
				}
				Document finalDoc = processDocument(message, doc);
				if(finalDoc != null){
					message.setContent(List.class, 
							new MessageContentsList(new DOMSource(finalDoc)));
				}
			} catch (Exception ex) {
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				throw new Fault(new RuntimeException(ex.getMessage() + ", stacktrace: " + sw.toString()));
			}

		}

		private Document getDomDocument(Message m) throws Exception {
			SOAPMessage saaj = m.getContent(SOAPMessage.class);

			if (saaj == null) {
				throw new Fault(new RuntimeException("SAAJ needs to be run"));
			}

			Document doc = saaj.getSOAPPart();
			return doc;
		}

		@Override
		public void handleFault(SoapMessage message) {

		}

		@Override
		public Set<String> getAfter() {
			return Collections.emptySet();
		}

		@Override
		public Set<String> getBefore() {
			return Collections.emptySet();
		}

		@Override
		public String getId() {
			return XMLSigOutInternal.class.getName();
		}

		@Override
		public String getPhase() {
			return Phase.POST_PROTOCOL;
		}

		@Override
		public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
			return null;
		}

		protected Document processDocument(Message message, Document doc)
				throws Exception {
			Document signedDoc = signer.signBody(doc);
			Document encryptedDoc = encrypter.encryptXml(signedDoc);
			Document signEncryptSignedDoc = signer.signEncryptedBody(encryptedDoc);
			return signEncryptSignedDoc;
		}


	}
}
