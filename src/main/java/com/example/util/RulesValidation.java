package com.example.util;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

// Protocol Source:MAC_Address Source:IP_Address Source:Port Destination:MAC_Address Destination:IP_Address Destination:Port  Message
//  0          1                   2              3                       4            5                      6
//  TCP         any                 80         10.199.12.8	           any           any                     any

public class RulesValidation {
    private static final Logger log = LoggerFactory.getLogger(RegexPattern.class);
    public static boolean rulesValidation(String[] token) {
        // String[] rule = {"TCP","f4:b3:01:11:f1:4d", "any", "any","d4:6b:a6:cb:be:82", "76.223.11.49", "80"};
        log.info("Starting Validation...");
        log.debug("Protocol: " + token[0] + " Source MAC: " + token[1] + " Source IP: " + token[2] + " Source Port: " + token[3] + " Destination MAC: " + token[4] + " Destination IP: " + token[5] + " Destination Port: " + token[6]);
        if(validateProtocol(token[0]) && validateMac(token[1]) && validateIp(token[2]) && validatePort(token[3]) && validateMac(token[4]) && validateIp(token[5]) && validatePort(token[6])) {
            return true;
        }
        log.info("Finishing Validation...");
        return false;
    }
    private static boolean validateMac(String input) {
        if(input==null) return false;
        if(input.equalsIgnoreCase("any")) return true;
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        return p.matcher(input).matches();
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
