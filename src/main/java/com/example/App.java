package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.pcap4j.packet.Packet;

import com.example.PacketFactoryPattern.PacketReader;
import com.example.PacketFactoryPattern.PacketReaderFactory;
import com.example.database.mysql.*;

// For testing purposes
// public class App 
// {

//     public static void main( String[] args )
//     {
//          try {
//             // For example, "live" mode with network interface "eth0" or "offline" mode with file "ids.pcapng"
//             // String mode = args[0];  // e.g., "live" or "offline"
//             // String source = args[1]; // network interface ip or pcap file path

//             String mode ="offline";
//             String  source="http.pcapng";
            
//             PacketReader reader = PacketReaderFactory.createPacketReader(mode, source);
//             // Read packets
//             Packet packet;
//             while ((packet = reader.getNextPacket()) != null) {
//                 // Write packet to file
//                 try (FileWriter file = new FileWriter("output.txt", true);
//                     PrintWriter output = new PrintWriter(file)) {
//                     output.append("Packet captured: " + packet+"\n");
//                 } catch (IOException e) {
//                     System.err.println("Error writing to file: " + e.getMessage());
//                 }
//             }
//             reader.close();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
public class App 
{

    public static void main( String[] args )
    {
        // Rules rules= new Rules();
        // User user= new User();
        // Alerts alerts= new Alerts();
        //user test signup
        // // System.out.println( user.signup("ahmly","fadaf12346"));
        // System.out.println(user.login("ahmly","fadaf12346"));
        // System.out.println(user.delete(9));
        //rules test
        // System.out.println(rules.insert("TCP", "192.168.1.1", "80", "00:11:22:33:44:55", "00:66:77:88:99:00", "8080", "192.168.2.2", 6));
        // List<Map<String, Object>> listrules=rules.searchUserRulesList(6);
        //this will return hashmap you need to prase it to get data inside each one
        //System.out.println(listrules.get(1));
        // Map<String, Object> rule=rules.search(8);
        // if(rule!=null){
        //     System.out.println(rule.get("dstPort"));
        //     System.out.println("rule");
        // }
        // System.out.println(rules.delete(8));
        //    System.out.println(alerts.insert("TCP", "192.168.1.100", "8080", "00:11:22:33:44:55", "00:66:77:88:99:00", "12345", "192.168.1.200", 6, 9));
        //    System.out.println(alerts.search(1).get("protocol"));
        //    System.out.println(alerts.delete(6));
    }
}