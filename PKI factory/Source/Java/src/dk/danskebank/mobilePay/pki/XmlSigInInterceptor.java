package dk.danskebank.mobilePay.pki;

import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.rs.security.xml.AbstractXmlSigInHandler;
import org.apache.cxf.rs.security.xml.XmlEncInInterceptor;
import org.apache.cxf.staxutils.W3CDOMStreamReader;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import dk.danskebank.mobilePay.pki.exceptions.InvalidSignatureException;

public class XmlSigInInterceptor extends AbstractXmlSigInHandler implements
		PhaseInterceptor<Message> {

	private boolean removeSignature = true;
	private boolean persistSignature = true;
	private PKIXMLSignerInterface signer;

	public XmlSigInInterceptor(PKIXMLSignerInterface signer) {
		this.signer = signer;
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
			factory.setIgnoringElementContentWhitespace(true);
			factory.setNamespaceAware(true);
			builder = factory.newDocumentBuilder();
			
			doc = builder.parse(content);

		} catch (Exception e){
			e.printStackTrace();
		}
		// Document doc = getDocument(message);
		if (doc == null) {
			return;
		}

		Element root = doc.getDocumentElement();
		Element signatureElement = getSignatureElement(root);
		if (signatureElement == null) {
			throwFault("XML Signature is not available", null);
		}

		boolean valid = false;
		Reference ref = null;
		try {
			XMLSignature signature = new XMLSignature(signatureElement, "",	true);

			ref = getReference(signature);
			Element signedElement = validateReference(root, ref);
			if (signedElement.hasAttribute("xml:id")) {
				signedElement.setIdAttribute("xml:id", true);
			}

			// See also WSS4J SAMLUtil.getCredentialFromKeyInfo
			KeyInfo keyInfo = signature.getKeyInfo();

			X509Certificate cert = keyInfo.getX509Certificate();
			if (cert != null) {
				valid = signature.checkSignatureValue(cert);
			} else {
				PublicKey pk = keyInfo.getPublicKey();
				if (pk != null) {
					valid = signature.checkSignatureValue(pk);
				}
			}

			// validate trust
			signer.validateTrust(cert, keyInfo.getPublicKey());
			
			if (valid && persistSignature) {
				message.setContent(XMLSignature.class, signature);
				message.setContent(Element.class, signedElement);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throwFault("Signature validation failed", ex);
		}
		if (!valid) {
			throwFault("Signature validation failed", null);
		}
		if (removeSignature) {
			if (!isEnveloping(root)) {
				Element signedEl = getSignedElement(root, ref);
				signedEl.removeAttribute("xml:id");
				signatureElement.getParentNode().removeChild(signatureElement);
			} else {
				Element actualBody = getActualBody(root);
				Document newDoc = DOMUtils.createDocument();
				newDoc.adoptNode(actualBody);
				root = actualBody;
			}
		}
		List<Element> bodyElements = DOMUtils.findAllElementsByTagNameNS(root, "http://schemas.xmlsoap.org/soap/envelope/", "Body");
		
		message.setContent(XMLStreamReader.class, new W3CDOMStreamReader((Element)bodyElements.get(0).getFirstChild()));
		message.setContent(InputStream.class, null);

	}

	
	protected Element validateReference(Element root, Reference ref) {
        boolean enveloped = false;
        
        String refId = ref.getURI();
        
        if (!refId.startsWith("#") || refId.length() <= 1) {
            throwFault("Only local Signature References are supported", null);
        }
        
        Element signedEl = getSignedElement(root, ref);
        if (signedEl != null) {
            enveloped = signedEl == root;
        } else {
            throwFault("Signature Reference ID is invalid", null);
        }
        
        
        Transforms transforms = null;
        try {
            transforms = ref.getTransforms();
        } catch (XMLSecurityException ex) {
            throwFault("Signature transforms can not be obtained", ex);
        }
        
        boolean c14TransformConfirmed = false;
        String c14TransformExpected = null;
        boolean envelopedConfirmed = false;
        for (int i = 0; i < transforms.getLength(); i++) {
            try {
                Transform tr = transforms.item(i);
                if (Transforms.TRANSFORM_ENVELOPED_SIGNATURE.equals(tr.getURI())) {
                    envelopedConfirmed = true;
                } else if (c14TransformExpected != null && c14TransformExpected.equals(tr.getURI())) {
                    c14TransformConfirmed = true;
                } 
            } catch (Exception ex) {
                throwFault("Problem accessing Transform instance", ex);    
            }
        }
        if (enveloped && !envelopedConfirmed) {
            throwFault("Only enveloped signatures are currently supported", null);
        }
        if (c14TransformExpected != null && !c14TransformConfirmed) {
            throwFault("Transform Canonicalization is not supported", null);
        }
        
        return signedEl;
    }
    
	private Element getActualBody(Element envelopingSigElement) {
		Element objectNode = getNode(envelopingSigElement,
				Constants.SignatureSpecNS, "Object", 0);
		if (objectNode == null) {
			throwFault("Object envelope is not available", null);
		}
		Element node = DOMUtils.getFirstElement(objectNode);
		if (node == null) {
			throwFault("No signed data is found", null);
		}
		return node;

	}

	private Element getSignatureElement(Element sigParentElement) {
		List<Element> elementsByTagNameNS = DOMUtils.findAllElementsByTagNameNS(sigParentElement, Constants.SignatureSpecNS, "Signature");
		if(elementsByTagNameNS.isEmpty()){
			return null;
		}
		return elementsByTagNameNS.get(0);
	}

	private Element getSignedElement(Element root, Reference ref) {
		String rootId = root.getAttribute("ID");
		String expectedID = ref.getURI().substring(1);

		if (!expectedID.equals(rootId)) {
			return findElementById(root, expectedID, true);
		} else {
			return root;
		}
	}

	/**
	 * Returns the single element that contains an Id with value
	 * <code>uri</code> and <code>namespace</code>. The Id can be either a
	 * wsu:Id or an Id with no namespace. This is a replacement for a XPath Id
	 * lookup with the given namespace. It's somewhat faster than XPath, and we
	 * do not deal with prefixes, just with the real namespace URI
	 * 
	 * If checkMultipleElements is true and there are multiple elements, we log
	 * a warning and return null as this can be used to get around the signature
	 * checking.
	 * 
	 * @param startNode
	 *            Where to start the search
	 * @param value
	 *            Value of the Id attribute
	 * @param checkMultipleElements
	 *            If true then go through the entire tree and return null if
	 *            there are multiple elements with the same Id
	 * @return The found element if there was exactly one match, or
	 *         <code>null</code> otherwise
	 */
	private static Element findElementById(Node startNode, String value,
			boolean checkMultipleElements) {
		//
		// Replace the formerly recursive implementation with a depth-first-loop
		// lookup
		//
		Node startParent = startNode.getParentNode();
		Node processedNode = null;
		Element foundElement = null;
		String id = value;

		while (startNode != null) {
			// start node processing at this point
			if (startNode.getNodeType() == Node.ELEMENT_NODE) {
				Element se = (Element) startNode;
				String attributeNS = se.getAttribute("xml:id");
				if (!"".equals(attributeNS) && id.equals(attributeNS)) {
					if (!checkMultipleElements) {
						return se;
					} else if (foundElement == null) {
						foundElement = se; // Continue searching to find
											// duplicates
					} else {
						// Multiple elements with the same 'Id' attribute value
						return null;
					}
				}
			}

			processedNode = startNode;
			startNode = startNode.getFirstChild();

			// no child, this node is done.
			if (startNode == null) {
				// close node processing, get sibling
				startNode = processedNode.getNextSibling();
			}
			// no more siblings, get parent, all children
			// of parent are processed.
			while (startNode == null) {
				processedNode = processedNode.getParentNode();
				if (processedNode == startParent) {
					return foundElement;
				}
				// close parent node processing (processed node now)
				startNode = processedNode.getNextSibling();
			}
		}
		return foundElement;
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

}
