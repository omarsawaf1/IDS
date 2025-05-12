package com.example;



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
        Rules rules= new Rules();
        User user= new User();
        // Alerts alerts= new Alerts();
        //user test signup
        // System.out.println( user.signup("ahmly","fadaf12346"));
        // System.out.println(user.login("ahmly","fadaf12346"));
        // System.out.println(user.delete(9));
        //rules test
        // System.out.println(rules.insert("TCP", "192.168.1.1", "80", "00:11:22:33:44:55", "00:66:77:88:99:00", "8080", "192.168.2.2", 6));
        // System.out.println(rules.insert("UDP", "10.0.0.1", "53", "AA:BB:CC:DD:EE:FF", "11:22:33:44:55:66", "5353", "10.0.0.2", 6));
        // System.out.println(rules.insert("ICMP", "172.16.0.1", "0", "AB:CD:EF:12:34:56", "65:43:21:BA:DC:FE", "0", "172.16.0.2", 6));
        // System.out.println(rules.insert("TCP", "8.8.8.8", "443", "00:00:00:00:00:01", "00:00:00:00:00:02", "8443", "1.1.1.1", 6));
        // System.out.println(rules.insert("UDP", "192.168.100.1", "161", "DE:AD:BE:EF:FE:ED", "CA:FE:BA:BE:12:34", "162", "192.168.100.2", 6));
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
        
        // user.login("ahmed1231","hello");
                // Load existing rules from DB (if any)
        // int loaded = RuleQueue.queueLoadRulesList();
        // System.out.println("Loaded rules: " + loaded);

        // // Create a new rule
        // String[] rawRuleMap = {
        //     "TCP",       // protocol
        //     "00:11:22",  // srcMac
        //     "192.168.1.1", // srcIP
        //     "8080",      // srcPort
        //     "00:44:55",  // dstMac
        //     "192.168.1.2", // dstIP
        //     "80"         // dstPort
        // };

        // // Add it
        // int testRuleId = RuleQueue.addRuleToQueue( rawRuleMap);
        // System.out.println("Added rule with ID: " );

        // Remove it
        // boolean removed = RuleQueue.removeRuleFromQueueById(16);
        // System.out.println("Removed rule: " + removed);
                // Initialize ElasticsearchManager
        // ElasticsearchManager manager = new ElasticsearchManager();

        // // Test indexing a raw packet for a user
        // String rawContent1 = "Packet data 6 for user 6";
        // manager.indexUserPacket(6, rawContent1);

        // // Test searching user 1's packets with a keyword
        // List<String> resultsUser1 = manager.searchUserPackets(6, "Packet");
        // System.out.println("Results for user 6:");
        // System.out.println(resultsUser1);

        // // Shutdown Elasticsearch connection
        // try {
        //     ElasticsearchManager.shutdown();
        // } catch (Exception e) {
        //     System.err.println("Error shutting down: " + e.getMessage());
        // }
    }
}