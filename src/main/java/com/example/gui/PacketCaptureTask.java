package com.example.gui;

public class PacketCaptureTask {
    private boolean isRunning = false;
    
    public void startCapture() {
        isRunning = true;
        // Start packet capture logic here
    }
    
    public void stopCapture() {
        isRunning = false;
        // Stop packet capture logic here
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}
