package com.example.database.ElasticSearch;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IDSAlert {
    private String timestamp;
    private String sourceIp;
    private String threatType;
    private String severity;
    private String id;

    // Single constructor that initializes the ID
    public IDSAlert() {
        this.id = UUID.randomUUID().toString(); // Initialize ID here
    }

    // Getters and setters with @JsonProperty annotations
    @JsonProperty("timestamp")
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @JsonProperty("sourceIp")
    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }

    @JsonProperty("threatType")
    public String getThreatType() { return threatType; }
    public void setThreatType(String threatType) { this.threatType = threatType; }

    @JsonProperty("severity")
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    @JsonProperty("id")
    public String getId() { return id; }
    // No setter for ID (auto-generated)
}