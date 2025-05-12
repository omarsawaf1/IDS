package com.example.gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Packet {
    private int id;
    private LocalDateTime timestamp;
    private String protocol;
    private String sourceMac;
    private String sourceIp;
    private int sourcePort;
    private String destMac;
    private String destIp;
    private int destPort;
    private int length;
    private String info;
    private byte[] rawData;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public Packet(int id, String protocol, String sourceIp, int sourcePort, 
                  String destIp, int destPort, int length, String info) {
        this.id = id;
        this.timestamp = LocalDateTime.now();
        this.protocol = protocol;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destIp = destIp;
        this.destPort = destPort;
        this.length = length;
        this.info = info;
    }
    
    // Full constructor
    public Packet(int id, LocalDateTime timestamp, String protocol, 
                  String sourceMac, String sourceIp, int sourcePort,
                  String destMac, String destIp, int destPort, 
                  int length, String info, byte[] rawData) {
        this.id = id;
        this.timestamp = timestamp;
        this.protocol = protocol;
        this.sourceMac = sourceMac;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.destMac = destMac;
        this.destIp = destIp;
        this.destPort = destPort;
        this.length = length;
        this.info = info;
        this.rawData = rawData;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getFormattedTimestamp() {
        return timestamp.format(formatter);
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getSourceMac() {
        return sourceMac;
    }
    
    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }
    
    public String getSourceIp() {
        return sourceIp;
    }
    
    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }
    
    public int getSourcePort() {
        return sourcePort;
    }
    
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }
    
    public String getDestMac() {
        return destMac;
    }
    
    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }
    
    public String getDestIp() {
        return destIp;
    }
    
    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }
    
       public int getDestPort() {
        return destPort;
    }
    
    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }
    
    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
    
    public String getInfo() {
        return info;
    }
    
    public void setInfo(String info) {
        this.info = info;
    }
    
    public byte[] getRawData() {
        return rawData;
    }
    
    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }
    
    public Object[] toTableRow() {
        return new Object[]{
            id,
            getFormattedTimestamp(),
            protocol,
            sourceIp + ":" + sourcePort,
            destIp + ":" + destPort,
            length,
            info
        };
    }
    
    public Object[] toDetailedTableRow() {
        return new Object[]{
            id,
            getFormattedTimestamp(),
            protocol,
            sourceMac,
            sourceIp,
            sourcePort,
            destMac,
            destIp,
            destPort,
            length,
            info
        };
    }
}

