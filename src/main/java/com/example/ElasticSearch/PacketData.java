package com.example.ElasticSearch;

public class PacketData {
    private EthernetHeader ethernet;
    private IPv4Header ipv4;
    private UDPHeader udp;
    private DNSHeader dns;
    private String rawPacket;

    public EthernetHeader getEthernet() { return ethernet; }
    public void setEthernet(EthernetHeader ethernet) { this.ethernet = ethernet; }

    public IPv4Header getIpv4() { return ipv4; }
    public void setIpv4(IPv4Header ipv4) { this.ipv4 = ipv4; }

    public UDPHeader getUdp() { return udp; }
    public void setUdp(UDPHeader udp) { this.udp = udp; }

    public DNSHeader getDns() { return dns; }
    public void setDns(DNSHeader dns) { this.dns = dns; }

    public String getRawPacket() { return rawPacket; }
    public void setRawPacket(String rawPacket) { this.rawPacket = rawPacket; }
}
