package com.example.util;


import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.PrintWriter;  

public class OfflinePacketReader {
    public static void main(String[] args) {
        String pcapFile = "./ids.pcapng";

        try {
            // Open the PCAP file in offline mode
            PcapHandle handle = Pcaps.openOffline(pcapFile);

            Packet packet;
            // Process packets one by one
            PrintWriter output = new PrintWriter("output.txt");
            while ((packet = handle.getNextPacket()) != null) {
                // System.out.println(packet);
                output.append(packet.toString());
                // System.out.println("Successfully wrote to the file.");
            }
            output.close();

            // Close the handle when done
            handle.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
