package dk.danskebank.mobilePay.demo;

import java.util.ArrayList;

public class ActionCommandLineParser {
    
    public enum Action {
        CREATE, RENEW, REVOKE, REVOKEALL,
        STATUS, STATUSALL, HELP, NONE
    }
    
    private ArrayList<String> serialNumbers;
    
    public Action getAction(String[] commandLineParams){
        if(commandLineParams.length == 0){
            return Action.NONE;
        }
        
        if(commandLineParams.length > 1){
            serialNumbers = new ArrayList<String>();
            for(int i = 1; i < commandLineParams.length; i++){
                serialNumbers.add(commandLineParams[i]);
            }
        }
        else{
            serialNumbers = null;
        }
        
        String cmd = commandLineParams[0].toUpperCase().substring(1);

        try{
            return Action.valueOf(cmd);
        }
        catch(IllegalArgumentException e){
            return Action.HELP;
        }
    }
    
    public ArrayList<String> getSerialNumbers(){
        return serialNumbers;
    }
}
