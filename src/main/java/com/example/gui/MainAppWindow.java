package com.example.gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.border.*;

public class MainAppWindow extends JFrame {
    private JTable packetTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton, filterButton, rulesButton, themeToggleButton;
    private JTextField searchField;
    private boolean isCapturing = false;
    private boolean isDarkTheme = true;
    private JLabel statusLabel, packetCountLabel;
    private RowFilter<DefaultTableModel, Object> currentFilter;
        
    // Color schemes - Updated with black background and red buttons
    private final Color DARK_BG = Color.BLACK;
    private final Color DARK_PANEL = new Color(20, 20, 20);
    private final Color DARK_TABLE_BG = new Color(30, 30, 30);
    private final Color DARK_GRID = new Color(50, 50, 50);
    private final Color BUTTON_COLOR = Color.RED;
    private final Color LIGHT_BG = new Color(240, 240, 240);
    private final Color LIGHT_PANEL = new Color(220, 220, 220);
    private final Color LIGHT_TABLE_BG = new Color(255, 255, 255);
    private final Color LIGHT_GRID = new Color(200, 200, 200);
        
    public MainAppWindow() {
        setTitle("Kaomi Intrusion Detection");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
                
        initializeUI();
    }
        
    private void initializeUI() {
        applyTheme();
        setLayout(new BorderLayout());
        add(createToolbar(), BorderLayout.NORTH);
        add(createPacketTablePanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());
        setupSearch();
    }
        
    private void applyTheme() {
        getContentPane().setBackground(isDarkTheme ? DARK_BG : LIGHT_BG);
        UIManager.put("Panel.background", isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        UIManager.put("MenuBar.background", isDarkTheme ? DARK_BG : Color.red);
        UIManager.put("Menu.background", isDarkTheme ? DARK_BG : Color.red);
        UIManager.put("Menu.foreground", isDarkTheme ? Color.black : Color.BLACK);
        UIManager.put("MenuItem.background", isDarkTheme ? DARK_PANEL : Color.red);
        UIManager.put("MenuItem.foreground", isDarkTheme ? Color.red : Color.BLACK);
        UIManager.put("PopupMenu.background", isDarkTheme ? DARK_PANEL : Color.red);
        UIManager.put("OptionPane.background", isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        UIManager.put("OptionPane.messageForeground", isDarkTheme ? Color.red : Color.BLACK);
    }
        
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
        // Updated button colors to red
        startButton = createButton("Start Capture", BUTTON_COLOR, e -> startCapture());
        stopButton = createButton("Stop Capture", BUTTON_COLOR, e -> stopCapture());
        filterButton = createButton("Filter", BUTTON_COLOR, e -> showFilterDialog());
        rulesButton = createButton("Rules", BUTTON_COLOR, e -> showRulesDialog());
        themeToggleButton = createButton("Toggle Theme", BUTTON_COLOR, e -> toggleTheme());
                
        stopButton.setEnabled(false);
                
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(isDarkTheme ? Color.red : Color.BLACK);
                
        toolbar.add(startButton);
        toolbar.add(stopButton);
        toolbar.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbar.add(filterButton);
        toolbar.add(rulesButton);
        toolbar.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbar.add(searchLabel);
        toolbar.add(searchField);
        toolbar.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbar.add(themeToggleButton);
                
        return toolbar;
    }
        
    private JButton createButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.red);
        button.addActionListener(listener);
        return button;
    }
        
    private JScrollPane createPacketTablePanel() {
        String[] columns = {"Time", "Source", "Destination", "Protocol", "Length", "Info"};
        tableModel = new DefaultTableModel(columns, 0);
                
        packetTable = new JTable(tableModel);
        packetTable.setBackground(isDarkTheme ? DARK_TABLE_BG : LIGHT_TABLE_BG);
        packetTable.setForeground(isDarkTheme ? Color.red : Color.BLACK);
        packetTable.setGridColor(isDarkTheme ? DARK_GRID : LIGHT_GRID);
        packetTable.getTableHeader().setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        packetTable.getTableHeader().setForeground(isDarkTheme ? Color.red : Color.BLACK);
        packetTable.setRowHeight(25);
        packetTable.setAutoCreateRowSorter(true);
                
        return new JScrollPane(packetTable);
    }
        
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(isDarkTheme ? DARK_PANEL : LIGHT_PANEL);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(isDarkTheme ? Color.red : Color.BLACK);
                
        packetCountLabel = new JLabel("Packets: 0");
        packetCountLabel.setForeground(isDarkTheme ? Color.red : Color.BLACK);
                
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(packetCountLabel, BorderLayout.EAST);
                
        return statusBar;
    }
        
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(DARK_BG);
                
        // File menu
        JMenu fileMenu = createMenu("File", 
            new String[]{"Open Capture File", "Save Capture", "Export Data", "Exit"},
            new ActionListener[]{
                e -> importCaptureFile(), 
                e -> saveCaptureFile(), 
                e -> exportData(), 
                e -> System.exit(0)
            });
                
        // Edit menu - Removed Copy and Find, kept Preferences
        JMenu editMenu = createMenu("Edit", 
            new String[]{"Preferences"},
            new ActionListener[]{
                e -> showSettingsDialog()
            });
                
        // Tools menu - Updated Analyze Traffic to open NetworkAnalyzer
        JMenu toolsMenu = createMenu("Tools", 
            new String[]{"Analyze Network", "Network Scan", "Data Management"},
            new ActionListener[]{
                e -> showNetworkAnalyzer(), 
                e -> JOptionPane.showMessageDialog(this, "Network Scanner"),
                e -> JOptionPane.showMessageDialog(this, "Data Management Tool")
            });
                
        // Help menu - Removed Help Contents, kept About
        JMenu helpMenu = createMenu("Help", 
            new String[]{"About"},
            new ActionListener[]{
                e -> showAboutDialog()
            });
                
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
                
        return menuBar;
    }
        
    private JMenu createMenu(String title, String[] items, ActionListener[] listeners) {
        JMenu menu = new JMenu(title);
        menu.setForeground(Color.red);
        menu.setBackground(DARK_BG);
                
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem(items[i]);
                menuItem.setBackground(DARK_BG);
                menuItem.setForeground(Color.black);
                menuItem.addActionListener(listeners[i]);
                menu.add(menuItem);
            }
        }
                
        return menu;
    }
        
    private void setupSearch() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
        });
    }
        
    private void applySearchFilter() {
        String searchText = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) packetTable.getRowSorter();
                
        if (searchText.isEmpty()) {
            sorter.setRowFilter(currentFilter);
        } else {
            RowFilter<DefaultTableModel, Object> searchFilter = RowFilter.regexFilter("(?i)" + searchText);
                        
            if (currentFilter != null) {
                List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
                filters.add(currentFilter);
                filters.add(searchFilter);
                sorter.setRowFilter(RowFilter.andFilter(filters));
            } else {
                sorter.setRowFilter(searchFilter);
            }
        }
        updatePacketCount();
    }
        
    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        refreshUI();
    }
        
    private void refreshUI() {
        // Store data
        int rowCount = tableModel.getRowCount();
        Object[][] data = new Object[rowCount][6];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < 6; j++) {
                data[i][j] = tableModel.getValueAt(i, j);
            }
        }
                
        // Recreate UI
        getContentPane().removeAll();
        tableModel.setRowCount(0);
        applyTheme();
                
        add(createToolbar(), BorderLayout.NORTH);
        add(createPacketTablePanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());
                
        // Restore data
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
                
        // Setup sorter and search
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        packetTable.setRowSorter(sorter);
        if (currentFilter != null) {
            sorter.setRowFilter(currentFilter);
        }
        setupSearch();
                
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
        revalidate();
        updatePacketCount();
    }
        
    private void startCapture() {
        isCapturing = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        statusLabel.setText("Capturing packets...");
        addSampleData();
    }
        
    private void stopCapture() {
        isCapturing = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        statusLabel.setText("Capture stopped");
    }
        
    private void addSampleData() {
        Object[][] sampleData = {
            {"10:45:23.123", "192.168.1.5", "8.8.8.8", "DNS", "64", "Standard query 0x1234 A www.example.com"},
            {"10:45:23.234", "8.8.8.8", "192.168.1.5", "DNS", "80", "Standard query response"},
            {"10:45:23.345", "192.168.1.5", "93.184.216.34", "TCP", "74", "52431 → 80 [SYN]"},
            {"10:45:24.112", "192.168.1.5", "10.0.0.1", "ICMP", "98", "Echo (ping) request"},
            {"10:45:24.223", "10.0.0.1", "192.168.1.5", "ICMP", "98", "Echo (ping) reply"}
        };
                
        for (Object[] row : sampleData) {
            tableModel.addRow(row);
        }
        updatePacketCount();
    }
        
    private void updatePacketCount() {
        int visibleRows = packetTable.getRowSorter().getViewRowCount();
        int totalRows = tableModel.getRowCount();
        packetCountLabel.setText("Packets: " + visibleRows + " (of " + totalRows + ")");
    }
        
    private void showFilterDialog() {
        // Create a simple dialog if FilterDialog class is not available
        try {
            FilterDialog dialog = new FilterDialog(this);
            dialog.setVisible(true);
            if (dialog.getFilter() != null) {
                currentFilter = dialog.getFilter();
                ((TableRowSorter<DefaultTableModel>)packetTable.getRowSorter()).setRowFilter(currentFilter);
                updatePacketCount();
            }
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(this, "Filter Dialog would appear here");
        }
    }
        
    // Method to show the NetworkAnalyzer
    private void showNetworkAnalyzer() {
        try {
            NetworkAnalyzer analyzer = new NetworkAnalyzer();
            analyzer.setVisible(true);
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            // Fallback if the constructor with JFrame parameter doesn't exist
            try {
                NetworkAnalyzer analyzer = new NetworkAnalyzer();
                analyzer.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Could not open Network Analyzer: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
    // Dialog method stubs with fallback implementations
        private void showRulesDialog() {
        try {
            new RulesDialog(this).setVisible(true);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(this, "Rules Dialog would appear here");
        }
    }
        
    private void showSettingsDialog() {
        try {
            new SettingsDialog(this).setVisible(true);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(this, "Settings Dialog would appear here");
        }
    }
        
    private void showAboutDialog() {
        try {
            new AboutDialog(this).setVisible(true);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(this,
                "About Kaomi Intrusion Detection\n\n" +
                "Version: 1.0\n" +
                "© 2023 Kaomi Security\n\n" +
                "A network monitoring and intrusion detection system.",
                "About", JOptionPane.INFORMATION_MESSAGE);
        }
    }
        
    // File operations (simplified)
    private void importCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                tableModel.setRowCount(0);
                BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) tableModel.addRow(parts);
                }
                reader.close();
                updatePacketCount();
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            }
        }
    }
        
    private void saveCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".pcap")) {
                    file = new File(file.getPath() + ".pcap");
                }
                                
                FileWriter writer = new FileWriter(file);
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder line = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        line.append(tableModel.getValueAt(i, j));
                        if (j < tableModel.getColumnCount() - 1) line.append(",");
                    }
                    writer.write(line.toString() + "\n");
                }
                writer.close();
                statusLabel.setText("Saved: " + file.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }
        
    private void exportData() {
        String[] formats = {"CSV", "XML", "JSON", "Text"};
        String format = (String) JOptionPane.showInputDialog(this, "Select format:",
            "Export Data", JOptionPane.QUESTION_MESSAGE, null, formats, formats[0]);
                
        if (format != null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChooser.getSelectedFile();
                    String ext = "." + format.toLowerCase();
                    if (!file.getName().toLowerCase().endsWith(ext)) {
                        file = new File(file.getPath() + ext);
                    }
                                        
                    switch (format) {
                        case "CSV": exportAsCSV(file); break;
                        case "XML": exportAsXML(file); break;
                        case "JSON": exportAsJSON(file); break;
                        case "Text": exportAsText(file); break;
                    }
                    statusLabel.setText("Exported as " + format);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Export error: " + e.getMessage());
                }
            }
        }
    }
        
    // Export methods (simplified)
    private void exportAsCSV(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        // Write header
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            writer.write(tableModel.getColumnName(i) + (i < tableModel.getColumnCount()-1 ? "," : "\n"));
        }
        // Write data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                writer.write(tableModel.getValueAt(i, j).toString() +
                    (j < tableModel.getColumnCount()-1 ? "," : "\n"));
            }
        }
        writer.close();
    }
        
    private void exportAsXML(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("<?xml version=\"1.0\"?>\n<packets>\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            writer.write("  <packet>\n");
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                String col = tableModel.getColumnName(j).toLowerCase().replace(" ", "_");
                String val = tableModel.getValueAt(i, j).toString()
                        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                writer.write("    <" + col + ">" + val + "</" + col + ">\n");
            }
            writer.write("  </packet>\n");
        }
        writer.write("</packets>");
        writer.close();
    }
        
    private void exportAsJSON(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write("{\n  \"packets\": [\n");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            writer.write("    {");
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                String col = tableModel.getColumnName(j).toLowerCase().replace(" ", "_");
                String val = tableModel.getValueAt(i, j).toString()
                        .replace("\\", "\\\\").replace("\"", "\\\"");
                writer.write("\"" + col + "\":\"" + val + "\"" +
                    (j < tableModel.getColumnCount()-1 ? "," : ""));
            }
            writer.write("}" + (i < tableModel.getRowCount()-1 ? ",\n" : "\n"));
        }
        writer.write("  ]\n}");
        writer.close();
    }
        
    private void exportAsText(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        // Calculate column widths
        int[] widths = new int[tableModel.getColumnCount()];
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            widths[i] = tableModel.getColumnName(i).length();
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                widths[i] = Math.max(widths[i], tableModel.getValueAt(j, i).toString().length());
            }
        }
                
        // Write header
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            writer.write(padRight(tableModel.getColumnName(i), widths[i] + 2));
        }
        writer.write("\n");
                
        // Write separator
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            for (int j = 0; j < widths[i] + 2; j++) writer.write("-");
        }
        writer.write("\n");
                
        // Write data
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                writer.write(padRight(tableModel.getValueAt(i, j).toString(), widths[j] + 2));
            }
            writer.write("\n");
        }
        writer.close();
    }
        
    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
        
    // Getters and utility methods
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getPacketTable() { return packetTable; }
        
    public void applyFilter(RowFilter<DefaultTableModel, Object> filter) {
        currentFilter = filter;
        ((TableRowSorter<DefaultTableModel>)packetTable.getRowSorter()).setRowFilter(filter);
        updatePacketCount();
    }
        
    public static void main(String[] args) {
        try {
            // Set the look and feel to the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
                
        SwingUtilities.invokeLater(() -> {
            MainAppWindow app = new MainAppWindow();
            app.setVisible(true);
        });
    }
}

