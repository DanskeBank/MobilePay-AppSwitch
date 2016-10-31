package dk.danskebank.mobilePay.demo;

import java.util.List;

import dk.danskebank.pki.pkifactoryservice.elements.CertificateStatusType;

public class DisplayUtil {
    
    public static void displayRevokedList(List<String> serialNumbers) {
        StringBuilder sb = new StringBuilder();
        sb.append("Revoked:\n");
        for(String serial: serialNumbers){
            sb.append(" - " + serial).append("\n");
        }
        System.out.println(sb);
    }
    
    public static void displayCertificateStatusList(List<CertificateStatusType> certificateStatusList) {
        StringBuilder sb = new StringBuilder();
        for(CertificateStatusType certStatus: certificateStatusList){
            String status = certStatus.getStatus().getGood()!=null ? "Good" : 
                certStatus.getStatus().getRevoked()!=null ? "Revoked" : 
                certStatus.getStatus().getExpired()!=null ? "Expired" : "Expires soon";
            sb.append("Certificate:\n")
            .append(" - Serial: ").append(certStatus.getCertificateSerialNo()).append("\n")
            .append(" - CertificateType: ").append(certStatus.getCertificateType().value()).append("\n")
            .append(" - MatchingSerial: ").append(certStatus.getMatchingCertificateSerialNo()).append("\n")
            .append(" - Status: " + status).append("\n");
        }
        
        sb.append("Total certificates: " + certificateStatusList.size());
        System.out.println(sb);
    }
    
    public static void displayHelpText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usage: TestPKIFactory [-action]\n\n") 
          .append("Where action is:\n") 
          .append("    -create       Issues new ecryption and signing certificates.\n") 
          .append("    -renew        Renews currently issued certificates.\n") 
          .append("    -revoke [<serial> ...]\n") 
          .append("                  Revokes one or more certificates by specified serial numbers\n") 
          .append("                  in decimal format. If no serial specified, then current\n")
          .append("                  issued certificates are revoked.\n") 
          .append("    -revokeall [<exclude-serial> ...]\n") 
          .append("                  Revokes all issued certificates except the ones with provided\n") 
          .append("                  serial number in decimal format. If no exclude-serial is\n") 
		  .append("                  specified, then current issued certificates are NOT revoked.\n")
          .append("    -status [<serial> ...]\n") 
          .append("                  Displays status of one or more certificates by serial numbers\n") 
          .append("                  in decimal format. If no serial specified current issued\n") 
          .append("                  certificates status is shown.\n")
          .append("    -statusall    Lists all issued certificates, active or not.\n") 
          .append("    -help         Displays this help text.");
                  
        System.out.println(sb);
    }
}
