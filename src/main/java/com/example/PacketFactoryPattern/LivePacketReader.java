package com.example.PacketFactoryPattern;

import java.net.InetAddress;

import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

public class LivePacketReader implements PacketReader {
    private PcapHandle handle;
    private PromiscuousMode mode ;
    private PcapNetworkInterface nif;
    

    public LivePacketReader(String networkInterfaceIp) throws Exception {
        // this.handle = Pcaps.openLive(networkInterfaceName, 65536, PcapHandle.PromiscuousMode.PROMISCUOUS, 10);
        InetAddress addr = InetAddress.getByName(networkInterfaceIp);
        nif = Pcaps.getDevByAddress(addr); 
            if (nif == null) {
                System.err.println("No network interface found for the provided address.");
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

