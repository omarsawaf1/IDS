package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import org.pcap4j.packet.Packet;

import com.example.util.PacketReader;
import com.example.util.PacketReaderFactory;

public class App 
{
    public static void main( String[] args )
    {
        // System.out.println( "Hello World!" );
         try {
            // For example, "live" mode with network interface "eth0" or "offline" mode with file "ids.pcapng"
            // String mode = args[0];  // e.g., "live" or "offline"
            // String source = args[1]; // network interface ip or pcap file path

            String mode ="offline";
            String  source="ids.pcapng";
            
            PacketReader reader = PacketReaderFactory.createPacketReader(mode, source);
            // Read packets
            Packet packet;
            // String dehexData="",hexStream="";
            while ((packet = reader.getNextPacket()) != null) {
                // Process packet: send to detector, log events, etc.
                // if(packet.toString().lastIndexOf("Hex stream: ")!=-1){
                //     hexStream=(packet.toString().substring(packet.toString().lastIndexOf("Hex stream: ")+12)).replaceAll(" ","").trim();
                // }
            
                // dehexData = HexToAscii.hexToAscii(hexStream);
                
                // Write packet to file
                try (FileWriter file = new FileWriter("output.txt", true);
                    PrintWriter output = new PrintWriter(file)) {
                    output.append("Packet captured: " + packet+"\n");
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }

            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
