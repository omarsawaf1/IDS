package com.example.PacketFactoryPattern;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

public class LivePacketReader implements PacketReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LivePacketReader.class);

    private PcapHandle handle;
    private PromiscuousMode mode;
    private PcapNetworkInterface nif;
    

    public LivePacketReader(String networkInterfaceIp) throws Exception {
        // this.handle = Pcaps.openLive(networkInterfaceName, 65536, PcapHandle.PromiscuousMode.PROMISCUOUS, 10);
        InetAddress addr = InetAddress.getByName(networkInterfaceIp);
        nif = Pcaps.getDevByAddress(addr); 
            if (nif == null) {
                LOGGER.error("No network interface found for the provided address.");
                return;
            }
        mode = PromiscuousMode.PROMISCUOUS;
        int snapLen = 65536;
        int timeout = 10;
        handle = nif.openLive(snapLen, mode, timeout);
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

