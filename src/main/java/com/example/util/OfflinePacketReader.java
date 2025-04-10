package com.example.util;


import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

public class OfflinePacketReader implements PacketReader {
    private PcapHandle handle;

    public OfflinePacketReader(String pcapFile) throws Exception {
        this.handle = Pcaps.openOffline(pcapFile);
    }

    @Override
    public Packet getNextPacket() {
        try {
            return handle.getNextPacket();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {
        if (handle != null) {
            handle.close();
        }
    }
}
