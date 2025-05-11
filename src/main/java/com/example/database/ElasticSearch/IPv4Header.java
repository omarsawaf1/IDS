package com.example.database.ElasticSearch;

public class IPv4Header {
    private String sourceIp;
    private String destinationIp;
    private String protocol;
    private int ttl;

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }

    public String getDestinationIp() { return destinationIp; }
    public void setDestinationIp(String destinationIp) { this.destinationIp = destinationIp; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getTtl() { return ttl; }
    public void setTtl(int ttl) { this.ttl = ttl; }
}
