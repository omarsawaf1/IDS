package com.example.util;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

// Protocol Source:MAC_Address Source:IP_Address Source:Port Destination:MAC_Address Destination:IP_Address Destination:Port  Message
//  0          1                   2              3                       4            5                      6
//  TCP         any                 80         10.199.12.8	           any           any                     any
public class RulesValidation {
    private static final Logger log = LoggerFactory.getLogger(RulesValidation.class);

    public static boolean rulesValidation(String[] token) {
        // String[] rule = {"TCP","f4:b3:01:11:f1:4d", "any", "any","d4:6b:a6:cb:be:82", "76.223.11.49", "80"};
        log.info("Starting Validation...");
        log.debug("Protocol: {} Source MAC: {} Source IP: {} Source Port: {} Destination MAC: {} Destination IP: {} Destination Port: {}", 
                  token[0], token[1], token[2], token[3], token[4], token[5], token[6]);
                  
        boolean isValid = validateProtocol(token[0]) && validateMac(token[1]) && validateIp(token[2]) &&
                          validatePort(token[3]) && validateMac(token[4]) && validateIp(token[5]) && 
                          validatePort(token[6]);
        
        log.info("Finishing Validation with result: {}", isValid);
        return isValid;
    }

    private static boolean validateMac(String input) {
        if (input == null) {
            log.warn("MAC validation failed: input is null");
            return false;
        }
        if (input.equalsIgnoreCase("any")) {
            return true;
        }
        boolean matches = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$").matcher(input).matches();
        if (!matches) {
            log.warn("MAC validation failed: invalid format for {}", input);
        }
        return matches;
    }

    private static boolean validateIp(String input) {
        if (input == null) {
            log.warn("IP validation failed: input is null");
            return false;
        }
        if (input.equalsIgnoreCase("any")) {
            return true;
        }
        boolean matches = Pattern.compile("^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$").matcher(input).matches();
        if (!matches) {
            log.warn("IP validation failed: invalid format for {}", input);
        }
        return matches;
    }

    private static boolean validatePort(String input) {
        if (input == null) {
            log.warn("Port validation failed: input is null");
            return false;
        }
        if (input.equalsIgnoreCase("any")) {
            return true;
        }
        try {
            int port = Integer.parseInt(input);
            boolean isValid = (port >= 0 && port < 65536);
            if (!isValid) {
                log.warn("Port validation failed: {} is out of range", port);
            }
            return isValid;
        } catch (NumberFormatException e) {
            log.warn("Port validation failed: {} is not a valid number", input);
            return false;
        }
    }

    private static boolean validateProtocol(String input) {
        if (input == null) {
            log.warn("Protocol validation failed: input is null");
            return false;
        }
        if (input.equalsIgnoreCase("any")) {
            return true;
        }
        boolean isValid = (input.equals("TCP") || input.equals("UDP"));
        if (!isValid) {
            log.warn("Protocol validation failed: {} is not a recognized protocol", input);
        }
        return isValid;
    }
}

