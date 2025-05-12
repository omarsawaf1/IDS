package com.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.example.engine.EngineIds;
import com.example.designpatterns.ObserverPattern.Observer;
import com.example.util.ParsedData;

public class AlertPage extends JFrame implements Observer {
    // UI Components
    private JTable alertTable;
    private DefaultTableModel tableModel;
    private JTextArea alertDetailsArea;
    private JLabel statusLabel, alertCountLabel;
    private JFrame parentFrame;
    
    // Engine instance
    private EngineIds engineInstance;
    private int currentUserId = 1; // Default user ID
    
    // Colors for UI
    private final Color DARK_BG = new Color(40, 40, 40);
    private final Color DARK_PANEL = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(240, 240, 240);
    private final Color LIGHT_PANEL = new Color(255, 255, 255);
    private final Color ACCENT = new Color(0, 120, 215);
    private boolean isDarkMode = true;
    
    // Constructor with parent frame parameter
    public AlertPage(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Security Alerts");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        // Get the engine instance
        engineInstance = EngineIds.getInstance();
        
        // Register this class as an observer for alerts
        engineInstance.addObserver(this);
        
        initUI();
    }
    
    // Constructor with parent frame and user ID
    public AlertPage(JFrame parent, int userId) {
        this(parent);
        this.currentUserId = userId;
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        
        // Create toolbar
        JToolBar toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Create main content
        JSplitPane mainSplit = createMainContent();
        add(mainSplit, BorderLayout.CENTER);
        
        // Create status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
        
        // Create menu
        createMenu();
    }
    
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton clearBtn = createButton("Clear Alerts", new Color(200, 60, 60));
        JButton settingsBtn = createButton("Toggle Theme", new Color(100, 100, 100));
        
        clearBtn.addActionListener(e -> clearAlerts());
        settingsBtn.addActionListener(e -> toggleTheme());
        
        toolbar.add(clearBtn);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(settingsBtn);
        
        return toolbar;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.RED);
        button.setFocusPainted(false);
        return button;
    }
    
    private JSplitPane createMainContent() {
        // Create alert table
        String[] columns = {"No.", "Time", "Severity", "Source", "Destination", "Alert Type", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        alertTable = new JTable(tableModel);
        alertTable.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        alertTable.setForeground(isDarkMode ? Color.red : Color.BLACK);
        alertTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        alertTable.setRowHeight(25);
        
        // Add selection listener
        alertTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = alertTable.getSelectedRow();
                if (row >= 0) {
                    updateAlertDetails(alertTable.convertRowIndexToModel(row));
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(alertTable);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        
        alertDetailsArea = new JTextArea();
        alertDetailsArea.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        alertDetailsArea.setForeground(isDarkMode ? Color.red : Color.BLACK);
        alertDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        alertDetailsArea.setEditable(false);
        
        detailsPanel.add(new JScrollPane(alertDetailsArea), BorderLayout.CENTER);
        
        // Create main split pane
        JSplitPane mainSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            tableScroll,
            detailsPanel
        );
        mainSplit.setDividerLocation(350);
        
        return mainSplit;
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        statusLabel = new JLabel("Ready - Monitoring for alerts");
        statusLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
        
        alertCountLabel = new JLabel("Alerts: 0");
        alertCountLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(alertCountLabel, BorderLayout.EAST);
        
        // Update alert count when table changes
        tableModel.addTableModelListener(e -> 
            alertCountLabel.setText("Alerts: " + tableModel.getRowCount())
        );
        
        return statusBar;
    }
    
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        
        // File menu
        JMenu fileMenu = createMenu("File");
        addMenuItem(fileMenu, "Export Alerts...", e -> exportAlerts());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Close", e -> dispose());
        
        // Actions menu
        JMenu actionsMenu = createMenu("Actions");
        addMenuItem(actionsMenu, "Clear All Alerts", e -> clearAlerts());
        
        // Help menu
        JMenu helpMenu = createMenu("Help");
        addMenuItem(helpMenu, "About", e -> showAboutDialog());
        
        menuBar.add(fileMenu);
        menuBar.add(actionsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        return menu;
    }
    
    private void addMenuItem(JMenu menu, String title, ActionListener action) {
        JMenuItem item = new JMenuItem(title);
        item.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        item.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        item.addActionListener(action);
        menu.add(item);
    }
    
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        
        // Update UI colors
        Color bg = isDarkMode ? DARK_BG : LIGHT_BG;
        Color panelBg = isDarkMode ? DARK_PANEL : LIGHT_PANEL;
        Color textColor = isDarkMode ? Color.red : Color.BLACK;
        
        getContentPane().setBackground(bg);
        
        // Update table
        alertTable.setBackground(panelBg);
        alertTable.setForeground(textColor);
        
        // Update text areas
        alertDetailsArea.setBackground(panelBg);
        alertDetailsArea.setForeground(textColor);
        
        // Update status bar
        statusLabel.setForeground(textColor);
        alertCountLabel.setForeground(textColor);
        
        // Force repaint
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private void clearAlerts() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all alerts?",
            "Confirm Clear", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.setRowCount(0);
            alertDetailsArea.setText("");
            statusLabel.setText("All alerts cleared");
        }
    }
    
    private void exportAlerts() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Alerts");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            statusLabel.setText("Exporting alerts to " + fileChooser.getSelectedFile().getName());
            // Implement actual export functionality here
        }
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Network Intrusion Detection System\n" +
            "Alert Monitoring System\n",
            "About Alerts",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateAlertDetails(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
        
        StringBuilder details = new StringBuilder();
        details.append("Alert: ").append(tableModel.getValueAt(row, 0)).append("\n");
        details.append("Time: ").append(tableModel.getValueAt(row, 1)).append("\n");
        details.append("Severity: ").append(tableModel.getValueAt(row, 2)).append("\n\n");
        
        details.append("=== Alert Details ===\n");
        details.append("Source: ").append(tableModel.getValueAt(row, 3)).append("\n");
        details.append("Destination: ").append(tableModel.getValueAt(row, 4)).append("\n");
        details.append("Alert Type: ").append(tableModel.getValueAt(row, 5)).append("\n");
        details.append("Description: ").append(tableModel.getValueAt(row, 6)).append("\n");
        
        alertDetailsArea.setText(details.toString());
    }
    
    // Observer pattern implementation
    @Override
    public void update(ParsedData parsedData) {
        if (parsedData == null) return;
        
        // Only process if this is an alert
        if (!parsedData.getalertflag()) return;
        
        SwingUtilities.invokeLater(() -> {
            // Format the data for display
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            String time = sdf.format(new Date());
            
            // Extract data from ParsedData using the Map
            Map<String, String> data = parsedData.getparsedData();
            
            // Extract fields from the map with fallbacks
            String source = getValueOrDefault(data, "source", "src", "sourceIP", "Unknown");
            String destination = getValueOrDefault(data, "destination", "dst", "destinationIP", "Unknown");
            String severity = getValueOrDefault(data, "severity", "level", "Medium");
            String alertType = getValueOrDefault(data, "alertType", "type", "rule", "Suspicious Activity");
            String description = getValueOrDefault(data, "description", "desc", "message", "Alert detected");
            
            // Get rule ID
            int ruleId = parsedData.getruleid();
            if (ruleId > 0) {
                alertType = "Rule " + ruleId + ": " + alertType;
            }
            
            // Add to table
            Object[] rowData = {
                tableModel.getRowCount() + 1,
                time,
                severity,
                source,
                destination,
                alertType,
                description
            };
            
            tableModel.addRow(rowData);
            
            // Update status
            statusLabel.setText("New alert received: " + alertType);
            
            // Auto-scroll to bottom
            alertTable.scrollRectToVisible(
                alertTable.getCellRect(alertTable.getRowCount() - 1, 0, true)
            );
        });
    }
    
    // Helper method to get a value from the map with multiple possible keys
    private String getValueOrDefault(Map<String, String> map, String... keys) {
        if (map == null) return "Unknown";
        
        for (String key : keys) {
            if (map.containsKey(key) && map.get(key) != null && !map.get(key).isEmpty()) {
                return map.get(key);
            }
        }
        
        // If we have more than one key, the last one is the default value
        if (keys.length > 0 && !keys[keys.length-1].contains("IP") && 
            !keys[keys.length-1].equals("src") && !keys[keys.length-1].equals("dst")) {
            return keys[keys.length-1];
        }
        
        return "Unknown";
    }
}
