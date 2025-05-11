package com.example.util;

import java.util.regex.Pattern;

public class PacketRule {
    private final int id;
    private final Pattern pattern;
    private final String[] rawRule;
    private volatile boolean active = true;
    //{protocol, srcMac, srcIP, srcPort, dstMac, dstIP, dstPort};
    public PacketRule(int id, String[] rawRule) {
        this.id = id;
        this.rawRule = rawRule;
        this.pattern = RegexPattern.patternCreate(rawRule);
    }

    public int getId() {
        return id;
    }

    public String[] getRawRule() {
        return rawRule;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public boolean matches(String packet) {
        return active && pattern.matcher(packet).find();
    }
}

