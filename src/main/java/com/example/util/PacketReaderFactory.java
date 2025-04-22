package com.example.util;
/*
 █████╗░████████╗██╗░░░░░
██╔══██║╚══██╔══╝██║░░░░░
██║░░██║░░░██║░░░██║░░░░░
██║░░██║░░░██║░░░██║░░░░░
██╚══██║░░░██║░░░███████╗
╚█████╔╝░░░╚═╝░░░╚══════╝

*/

public class PacketReaderFactory {
    public static PacketReader createPacketReader(String mode, String source) throws Exception {
        if ("live".equalsIgnoreCase(mode)) {
            // source is ip address
            return new LivePacketReader(source);
        } else if ("offline".equalsIgnoreCase(mode)) {
            // source is pcapng file
            return new OfflinePacketReader(source);
        } else {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }
}

