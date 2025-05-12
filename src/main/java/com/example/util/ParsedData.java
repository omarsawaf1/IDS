package com.example.util;

// import java.util.HashMap;
import java.util.Map;

public class ParsedData {
    private String rowData;
    private Map<String, String> parsedData;
    private boolean alertflag;
    private int ruleid;
    public ParsedData(String rowData, Map<String, String> parsedData, boolean alertflag,int ruleid) {
        this.rowData = rowData;
        this.parsedData = parsedData;
        this.alertflag = alertflag;
        this.ruleid = ruleid;
    }
    public String getrowData() {
        return this.rowData;
    }
    public Map<String, String> getparsedData() {
        return this.parsedData;
    }
    public boolean getalertflag() {
        return alertflag;
    }
    public int getruleid() {
        return ruleid;
    }

}


    