package com.example.producer;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

public class OfflinePacketReader {
    private PcapHandle handle;

    public OfflinePacketReader(String pcapFile) throws Exception {
        this.handle = Pcaps.openOffline(pcapFile);
    }

    public Packet getNextPacket() {
        try {
            return handle.getNextPacket();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void close() {
        if (handle != null) {
            handle.close();
        }
    }
}