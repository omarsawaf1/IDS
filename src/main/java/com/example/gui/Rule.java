package com.example.gui;

public class Rule {
    private int id;
    private String name;
    private String protocol;
    private String source;
    private String destination;
    private String port;
    private String action;
    private String description;
    
    public Rule(int id, String name, String protocol, String source, String destination, 
                String port, String action, String description) {
        this.id = id;
        this.name = name;
        this.protocol = protocol;
        this.source = source;
        this.destination = destination;
        this.port = port;
        this.action = action;
        this.description = description;
    }
    
    public Rule(boolean enabled, String action2, String protocol2, String sourceMac, String sourceIp, String sourcePort,
            String destMac, String destIp, String destPort, String description2) {
        //TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public String getSource() {
        return source;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Object[] toArray() {
        return new Object[]{id, name, protocol, source, destination, port, action, description};
    }
}
