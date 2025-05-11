package com.example.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


// Protocol Source:IP_Address Source:Port  Destination:IP_Address Destination:Port  Message
//  0          1                   2              3                       4            5
//  TCP         any                 80         10.199.12.8	           any           any

public class RegexPattern {
    private static final Logger log = LoggerFactory.getLogger(RegexPattern.class);
    public static void main(String[] args) {
    String packet="""
[Ethernet Header (14 bytes)]
  Destination address: d4:6b:a6:cb:be:82
  Source address: f4:b3:01:11:f1:4d
  Type: 0x0800 (IPv4)
[IPv4 Header (20 bytes)]
  Version: 4 (IPv4)
  IHL: 5 (20 [bytes])
  TOS: [precedence: 0 (Routine)] [tos: 0 (Default)] [mbz: 0]
  Total length: 52 [bytes]
  Identification: 44196
  Flags: (Reserved, Don't Fragment, More Fragment) = (false, true, false)
  Fragment offset: 0 (0 [bytes])
  TTL: 128
  Protocol: 6 (TCP)
  Header checksum: 0x0000
  Source address: /192.168.1.13
  Destination address: /76.223.11.49
[TCP Header (32 bytes)]
  Source port: 57485 (unknown)
  Destination port: 80 (HTTP)
  Sequence Number: 3060699315
  Acknowledgment Number: 0
  Data Offset: 8 (32 [bytes])
  Reserved: 0
  URG: false
  ACK: false
  PSH: false
  RST: false
  SYN: true
  FIN: false
  Window: 64240
  Checksum: 0x19ec
  Urgent Pointer: 0
  Option: [Kind: 2 (Maximum Segment Size)] [Length: 4 bytes] [Maximum Segment Size: 1460 bytes]
  Option: [Kind: 1 (No Operation)]
  Option: [Kind: 3 (Window Scale)] [Length: 3 bytes] [Shift Count: 8]
  Option: [Kind: 1 (No Operation)]
  Option: [Kind: 1 (No Operation)]
  Option: [Kind: 4 (SACK Permitted)] [Length: 2 bytes]
""";
        String[] rule = {"TCP", "any", "any", "76.223.11.49", "80"};
        System.out.println("Rule Validation is :"+RulesValidation.rulesValidation(rule));
        // System.out.println(matcher.matches());
        
        Pattern p = patternCreate(rule);
        Matcher m = p.matcher(packet);

        // Use find() rather than matches(), because matches()
        // attempts to match the **entire** text to the regex exactly.
        // find() succeeds if the pattern can match **anywhere**.
        // if (m.find()) {
        if (m.matches()) {
            System.out.println("Packet matches rule!");
        } else {
            System.out.println("No match.");
        }

    }
    public static Pattern patternCreate(String[] rule) {
        log.info("Starting Pattern Creation...");
        StringBuilder regex = new StringBuilder("(?s)");  // (?s) = dot matches newlines
    
        // Protocol
        if (!rule[0].equalsIgnoreCase("any")) {
            regex.append("(?=.*\\b")
                 .append(Pattern.quote(rule[0]))
                 .append("\\b)");
        }
        // Source IP
        if (!rule[1].equalsIgnoreCase("any")) {
            String sip = rule[1].replace(".", "\\.");
            regex.append("(?=.*Source address: /").append(sip).append(")");
        }
        // Source Port
        if (!rule[2].equalsIgnoreCase("any")) {
            regex.append("(?=.*Source port: ").append(rule[2]).append(")");
        }
        // Destination IP
        if (!rule[3].equalsIgnoreCase("any")) {
            String dip = rule[3].replace(".", "\\.");
            regex.append("(?=.*Destination address: /").append(dip).append(")");
        }
        // Destination Port
        if (!rule[4].equalsIgnoreCase("any")) {
            regex.append("(?=.*Destination port: ").append(rule[4]).append(")");
        }
    
        regex.append(".*");  // match the rest of the input
        // System.out.println(regex);
        log.debug("Regex: {}" , regex);
        log.info("Finishing Pattern Creation...");
        return Pattern.compile(regex.toString());
    }
    
}
