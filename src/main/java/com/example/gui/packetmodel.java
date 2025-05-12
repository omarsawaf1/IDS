package com.example.gui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class packetmodel {
    private int number;
    private String time;
    private String source;
    private String destination;
    private String protocol;
    private int length;
    private String info;
    private boolean isAlert;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public packetmodel(int number, String source, String destination, String protocol, int length, String info) {
        this.number = number;
        this.time = LocalDateTime.now().format(formatter);
        this.source = source;
        this.destination = destination;
        this.protocol = protocol;
        this.length = length;
        this.info = info;
        this.isAlert = info.toUpperCase().contains("ALERT");
    }
    
    public Object[] toTableRow() {
        return new Object[]{number, time, source, destination, protocol, length, info};
    }
    
    // Getters
    public int getNumber() { return number; }
    public String getTime() { return time; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public String getProtocol() { return protocol; }
    public int getLength() { return length; }
    public String getInfo() { return info; }
    public boolean isAlert() { return isAlert; }
}
