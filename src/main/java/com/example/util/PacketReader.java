package com.example.util;

import org.pcap4j.packet.Packet;

public interface PacketReader {
    Packet getNextPacket();
    void close();
}
