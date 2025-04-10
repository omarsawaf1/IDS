package com.example;

import org.pcap4j.packet.Packet;

import com.example.util.PacketReader;
import com.example.util.PacketReaderFactory;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
         try {
            // For example, "live" mode with network interface "eth0" or "offline" mode with file "ids.pcapng"
            // String mode = args[0];  // e.g., "live" or "offline"
            // String source = args[1]; // network interface ip or pcap file path

            String mode ="live";
            String  source="192.168.1.104";
            
            PacketReader reader = PacketReaderFactory.createPacketReader(mode, source);

            Packet packet;
            while ((packet = reader.getNextPacket()) != null) {
                // Process packet: send to detector, log events, etc.
                System.out.println(packet);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
