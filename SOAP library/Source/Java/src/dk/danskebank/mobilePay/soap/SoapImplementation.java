package dk.danskebank.mobilePay.soap;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.xml.security.Init;

import com.danskebank.securesoapsgw.RequestHeaderType;
import com.danskebank.services.CancelInput;
import com.danskebank.services.CancelOutput;
import com.danskebank.services.CancelV02;
import com.danskebank.services.CancelV02Interface;
import com.danskebank.services.CaptureInput;
import com.danskebank.services.CaptureOutput;
import com.danskebank.services.CaptureV02;
import com.danskebank.services.CaptureV02Interface;
import com.danskebank.services.DacGetReservationsInput;
import com.danskebank.services.DacGetReservationsOutput;
import com.danskebank.services.DacGetStatusInput;
import com.danskebank.services.DacGetStatusOutput;
import com.danskebank.services.DacRefundInput;
import com.danskebank.services.DacRefundOutput;
import com.danskebank.services.GetReservationsV02;
import com.danskebank.services.GetReservationsV02Interface;
import com.danskebank.services.GetStatusV03;
import com.danskebank.services.GetStatusV03Interface;
import com.danskebank.services.RefundV03;
import com.danskebank.services.RefundV03Interface;

import dk.danskebank.mobilePay.pki.PKIFaultInterceptor;
import dk.danskebank.mobilePay.pki.StoreMessageInInterceptor;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;

public class SoapImplementation {


	private SoapXMLSignerInterface xmlSigner;
	private SoapXMLEncryptInterface xmlEncryptor;
	private String customerId;

	public SoapXMLSignerInterface getXmlSigner() {
		return xmlSigner;
	}

	public void setXmlSigner(SoapXMLSignerInterface signer) {
		this.xmlSigner = signer;
	}

	public SoapXMLEncryptInterface getXmlEncryptor() {
		return xmlEncryptor;
	}

	public void setXmlEncryptor(SoapXMLEncryptInterface xmlEncryptor) {
		this.xmlEncryptor = xmlEncryptor;
	}

	public SoapImplementation(String customerId) {
		Init.init();
		this.customerId = customerId;
	}

	public DacGetStatusOutput getStatus(String merchantId, String orderId, String mpCustomerId, Date dateFrom, Date dateTo, boolean extendedOutput) throws CouldNotSignException{
		DacGetStatusInput input = new DacGetStatusInput();
		input.setMerchantId(merchantId);
		input.setOrderId(orderId);
		if(mpCustomerId != null){
			input.setCustomerId(mpCustomerId);
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		if(dateFrom != null){
			input.setDateFrom(dateFormatter.format(dateFrom));
		}
		if(dateTo != null){
			input.setDateTo(dateFormatter.format(dateTo));
		}
		if(extendedOutput){
			input.setActionCode("E");
		} else {
			input.setActionCode("B");
		}
		input.setTest("N");
		try {
			return getStatusService().getStatus(input, null, getRequestHeader());
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	public CancelOutput cancel(String merchantId, String orderId, String mpCustomerId, Date dateFrom, Date dateTo) throws CouldNotSignException{
		CancelInput input = new CancelInput();
		input.setOrderID(orderId);
		input.setMerchantID(merchantId);
		if(mpCustomerId != null){
			input.setCustomerId(mpCustomerId);
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		if(dateFrom != null){
			input.setDateFrom(dateFormatter.format(dateFrom));
		}
		if(dateTo != null){
			input.setDateTo(dateFormatter.format(dateTo));
		}
		input.setTest("N");
		try {
			return getCancelService().cancel(input, null, getRequestHeader());
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	public CaptureOutput capture(String merchantId, String orderId, String mpCustomerId, Date dateFrom, Date dateTo, String bulkRef, BigDecimal amount ) throws CouldNotSignException{
		CaptureInput input = new CaptureInput();
		input.setOrderId(orderId);
		input.setMerchantId(merchantId);
		input.setBulkRef(bulkRef);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		if(dateFrom != null){
			input.setDateFrom(dateFormatter.format(dateFrom));
		}
		if(dateTo != null){
			input.setDateTo(dateFormatter.format(dateTo));
		}
		input.setAmount(amount);
		input.setTest("N");
		try {
			return getCaptureService().capture(input, null, getRequestHeader());
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	public DacGetReservationsOutput getReservations(String merchantId, String mpCustomerId, Date dateFrom, Date dateTo, String actionCode) throws CouldNotSignException {
		DacGetReservationsInput input = new DacGetReservationsInput();
		input.setMerchantId(merchantId);
		input.setActionCode(actionCode);
		input.setCustomerId(mpCustomerId);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		if(dateFrom != null){
			input.setTimeStart(dateFormatter.format(dateFrom));
		}
		if(dateTo != null){
			input.setTimeEnd(dateFormatter.format(dateTo));
		}
		input.setTest("N");
		try {
			return getReservationService().getReservations(input, null, getRequestHeader());
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	public DacRefundOutput refund(String merchantId, String orderId, String mpCustomerId, Date dateFrom, Date dateTo, String bulkRef, BigDecimal amount) throws CouldNotSignException{
		DacRefundInput input = new DacRefundInput();
		input.setOrderId(orderId);
		input.setMerchantId(merchantId);
		if(mpCustomerId != null){
			input.setCustomerId(mpCustomerId);
		}
		input.setBulkRef(bulkRef);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		if(dateFrom != null){
			input.setDateFrom(dateFormatter.format(dateFrom));
		}
		if(dateTo != null){
			input.setDateTo(dateFormatter.format(dateTo));
		}
		input.setAmount(amount);
		input.setTest("N");
		try {
			return getRefundService().refund(input, null, getRequestHeader());
		} catch (DatatypeConfigurationException e) {
			throw new CouldNotSignException(e);
		}
	}
	
	private RequestHeaderType getRequestHeader() throws DatatypeConfigurationException {
		RequestHeaderType requestHeader = new RequestHeaderType();
		requestHeader.setLanguage("DK");
		requestHeader.setRequestId((System.currentTimeMillis() % 1000000000)
				+ "");
		requestHeader.setSenderId(customerId);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		requestHeader.setTimestamp(format.format(new Date()));
		requestHeader.setSignerId1(this.customerId);
		requestHeader.setSignerId2("");
		requestHeader.setSignerId3("");
		requestHeader.setDBCryptId("");
		requestHeader.setSenderId(this.customerId);
		return requestHeader;
	}

	protected GetStatusV03 getStatusService() {
		URL wsdlLocation = SoapImplementation.class.getClassLoader().getResource("resources/wsdl/soapsg/GetStatusV03.wsdl");

		GetStatusV03Interface service = new GetStatusV03Interface(wsdlLocation);
		GetStatusV03 statusService = service.getGetStatusV03();
		setupClient(statusService);
		return statusService;
	}

	protected CancelV02 getCancelService() {
		URL wsdlLocation = SoapImplementation.class.getClassLoader().getResource("resources/wsdl/soapsg/CancelV02.wsdl");

		CancelV02Interface service = new CancelV02Interface(wsdlLocation);
		CancelV02 cancelService = service.getCancelV02();
		setupClient(cancelService);
		return cancelService;
	}

	protected CaptureV02 getCaptureService() {
		URL wsdlLocation = SoapImplementation.class.getClassLoader().getResource("resources/wsdl/soapsg/CaptureV02.wsdl");

		CaptureV02Interface service = new CaptureV02Interface(wsdlLocation);
		CaptureV02 captureService = service.getCaptureV02();
		setupClient(captureService);
		return captureService;
	}

	protected RefundV03 getRefundService() {
		URL wsdlLocation = SoapImplementation.class.getClassLoader().getResource("resources/wsdl/soapsg/RefundV03.wsdl");

		RefundV03Interface service = new RefundV03Interface(wsdlLocation);
		RefundV03 refundService = service.getRefundV03();
		setupClient(refundService);
		return refundService;
	}

	protected GetReservationsV02 getReservationService() {
		URL wsdlLocation = SoapImplementation.class.getClassLoader().getResource("resources/wsdl/soapsg/GetReservationsV01.wsdl");

		GetReservationsV02Interface service = new GetReservationsV02Interface(wsdlLocation);
		GetReservationsV02 reservationsService = service.getGetReservationsV02();
		setupClient(reservationsService);
		return reservationsService;
	}

	private void setupClient(Object service) {
		Client client = ClientProxy.getClient(service);
		client.getInInterceptors().add(new StoreMessageInInterceptor());
		client.getInInterceptors().add(new SoapSigInInterceptor(this.xmlSigner, this.xmlEncryptor));
		client.getInInterceptors().add(new PKIFaultInterceptor());
		client.getOutInterceptors().add(new SAAJOutInterceptor());
		client.getOutInterceptors().add(new SoapSigOutInterceptor(this.xmlSigner, this.xmlEncryptor));
		client.getEndpoint().put(SecurityConstants.ENCRYPT_CRYPTO,this.xmlEncryptor.getCrypto());
		client.getEndpoint().put(SecurityConstants.SIGNATURE_CRYPTO,this.xmlSigner.getCrypto());
		HTTPConduit http = (HTTPConduit) client.getConduit();

		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();

		httpClientPolicy.setConnectionTimeout(36000);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setReceiveTimeout(32000);

		http.setClient(httpClientPolicy);
	}
}