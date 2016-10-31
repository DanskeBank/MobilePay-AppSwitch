package dk.danskebank.mobilePay.demo;

import dk.danskebank.mobilePay.demo.ActionCommandLineParser.Action;
import dk.danskebank.mobilePay.pki.PKIImplementation;
import dk.danskebank.mobilePay.pki.PKIXMLSignerInterface;
import dk.danskebank.mobilePay.pki.PKIXMLSoftwareEncrypt;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotEncryptException;
import dk.danskebank.mobilePay.pki.exceptions.CouldNotSignException;
import dk.danskebank.pki.pkifactoryservice.PKIFactoryServiceFault;
import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusResponse;
import dk.danskebank.pki.pkifactoryservice.elements.CreateCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.GetOwnCertificateListResponse;
import dk.danskebank.pki.pkifactoryservice.elements.KeyGeneratorTypeType;
import dk.danskebank.pki.pkifactoryservice.elements.RenewCertificateResponse;
import dk.danskebank.pki.pkifactoryservice.elements.RevokeCertificateResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CRLReason;
import java.util.ArrayList;
import java.util.EnumSet;

public class DemoPKIFactory {

	public static void main(String[] args) throws GeneralSecurityException, IOException, PKIFactoryServiceFault,
			CouldNotEncryptException, CouldNotSignException {
		
		ActionCommandLineParser commandParser = new ActionCommandLineParser();
		Action action = commandParser.getAction(args);

		if (Action.HELP.equals(action)) {
			DisplayUtil.displayHelpText();
		} else {
			// Initial setup
			MerchantKeystore merchantKeystore = new MerchantKeystore();
			PKIImplementation pkiFactory = new PKIImplementation(merchantKeystore.getCustomerId());
			pkiFactory.setBankRootCertificate(BankCertUtil.getProdRootCert());
			PKIXMLSignerInterface signer = merchantKeystore.getSoftwareSigner();
			pkiFactory.setXmlSigner(signer);

			// Getting newest Bank certificates
			BankCertUtil.setBankCerts(pkiFactory.getBankCertificate());
			signer.setBankSigningCertificate(BankCertUtil.getSigningCert());
			pkiFactory.setXmlEncryptor(new PKIXMLSoftwareEncrypt(BankCertUtil.getEncryptionCert()));

			if (EnumSet.of(Action.NONE, // Automatic certificate issuing
						   Action.CREATE, 
						   Action.RENEW).contains(action)) {

				boolean firstTimeIssuingCert = merchantKeystore.isFirstTimeIssuingCert();

				if ((firstTimeIssuingCert && Action.RENEW.equals(action))
						|| (!firstTimeIssuingCert && Action.CREATE.equals(action))) {
					throw new UnsupportedOperationException("Invalid command: " + action.name());
				}

				if (firstTimeIssuingCert) {
					// Getting new merchant certificates using PINCODE
					CreateCertificateResponse createCertificateResponse = pkiFactory.createCertificate(
							KeyGeneratorTypeType.SOFTWARE, merchantKeystore.getFirstTimePincode(),
							merchantKeystore.getSigningCertRequest(), merchantKeystore.getEncryptionCertRequest());

					merchantKeystore.storeMerchantCertificates(createCertificateResponse.getSigningCert(),
							createCertificateResponse.getEncryptionCert());
				} else {
					// Renewing merchant certificates
					RenewCertificateResponse renewCertificate = pkiFactory.renewCertificate(
							KeyGeneratorTypeType.SOFTWARE, merchantKeystore.getSigningCertRequest(),
							merchantKeystore.getEncryptionCertRequest());

					merchantKeystore.storeMerchantCertificates(renewCertificate.getSigningCert(),
							renewCertificate.getEncryptionCert());
				}

				BankCertUtil.storeBankCertificates(MerchantKeystore.getIssuedEncryptionKeystorePath(),
						MerchantKeystore.getIssuedSigningKeystorePath(), MerchantKeystore.getKeystorePassword(), false);
			} else { // Action that uses serial numbers

				ArrayList<String> serialNumbers;
				if (commandParser.getSerialNumbers() == null) {
					serialNumbers = new ArrayList<String>();
					serialNumbers.add(merchantKeystore.getEncryptionCert().getSerialNumber().toString(10));
					serialNumbers.add(merchantKeystore.getSigningCert().getSerialNumber().toString(10));
				} else {
					serialNumbers = commandParser.getSerialNumbers();
				}

				switch (action) {
				case REVOKE:
					RevokeCertificateResponse revokedList = pkiFactory.revokeCertificate(KeyGeneratorTypeType.SOFTWARE,
							serialNumbers, CRLReason.UNSPECIFIED);
					DisplayUtil.displayRevokedList(revokedList.getCertificateSerialNo());
					break;
				case REVOKEALL:
					RevokeCertificateResponse revokeAllList = pkiFactory
							.revokeAllCertificates(KeyGeneratorTypeType.SOFTWARE, serialNumbers, CRLReason.UNSPECIFIED);
					DisplayUtil.displayRevokedList(revokeAllList.getCertificateSerialNo());
					break;
				case STATUS:
					CertificateStatusResponse certificateStatus = pkiFactory.certificateStatus(serialNumbers,
							KeyGeneratorTypeType.SOFTWARE);
					DisplayUtil.displayCertificateStatusList(certificateStatus.getCertificateStatus());
					break;
				case STATUSALL:
					GetOwnCertificateListResponse ownCertificateList = pkiFactory
							.getOwnCertificateList(KeyGeneratorTypeType.SOFTWARE);
					DisplayUtil.displayCertificateStatusList(ownCertificateList.getCertificateStatus());
					break;
				default:
					throw new UnsupportedOperationException("Invalid command: " + action.name());
				}
			}
			System.out.println("Action completed successfully.");
		}
	}
}