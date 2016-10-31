/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package dk.danskebank.mobilePay.pki;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.phase.Phase;

import dk.danskebank.mobilePay.pki.exceptions.PKISoapFaultException;

public class PKIFaultInterceptor extends AbstractSoapInterceptor {
    public PKIFaultInterceptor() {
        super(Phase.POST_PROTOCOL);
    }

    public void handleMessage(SoapMessage message) {
        XMLStreamReader xmlReader = message.getContent(XMLStreamReader.class);
        if (xmlReader == null) {
            return;
        }
        try {
            // advance to first tag.
            int x = xmlReader.getEventType();
            while (x != XMLStreamReader.START_ELEMENT
                && x != XMLStreamReader.END_ELEMENT
                && xmlReader.hasNext()) {
                x = xmlReader.next();
            }
            if (!xmlReader.hasNext()) {
                //end of document, just return
                return;
            }
        } catch (XMLStreamException e) {
            throw new PKISoapFaultException(e);
        }
        if ("PKIFactoryServiceFault".equals(xmlReader.getName().getLocalPart()) && isRequestor(message)) {
            message.getInterceptorChain().abort();
            throw new PKISoapFaultException("Soap Fault");
        }
    }
}
