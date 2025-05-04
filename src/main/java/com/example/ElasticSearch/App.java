package com.example.ElasticSearch;

public class App {
    public static void main(String[] args) {
        ElasticsearchManager esManager = new ElasticsearchManager();
        esManager.deleteIndex("packets");
        esManager.createPacketIndex();

        EthernetHeader ethernet = new EthernetHeader();
        ethernet.setSource("00:0c:29:b7:79:36");
        ethernet.setDestination("00:50:56:f7:f3:68");
        ethernet.setType("0x0800");

        IPv4Header ipv4 = new IPv4Header();
        ipv4.setSourceIp("192.168.47.129");
        ipv4.setDestinationIp("192.168.47.2");
        ipv4.setProtocol("UDP");
        ipv4.setTtl(64);

        UDPHeader udp = new UDPHeader();
        udp.setSourcePort(36316);
        udp.setDestinationPort(53);

        DNSHeader dns = new DNSHeader();
        dns.setQname("contile.services.mozilla.com");
        dns.setQtype("A");

        String rawPacket = """
            Packet captured: [Ethernet Header (14 bytes)]
            Destination address: 00:50:56:f7:f3:68
            Source address: 00:0c:29:b7:79:36
            Type: 0x0800 (IPv4)
            [IPv4 Header (20 bytes)]
            Source: /192.168.47.129
            Destination: /192.168.47.2
            [UDP Header (8 bytes)]
            Source port: 36316
            Destination port: 53
            [DNS Header (46 bytes)]
            QNAME: contile.services.mozilla.com
        """;

        PacketData packet = new PacketData();
        packet.setEthernet(ethernet);
        packet.setIpv4(ipv4);
        packet.setUdp(udp);
        packet.setDns(dns);
        packet.setRawPacket(rawPacket);

        esManager.indexPacket(packet);

        // Add more 15 different packets
for (int i = 0; i < 15; i++) {
    EthernetHeader eth = new EthernetHeader();
    eth.setSource("00:11:22:33:44:" + String.format("%02x", i));
    eth.setDestination("aa:bb:cc:dd:ee:" + String.format("%02x", i));
    eth.setType("0x0800");

    IPv4Header ip = new IPv4Header();
    ip.setSourceIp("10.0.0." + (i + 1));
    ip.setDestinationIp("10.0.1." + (i + 1));
    ip.setProtocol((i % 2 == 0) ? "TCP" : "UDP");
    ip.setTtl(50 + i);

    UDPHeader udpHeader = new UDPHeader();
    udpHeader.setSourcePort(1000 + i);
    udpHeader.setDestinationPort(2000 + i);

    DNSHeader dnsHeader = new DNSHeader();
    dnsHeader.setQname("domain" + i + ".example.com");
    dnsHeader.setQtype((i % 2 == 0) ? "A" : "AAAA");

    String raw = String.format("""
        Packet %d
        Ethernet: src=%s dst=%s
        IP: src=%s dst=%s protocol=%s TTL=%d
        UDP: srcPort=%d dstPort=%d
        DNS: qname=%s qtype=%s
        """, i,
        eth.getSource(), eth.getDestination(),
        ip.getSourceIp(), ip.getDestinationIp(), ip.getProtocol(), ip.getTtl(),
        udpHeader.getSourcePort(), udpHeader.getDestinationPort(),
        dnsHeader.getQname(), dnsHeader.getQtype());

    PacketData p = new PacketData();
    p.setEthernet(eth);
    p.setIpv4(ip);
    p.setUdp(udpHeader);
    p.setDns(dnsHeader);
    p.setRawPacket(raw);

    esManager.indexPacket(p);
}


System.out.println("\n--- Search Tests ---");
esManager.searchByDestinationIp("10.0.1.5");
esManager.searchBySourceIp("10.0.0.3");
esManager.searchByProtocol("TCP");
esManager.searchByProtocol("UDP");
esManager.searchBySourcePort(1004);
esManager.searchByDestinationPort(2007);
esManager.searchByTTLRange(52, 60);
esManager.searchByDnsQuery("domain8.example.com");
esManager.searchInRawText("domain5");

    }
}
