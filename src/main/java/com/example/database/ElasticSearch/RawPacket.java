package com.example.database.ElasticSearch;

public class RawPacket {
    private String raw;

    public RawPacket() {}

    public RawPacket(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
