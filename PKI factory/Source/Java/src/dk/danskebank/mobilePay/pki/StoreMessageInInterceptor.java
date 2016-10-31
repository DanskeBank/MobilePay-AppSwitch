package dk.danskebank.mobilePay.pki;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;

public class StoreMessageInInterceptor implements PhaseInterceptor<Message> {

	@Override
	public void handleFault(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		InputStream content = new BufferedInputStream(message.getContent(InputStream.class));
	    message.setContent(InputStream.class, content);
		content.mark(0);
		try {
		    content.reset();
		} catch (Exception e) {
		}
	}

	@Override
	public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
		return null;
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
		return getClass().getName();
	}

	@Override
	public String getPhase() {
		return Phase.PRE_STREAM;
	}

}
