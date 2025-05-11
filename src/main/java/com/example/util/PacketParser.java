package com.example.util;

import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketParser {
    private static final Logger logger = LoggerFactory.getLogger(PacketParser.class);

    public static String[] matcher(String packet) {
        logger.debug("Parsing packet: {}", packet);
        String[] fields = packet.split("\\n");
        String[] extractedData = new String[8];

        // Extracting MAC addresses
        extractedData[5] = fields[1].substring(fields[1].indexOf(":") + 2);
        extractedData[6] = fields[2].substring(fields[2].indexOf(":") + 2);

        for (String field : fields) {
            if (field.contains("Destination port:")) {
                int index = field.indexOf(":");
                extractedData[4] = field.substring(index + 2);
            } else if (field.contains("Source port:")) {
                int index = field.indexOf(":");
                extractedData[2] = field.substring(index + 2);
            } else if (field.contains("TCP")) {
                extractedData[0] = "TCP";
            } else if (field.contains("UDP")) {
                extractedData[0] = "UDP";
            } else if (field.contains("Destination address:")) {
                int index = field.indexOf(":");
                extractedData[3] = field.substring(index + 3);
            } else if (field.contains("Source address:")) {
                int index = field.indexOf(":");
                extractedData[1] = field.substring(index + 3);
            } else if (field.contains("Hex stream: ")) {
                int index = field.indexOf(":");
                String hexStream = field.substring(index + 1).replaceAll(" ", "");
                extractedData[7] = HexToAscii.hexToAscii(hexStream);
            }
        }
        logger.debug("Extracted data: {}", (Object) extractedData);
        return extractedData;
    }

    public static Map<String, String> parsePacket(String packet) {
        if (packet == null) {
            logger.warn("Received null packet");
            return null;
        }
        logger.debug("Received packet for parsing");
        Map<String, String> packetData = new HashMap<>();
        String[] extractedData = matcher(packet);

        packetData.put("protocol", extractedData[0]);
        packetData.put("srcIp", extractedData[1]);
        packetData.put("srcPort", extractedData[2]);
        packetData.put("dstIp", extractedData[3]);
        packetData.put("dstPort", extractedData[4]);
        packetData.put("dstMac", extractedData[5]);
        packetData.put("srcMac", extractedData[6]);

        if (extractedData[4].contains("(HTTP)")) {
            packetData.put("data", extractedData[7]);
        }
        logger.debug("Parsed packet data: {}", packetData);

        return packetData;
    }
}

