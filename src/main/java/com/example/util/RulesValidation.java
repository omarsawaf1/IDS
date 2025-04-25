package com.example.util;
import java.util.regex.Pattern; 

// Protocol Source:IP_Address Source:Port  Destination:IP_Address Destination:Port  Message
//  0          1                   2              3                       4            5
//  TCP         any                 80         10.199.12.8	           any           any
public class RulesValidation {
    public static boolean rulesValidation(String[] token) {
        //  String[] rule = {"TCP", "any", "any", "76.223.11.49", "80"};
        if(validateProtocol(token[0]) && validateIp(token[1]) && validatePort(token[2]) && validateIp(token[3]) && validatePort(token[4])) {
            return true;
        }
        return false;
    }
    private static boolean validateIp(String input) {
        if(input==null) return false;
        if(input.equalsIgnoreCase("any")) return true;
        Pattern p = Pattern.compile("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$");
        return p.matcher(input).matches();
    }
    private static boolean validatePort(String input) {
        if(input==null) return false;
        if(input.equalsIgnoreCase("any")) return true;
        return (Integer.parseInt(input) >= 0 && Integer.parseInt(input) < 65536);
    }
    private static boolean validateProtocol(String input) {
        if(input==null) return false;
        if(input.equalsIgnoreCase("any")) return true;
        return (input.equals("TCP") || input.equals("UDP"));
    }
}
