package com.example.PacketFactoryPattern;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OfflinePacketReader implements PacketReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflinePacketReader.class);
    private PcapHandle handle;

    public OfflinePacketReader(String pcapFile) throws Exception {
        try {
            this.handle = Pcaps.openOffline(pcapFile);
            LOGGER.info("Opened offline pcap file: {}", pcapFile);
        } catch (Exception e) {
            LOGGER.error("Failed to open pcap file: {}", pcapFile, e);
            throw e;
        }
    }

    @Override
    public Packet getNextPacket() {
        try {
            return handle.getNextPacket();
        } catch (Exception e) {
            LOGGER.error("Error while getting next packet", e);
            // e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        if (handle != null) {
            handle.close();
            LOGGER.info("Closed pcap handle");
        }
    }
}

