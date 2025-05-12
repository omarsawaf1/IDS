package com.example.util;



import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Protocol Source:MAC_Address Source:IP_Address Source:Port Destination:MAC_Address Destination:IP_Address Destination:Port  Message
//  0          1                   2              3                       4            5                      6
//  TCP         any                 80         10.199.12.8	           any           any                     any

public class RegexPattern {
    private static final Logger log = LoggerFactory.getLogger(RegexPattern.class);

    public static Pattern patternCreate(String[] rule) {
        log.info("Starting Pattern Creation...");
        StringBuilder regex = new StringBuilder("(?s)");  // (?s) = dot matches newlines
    
        // Protocol
        if (!rule[0].equalsIgnoreCase("any")) {
            regex.append("(?=.*\\b")
                 .append(Pattern.quote(rule[0]))
                 .append("\\b)");
        }
        // Source MAC
        if (!rule[1].equalsIgnoreCase("any")) {
            regex.append("(?=.*Source address: ").append(rule[1]).append(")");
        }
        // Source IP
        if (!rule[2].equalsIgnoreCase("any")) {
            String sip = rule[2].replace(".", "\\.");
            regex.append("(?=.*Source address: /").append(sip).append(")");
        }
        // Source Port
        if (!rule[3].equalsIgnoreCase("any")) {
            regex.append("(?=.*Source port: ").append(rule[3]).append(")");
        }
        // Destination MAC
        if (!rule[4].equalsIgnoreCase("any")) {
            regex.append("(?=.*Destination address: ").append(rule[4]).append(")");
        }
        // Destination IP
        if (!rule[5].equalsIgnoreCase("any")) {
            String dip = rule[5].replace(".", "\\.");
            regex.append("(?=.*Destination address: /").append(dip).append(")");
        }
        // Destination Port
        if (!rule[6].equalsIgnoreCase("any")) {
            regex.append("(?=.*Destination port: ").append(rule[6]).append(")");
        }
    
        regex.append(".*");  // match the rest of the input
        return Pattern.compile(regex.toString());
    }
    
}

