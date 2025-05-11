package com.example.PacketFactoryPattern;
/*
 █████╗░████████╗██╗░░░░░
██╔══██║╚══██╔══╝██║░░░░░
██║░░██║░░░██║░░░██║░░░░░
██║░░██║░░░██║░░░██║░░░░░
██╚══██║░░░██║░░░███████╗
╚█████╔╝░░░╚═╝░░░╚══════╝

*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketReaderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketReaderFactory.class);

    public static PacketReader createPacketReader(String mode, String source) throws Exception {
        if ("live".equalsIgnoreCase(mode)) {
            // source is ip address
            LOGGER.info("Creating live packet reader for interface: {}", source);
            return new LivePacketReader(source);
        } else if ("offline".equalsIgnoreCase(mode)) {
            // source is pcapng file
            LOGGER.info("Creating offline packet reader for file: {}", source);
            return new OfflinePacketReader(source);
        } else {
            LOGGER.error("Invalid mode: {}", mode);
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }
}

