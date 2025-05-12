package com.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;

public class PacketController {
    private PacketCaptureTask captureTask;
    private JTable packetTable;
    private DefaultTableModel tableModel;
    
    public PacketController(PacketCaptureTask captureTask, JTable packetTable, DefaultTableModel tableModel) {
        this.captureTask = captureTask;
        this.packetTable = packetTable;
        this.tableModel = tableModel;
    }
    
    public ActionListener createStartButtonListener() {
        return e -> {
            // Start packet capture
            captureTask.startCapture();
        };
    }
    
    public ActionListener createStopButtonListener() {
        return e -> {
            // Stop packet capture
            captureTask.stopCapture();
        };
    }
    
    public ActionListener createRestartButtonListener() {
        return e -> {
            // Restart packet capture
            captureTask.stopCapture();
            tableModel.setRowCount(0); // Clear table
            captureTask.startCapture();
        };
    }
}
