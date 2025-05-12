package com.example.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.PacketFactoryPattern.PacketReader;
import com.example.PacketFactoryPattern.PacketReaderFactory;

public class PacketParser {
    private static final Logger logger = LoggerFactory.getLogger(PacketParser.class);
     // Pre-compiled patterns
    private static final Pattern PROTO_P = Pattern.compile("^\\s*Protocol:\\s*\\d+\\s*\\((TCP|UDP|ICMP|DNS|ARP|SCTP)\\)");
    private static final Pattern ETH_DEST_P = Pattern.compile("^\\s*Destination address: ([0-9a-fA-F:]{2,})");
    private static final Pattern ETH_SRC_P  = Pattern.compile("^\\s*Source address: ([0-9a-fA-F:]{2,})");
    private static final Pattern IP_SRC_P   = Pattern.compile("^\\s*Source address: /?([0-9.]+)");
    private static final Pattern IP_DST_P   = Pattern.compile("^\\s*Destination address: /?([0-9.]+)");
    private static final Pattern PORT_SRC_P = Pattern.compile("^\\s*Source port: (\\d+)");
    private static final Pattern PORT_DST_P = Pattern.compile("^\\s*Destination port: (\\d+)");
    private static final Pattern HEX_P      = Pattern.compile("^.*Hex stream:\\s*([0-9A-Fa-f ]+)$");

public static void main(String[] args) {
        System.out.println( "Hello World!" );
         try {
            String mode ="offline";
            String  source="capture.pcapng";
            PacketReader reader = PacketReaderFactory.createPacketReader(mode, source);
            Packet packet;
            while ((packet = reader.getNextPacket()) != null) {
                // System.out.println(packet);
                Map<String, String> packetData = parsePacket(packet.toString());
                if(packetData == null) {
                    System.out.println("Failed to parse packet");
                    break;
                }
            //    System.out.println(matcher(packet.toString()));
                System.out.println(packetData);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String[] matcher(String packetText) {
        logger.debug("Parsing packet: {}", packetText);
        String[] extracted = new String[8];

        String[] lines = packetText.split("\\r?\\n");
        for (String line : lines) {
            Matcher m;
            // 1. Protocol
            m = PROTO_P.matcher(line);
            if (m.find()) {
                extracted[0] = m.group(1);
                continue;
            }
            // 2. Ethernet Destination MAC (first occurrence)
            m = ETH_DEST_P.matcher(line);
            if (m.find() && extracted[5] == null) {
                extracted[5] = m.group(1);
                continue;
            }
            // 3. Ethernet Source MAC (first occurrence)
            m = ETH_SRC_P.matcher(line);
            if (m.find() && extracted[6] == null) {
                extracted[6] = m.group(1);
                continue;
            }
            // 4. IP Source
            m = IP_SRC_P.matcher(line);
            if (m.find()) {
                extracted[1] = m.group(1);
                continue;
            }
            // 5. IP Destination
            m = IP_DST_P.matcher(line);
            if (m.find()) {
                extracted[3] = m.group(1);
                continue;
            }
            // 6. TCP/UDP Source Port
            m = PORT_SRC_P.matcher(line);
            if (m.find()) {
                extracted[2] = m.group(1);
                continue;
            }
            // 7. TCP/UDP Destination Port
            m = PORT_DST_P.matcher(line);
            if (m.find()) {
                extracted[4] = m.group(1);
                continue;
            }
            // 8. Hex Stream → ASCII
            m = HEX_P.matcher(line);
            if (m.find()) {
                String hex = m.group(1).replaceAll("\\s+", "");
                extracted[7] = HexToAscii.hexToAscii(hex);
            }
        }

        logger.debug("Extracted data: protocol={}, srcIP={}, srcPort={}, dstIP={}, dstPort={}, ethDst={}, ethSrc={}, payload={}",
                extracted[0], extracted[1], extracted[2], extracted[3], extracted[4], extracted[5], extracted[6], extracted[7]);

        return extracted;
    }

   public static Map<String, String> parsePacket(String packet) {
    Map<String, String> packetData = new HashMap<>();

    // Default to "null" for all expected headers
    packetData.put("protocol", "null");
    packetData.put("srcIp", "null");
    packetData.put("srcPort", "null");
    packetData.put("dstIp", "null");
    packetData.put("dstPort", "null");
    packetData.put("dstMac", "null");
    packetData.put("srcMac", "null");
    packetData.put("payload", "null");

    if (packet == null) {
        logger.warn("Received null packet — returning map with all null values.");
        return packetData;
    }

    logger.debug("Parsing packet:\n{}", packet);
    String[] extractedData = matcher(packet);

    if (extractedData == null || extractedData.length < 8) {
        logger.warn("Incomplete extraction — returning map with default 'null' values.");
        return packetData;
    }

    // Fill in extracted values if present
    if (extractedData[0] != null) packetData.put("protocol", extractedData[0]);
    if (extractedData[1] != null) packetData.put("srcIp", extractedData[1]);
    if (extractedData[2] != null) packetData.put("srcPort", extractedData[2]);
    if (extractedData[3] != null) packetData.put("dstIp", extractedData[3]);
    if (extractedData[4] != null) packetData.put("dstPort", extractedData[4]);
    if (extractedData[5] != null) packetData.put("dstMac", extractedData[5]);
    if (extractedData[6] != null) packetData.put("srcMac", extractedData[6]);
    if (extractedData[7] != null) packetData.put("payload", extractedData[7]);

    logger.debug("Parsed packet data: {}", packetData);
    return packetData;
}

}

