package com.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.example.database.ElasticSearch.ElasticsearchManager;
import com.example.engine.EngineIds;
import com.example.util.ParsedData;
import com.example.designpatterns.ObserverPattern.Observer;

public class NetworkAnalyzer extends JFrame implements Observer {
    // UI Components
    private JTable packetTable;
    private DefaultTableModel tableModel;
    private JTextArea detailsArea, hexArea;
    private JButton startBtn, stopBtn;
    private JLabel statusLabel, packetCountLabel;
    private JTextField searchField;
    private boolean isCapturing = false;
    private String currentFilter = "";
    private JFrame parentFrame;
    
    // Add this at the top of the class
    private static final Logger logger = LoggerFactory.getLogger(NetworkAnalyzer.class);
    
    // ElasticsearchManager for searching packets
    private JComboBox<String> modeSelector;
    private ElasticsearchManager elasticsearchManager;
    private int currentUserId = 1; // Default user ID, should be set from login
    
    // Engine instance
    private EngineIds engineInstance;
        
    // Colors for UI
    private final Color DARK_BG = new Color(40, 40, 40);
    private final Color DARK_PANEL = new Color(50, 50, 50);
    private final Color LIGHT_BG = new Color(240, 240, 240);
    private final Color LIGHT_PANEL = new Color(255, 255, 255);
    private final Color ACCENT = new Color(0, 120, 215);
    private boolean isDarkMode = true;
    
    // Constructor with parent frame parameter
    public NetworkAnalyzer(JFrame parent) {
        this.parentFrame = parent;
        setTitle("Network Analyzer");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        
        // Initialize ElasticsearchManager
        elasticsearchManager = new ElasticsearchManager();
        
        // Get the engine instance
        engineInstance = EngineIds.getInstance();
        
        // Register this class as an observer
        engineInstance.addObserver(this);
        logger.info("NetworkAnalyzer initialized and registered as observer");
        
        initUI();
        createMenu();
    }
    
    // Constructor with parent frame and user ID
    public NetworkAnalyzer(JFrame parent, int userId) {
        this(parent);
        this.currentUserId = userId;
    }
    
    // Default constructor for backward compatibility
    public NetworkAnalyzer() {
        this(null);
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
    }
        
    private JToolBar createToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        startBtn = createButton("Start Capture", ACCENT);
        stopBtn = createButton("Stop Capture", new Color(200, 60, 60));
        JButton filterBtn = createButton("Filter", new Color(60, 60, 200));
        JButton settingsBtn = createButton("toggle theme", new Color(100, 100, 100));
        
        stopBtn.setEnabled(false);
        
        startBtn.addActionListener(e -> startCapture());
        stopBtn.addActionListener(e -> stopCapture());
        filterBtn.addActionListener(e -> showFilterDialog());
        settingsBtn.addActionListener(e -> toggleTheme());
        
        // Add mode selector combo box
        String[] modes = {"Normal Mode", "Live Packet Data"};
        modeSelector = new JComboBox<>(modes);
        modeSelector.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        modeSelector.setForeground(isDarkMode ? Color.red : Color.BLACK);
        modeSelector.addActionListener(e -> handleModeChange());
        
        // Add label for the mode selector
        JLabel modeLabel = new JLabel("Capture Mode: ");
        modeLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
        
        toolbar.add(startBtn);
        toolbar.add(Box.createRigidArea(new Dimension(5, 0)));
        toolbar.add(stopBtn);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        
        // Add mode selector and its label
        toolbar.add(modeLabel);
        toolbar.add(modeSelector);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        
        toolbar.add(filterBtn);
        toolbar.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbar.add(settingsBtn);
        
        // Add search field
        toolbar.add(Box.createHorizontalGlue());
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
        
        searchField = new JTextField(15);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter(searchField.getText());
            }
        });
        
        toolbar.add(searchLabel);
        toolbar.add(searchField);
        
        return toolbar;
    }
    
    // Handle mode changes
    private void handleModeChange() {
        String selectedMode = (String) modeSelector.getSelectedItem();
        
        if ("Live Packet Data".equals(selectedMode)) {
            statusLabel.setText("Switched to Live Packet Data mode");
        } else { // Normal Mode
            // Show popup for file selection
            showFileSelectionPopup();
        }
    }
    
    // Show the file selection popup
    private void showFileSelectionPopup() {
        JPopupMenu fileMenu = new JPopupMenu();
        
        // Set background and foreground colors based on theme
        fileMenu.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        
        // Option to upload a file
        JMenuItem uploadItem = new JMenuItem("Upload PCAP File");
        uploadItem.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        uploadItem.addActionListener(e -> selectPcapFile());
        
        // Option to specify a file path
        JMenuItem pathItem = new JMenuItem("Enter File Path");
        pathItem.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        pathItem.addActionListener(e -> enterFilePath());
        
        // Option to cancel
        JMenuItem cancelItem = new JMenuItem("Cancel");
        cancelItem.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        cancelItem.addActionListener(e -> {
            // If user cancels, switch back to Live Packet Data
            modeSelector.setSelectedItem("Live Packet Data");
        });
        
        // Add items to the popup menu
        fileMenu.add(uploadItem);
        fileMenu.add(pathItem);
        fileMenu.addSeparator();
        fileMenu.add(cancelItem);
        
        // Show the popup near the mode selector
        fileMenu.show(modeSelector, 0, modeSelector.getHeight());
    }
    
    // Method to handle file selection via file chooser
    private void selectPcapFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select PCAP File");
        
        // Add file filter for PCAP files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pcap") ||
                        f.getName().toLowerCase().endsWith(".pcapng");
            }
                
            @Override
            public String getDescription() {
                return "PCAP Files (*.pcap, *.pcapng)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processPcapFile(selectedFile.getAbsolutePath());
        } else {
            // If user cancels, switch back to Live Packet Data
            modeSelector.setSelectedItem("Live Packet Data");
        }
    }
    
    // Method to handle file path entry
    private void enterFilePath() {
        String path = JOptionPane.showInputDialog(this, 
            "Enter the full path to the PCAP file:",
            "PCAP File Path", JOptionPane.QUESTION_MESSAGE);
        
        if (path != null && !path.trim().isEmpty()) {
            File file = new File(path.trim());
            if (file.exists() && file.isFile()) {
                processPcapFile(path.trim());
            } else {
                JOptionPane.showMessageDialog(this,
                    "The specified file does not exist or is not a valid file.",
                    "Invalid File Path", JOptionPane.ERROR_MESSAGE);
                // Switch back to Live Packet Data
                modeSelector.setSelectedItem("Live Packet Data");
            }
        } else {
            // If user cancels, switch back to Live Packet Data
            modeSelector.setSelectedItem("Live Packet Data");
        }
    }
    
    // Method to process the selected PCAP file
    private void processPcapFile(String filePath) {
        statusLabel.setText("Processing file: " + filePath);
        
        // Clear existing data
        tableModel.setRowCount(0);
        detailsArea.setText("");
        hexArea.setText("");
        
        // Start the engine with the selected file
        try {
            // Stop any existing capture
            if (isCapturing) {
                stopCapture();
            }
            
            // Start the engine with the file path
            engineInstance.startEngine(filePath);
            
            // Update UI state
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            isCapturing = true;
            statusLabel.setText("Processing PCAP file: " + filePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error processing PCAP file: " + e.getMessage(),
                "File Processing Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error processing file: " + e.getMessage());
        }
    }
    
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        
        // Update UI colors
        Color bg = isDarkMode ? DARK_BG : LIGHT_BG;
        Color panelBg = isDarkMode ? DARK_PANEL : LIGHT_PANEL;
        Color textColor = isDarkMode ? Color.red : Color.BLACK;
        
        getContentPane().setBackground(bg);
        
        // Update table
        packetTable.setBackground(panelBg);
        packetTable.setForeground(textColor);
        
        // Update text areas
        detailsArea.setBackground(panelBg);
        detailsArea.setForeground(textColor);
        hexArea.setBackground(panelBg);
        hexArea.setForeground(textColor);
        
        // Update combo box
        modeSelector.setBackground(panelBg);
        modeSelector.setForeground(textColor);
        
        // Update status bar
        statusLabel.setForeground(textColor);
        packetCountLabel.setForeground(textColor);
        
        // Force repaint
        SwingUtilities.updateComponentTreeUI(this);
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.RED);
        button.setFocusPainted(false);
        return button;
    }
        
    private JSplitPane createMainContent() {
        // Create packet table
        String[] columns = {"No.", "Time", "Source", "Destination", "Protocol", "Length", "Info", "Alert"};
        tableModel = new DefaultTableModel(columns, 0);
        packetTable = new JTable(tableModel);
        packetTable.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        packetTable.setForeground(isDarkMode ? Color.red : Color.BLACK);
        packetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        packetTable.setRowHeight(25);
                
        // Add sorter and selection listener
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        packetTable.setRowSorter(sorter);
                
        packetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = packetTable.getSelectedRow();
                if (row >= 0) {
                    updatePacketDetails(packetTable.convertRowIndexToModel(row));
                }
            }
        });
                
        JScrollPane tableScroll = new JScrollPane(packetTable);
                
        // Create details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
                
        detailsArea = new JTextArea();
        detailsArea.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        detailsArea.setForeground(isDarkMode ? Color.red : Color.BLACK);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailsArea.setEditable(false);
                
        hexArea = new JTextArea();
        hexArea.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        hexArea.setForeground(isDarkMode ? Color.red : Color.BLACK);
        hexArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hexArea.setEditable(false);
                
        JSplitPane detailsSplit = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(detailsArea),
            new JScrollPane(hexArea)
        );
        detailsSplit.setDividerLocation(200);
        detailsPanel.add(detailsSplit, BorderLayout.CENTER);
                
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
                
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
                
        packetCountLabel = new JLabel("Packets: 0");
        packetCountLabel.setForeground(isDarkMode ? Color.red : Color.BLACK);
                
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(packetCountLabel, BorderLayout.EAST);
                
        // Update packet count when table changes
        tableModel.addTableModelListener(e -> 
            packetCountLabel.setText("Packets: " + tableModel.getRowCount())
        );
                
        return statusBar;
    }
        
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
                
        // File menu
        JMenu fileMenu = createMenu("File");
        addMenuItem(fileMenu, "Open Capture...", e -> openCaptureFile());
        addMenuItem(fileMenu, "Save Capture...", e -> saveCaptureFile());
        fileMenu.addSeparator();
        addMenuItem(fileMenu, "Exit", e -> System.exit(0));
                
        // Capture menu
        JMenu captureMenu = createMenu("Capture");
        addMenuItem(captureMenu, "Start", e -> startCapture());
        addMenuItem(captureMenu, "Stop", e -> stopCapture());
        addMenuItem(captureMenu, "Restart", e -> {
            stopCapture(); 
            startCapture();
        });
        captureMenu.addSeparator();
        // Add option to return to main window
        addMenuItem(captureMenu, "Return to Main Window", e -> returnToMainWindow());
                
        // Analyze menu
        JMenu analyzeMenu = createMenu("Analyze");
        addMenuItem(analyzeMenu, "Apply Filter...", e -> showFilterDialog());
        addMenuItem(analyzeMenu, "Statistics", e -> showStatistics());
        addMenuItem(analyzeMenu, "Search in Elasticsearch", e -> showElasticsearchSearchDialog());
                
        // Help menu
        JMenu helpMenu = createMenu("Help");
        addMenuItem(helpMenu, "About", e -> showAboutDialog());
                
        menuBar.add(fileMenu);
        menuBar.add(captureMenu);
        menuBar.add(analyzeMenu);
        menuBar.add(helpMenu);
                
        setJMenuBar(menuBar);
    }
    
    // Method to return to the main window
    private void returnToMainWindow() {
        // Stop capture if it's running
        if (isCapturing) {
            stopCapture();
        }
                
        // Dispose this window
        this.dispose();
                
        // If parent frame exists, make it visible
        if (parentFrame != null) {
            parentFrame.setVisible(true);
        } else {
            // If no parent frame, create a new MainAppWindow
            SwingUtilities.invokeLater(() -> {
                try {
                    MainAppWindow mainWindow = new MainAppWindow();
                    mainWindow.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error returning to main window: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
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
           
    private void startCapture() {
        if (!isCapturing) {
            tableModel.setRowCount(0);
            detailsArea.setText("");
            hexArea.setText("");
                    
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            statusLabel.setText("Capturing packets...");
            isCapturing = true;
                    
            // Check which mode is selected
            String selectedMode = (String) modeSelector.getSelectedItem();
                    
            logger.info("Starting capture in mode: {}", selectedMode);
                    
            if ("Live Packet Data".equals(selectedMode)) {
                // Start the engine in live capture mode
                try {
                    engineInstance.startEngine();
                    statusLabel.setText("Capturing live packet data...");
                    logger.info("Engine started in live capture mode");
                } catch (Exception e) {
                    logger.error("Error starting engine: {}", e.getMessage(), e);
                    JOptionPane.showMessageDialog(this,
                        "Error starting capture: " + e.getMessage(),
                        "Capture Error", JOptionPane.ERROR_MESSAGE);
                    stopCapture();
                }
            } else {
                // For Normal mode, prompt for file selection
                showFileSelectionPopup();
                // The actual capture will be started in processPcapFile method
            }
        }
    }
        
    private void stopCapture() {
        if (isCapturing) {
            // Stop the engine if it's running
            if (EngineIds.isEngineRunning()) {
                engineInstance.stopEngine();
            }
                                
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            statusLabel.setText("Capture stopped");
            isCapturing = false;
        }
    }
    
    // Observer pattern implementation
    // Enhanced update method to better handle packet data
    @Override
    public void update(ParsedData parsedData) {
        // This method is called when new packet data is available from the engine
        if (parsedData == null) {
            logger.error("Received null ParsedData in update method");
            return;
        }
            
        // Log the received data for debugging
        logger.debug("Received packet data: {}", parsedData.getrowData());
            
        SwingUtilities.invokeLater(() -> {
            try {
                addPacketFromParsedData(parsedData);
            } catch (Exception e) {
                logger.error("Error adding packet to table: {}", e.getMessage(), e);
                // Display error in status bar
                statusLabel.setText("Error processing packet: " + e.getMessage());
            }
        });
    }
    
    // Improved method to add a packet from ParsedData with better error handling
    private void addPacketFromParsedData(ParsedData parsedData) {
        if (parsedData == null) {
            logger.warn("Null ParsedData received in addPacketFromParsedData");
            return;
        }
            
        // Format the current time for display
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String time = sdf.format(new Date());
            
        // Get the parsed data map
        Map<String, String> dataMap = parsedData.getparsedData();
        if (dataMap == null) {
            logger.warn("Null parsed data map in packet");
            // Still try to display something using the raw data
            String rawData = parsedData.getrowData();
            if (rawData != null) {
                Object[] rowData = {
                    tableModel.getRowCount() + 1,
                    time,
                    "Unknown",
                    "Unknown",
                    "Unknown",
                    rawData.length(),
                    "Raw data available",
                    ""
                };
                tableModel.addRow(rowData);
            }
            return;
        }
            
        // Log the parsed data map for debugging
        logger.debug("Parsed data map: {}", dataMap);
            
        // Extract packet information from the map using the keys from PacketParser
        String srcIp = getValueOrDefault(dataMap, "srcIp", "Unknown");
        String dstIp = getValueOrDefault(dataMap, "dstIp", "Unknown");
        String protocol = getValueOrDefault(dataMap, "protocol", "Unknown");
        String srcPort = getValueOrDefault(dataMap, "srcPort", "");
        String dstPort = getValueOrDefault(dataMap, "dstPort", "");
        String srcMac = getValueOrDefault(dataMap, "srcMac", "");
        String dstMac = getValueOrDefault(dataMap, "dstMac", "");
        String data = getValueOrDefault(dataMap, "data", "");
            
        // Format source and destination with port if available
        String source = srcIp;
        if (!srcPort.isEmpty()) {
            source += ":" + srcPort;
        }
        if (!srcMac.isEmpty()) {
            source += " (" + srcMac + ")";
        }
            
        String destination = dstIp;
        if (!dstPort.isEmpty()) {
            destination += ":" + dstPort;
        }
        if (!dstMac.isEmpty()) {
            destination += " (" + dstMac + ")";
        }
            
        // Create info field
        String info = protocol + " packet";
        if (!data.isEmpty()) {
            info = data.length() > 50 ? data.substring(0, 50) + "..." : data;
        }
            
        // Get packet length
        int packetLength = 0;
        String rawData = parsedData.getrowData();
        if (rawData != null) {
            packetLength = rawData.length();
        }
            
        // Check if packet matches filter
        if (!currentFilter.isEmpty()) {
            boolean matches = protocol.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            source.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            destination.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            info.toLowerCase().contains(currentFilter.toLowerCase());
            if (!matches) {
                logger.debug("Packet filtered out by current filter: {}", currentFilter);
                return;
            }
        }
            
        // Check if this packet triggered an alert
        boolean isAlert = parsedData.getalertflag();
        Integer ruleId = parsedData.getruleid();
        String alertStatus = isAlert ? "ALERT (Rule " + ruleId + ")" : "";
            
        // Add to table
        Object[] rowData = {
            tableModel.getRowCount() + 1,
            time,
            source,
            destination,
            protocol,
            packetLength,
            info,
            alertStatus
        };
            
        logger.debug("Adding row to table: {}", java.util.Arrays.toString(rowData));
        tableModel.addRow(rowData);
            
        // If this is an alert, highlight the row
        if (isAlert) {
            int lastRow = tableModel.getRowCount() - 1;
            // We would need to implement row coloring here
            // This is typically done with a custom TableCellRenderer
            logger.info("Alert detected for packet at row {}", lastRow);
        }
            
        // Auto-scroll to bottom
        packetTable.scrollRectToVisible(
            packetTable.getCellRect(packetTable.getRowCount() - 1, 0, true)
        );
    }
        
    // Helper method to get a value from the map with fallback
    private String getValueOrDefault(Map<String, String> map, String key, String defaultValue) {
        if (map == null) return defaultValue;
        String value = map.get(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
        
    private void updatePacketDetails(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) return;
                    
        StringBuilder details = new StringBuilder();
        details.append("Packet: ").append(tableModel.getValueAt(row, 0)).append("\n");
        details.append("Time: ").append(tableModel.getValueAt(row, 1)).append("\n\n");
                            
        String protocol = (String) tableModel.getValueAt(row, 4);
        details.append("=== ").append(protocol).append(" Header ===\n");
                            
        details.append("Source: ").append(tableModel.getValueAt(row, 2)).append("\n");
        details.append("Destination: ").append(tableModel.getValueAt(row, 3)).append("\n");
        details.append("Protocol: ").append(protocol).append("\n");
        details.append("Length: ").append(tableModel.getValueAt(row, 5)).append(" bytes\n");
        details.append("Info: ").append(tableModel.getValueAt(row, 6)).append("\n");
            
        // Add alert information if present
        String alertStatus = (String) tableModel.getValueAt(row, 7);
        if (alertStatus != null && !alertStatus.isEmpty()) {
            details.append("\n=== Alert Information ===\n");
            details.append(alertStatus).append("\n");
        }
            
        detailsArea.setText(details.toString());
                    
        // For hex view, we would need the actual packet bytes
        // Since we don't have direct access to raw bytes, display the raw data if available
        hexArea.setText("Hex view not available for this packet.");
    }
        
    // Regular search filter for the table
    private void applySearchFilter(String text) {
        TableRowSorter<DefaultTableModel> sorter = 
            (TableRowSorter<DefaultTableModel>) packetTable.getRowSorter();
                            
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
        
    // New method to search in Elasticsearch
    private void searchElasticsearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
                            
        statusLabel.setText("Searching in Elasticsearch...");
                            
        // Use SwingWorker to perform search in background
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return elasticsearchManager.searchUserPackets(currentUserId, keyword);
            }
                                            
            @Override
            protected void done() {
                try {
                    List<String> results = get();
                    displayElasticsearchResults(results, keyword);
                    statusLabel.setText("Search complete. Found " + results.size() + " results.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(NetworkAnalyzer.this,
                        "Error searching Elasticsearch: " + e.getMessage(),
                        "Search Error", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Search failed");
                }
            }
        }.execute();
    }
        
    // Display Elasticsearch search results
    private void displayElasticsearchResults(List<String> results, String keyword) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No results found for: " + keyword,
                "Search Results", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
                            
        // Clear the current table
        tableModel.setRowCount(0);
                            
        // Parse and add results to the table
        int rowCount = 0;
        for (String rawPacket : results) {
            try {
                // Parse the raw packet string
                // Format: "Time: %s, Source: %s, Destination: %s, Protocol: %s, Length: %d, Info: %s"
                String time = extractValue(rawPacket, "Time:");
                String source = extractValue(rawPacket, "Source:");
                String destination = extractValue(rawPacket, "Destination:");
                String protocol = extractValue(rawPacket, "Protocol:");
                String lengthStr = extractValue(rawPacket, "Length:");
                String info = extractValue(rawPacket, "Info:");
                                                                    
                int length = 0;
                try {
                    length = Integer.parseInt(lengthStr);
                } catch (NumberFormatException e) {
                    // Use default if parsing fails
                    length = 64;
                }
                                                                    
                Object[] rowData = {
                    ++rowCount,
                    time,
                    source,
                    destination,
                    protocol,
                    length,
                    info,
                    "" // No alert information for search results
                };
                                                                    
                tableModel.addRow(rowData);
            } catch (Exception e) {
                System.err.println("Error parsing packet: " + e.getMessage());
                // Add raw data as fallback
                Object[] rowData = {
                    ++rowCount,
                    new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()),
                    "Unknown",
                    "Unknown",
                    "Unknown",
                    rawPacket.length(),
                    rawPacket,
                    ""
                };
                tableModel.addRow(rowData);
            }
        }
    }
        
    // Helper method to extract values from the raw packet string
    private String extractValue(String rawPacket, String key) {
        int startIndex = rawPacket.indexOf(key) + key.length();
        int endIndex = rawPacket.indexOf(",", startIndex);
        if (endIndex == -1) {
            // This might be the last field
            return rawPacket.substring(startIndex).trim();
        }
        return rawPacket.substring(startIndex, endIndex).trim();
    }
        
    // Show dialog for Elasticsearch search
    private void showElasticsearchSearchDialog() {
                    
        String keyword = JOptionPane.showInputDialog(this,
            "Enter search term for Elasticsearch:",
            "Elasticsearch Search", JOptionPane.QUESTION_MESSAGE);
                            
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchElasticsearch(keyword);
        }
    }
            
    // Method to open a capture file
    private void openCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Capture File");
            
        // Add file filter for PCAP files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pcap") ||
                        f.getName().toLowerCase().endsWith(".pcapng");
            }
                
            @Override
            public String getDescription() {
                return "PCAP Files (*.pcap, *.pcapng)";
            }
        });
            
        int result = fileChooser.showOpenDialog(this);
            
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processPcapFile(selectedFile.getAbsolutePath());
        }
    }
    
    // Method to save the current capture to a file
    private void saveCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Capture File");
            
        // Add file filter for PCAP files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".pcap");
            }
                
            @Override
            public String getDescription() {
                return "PCAP Files (*.pcap)";
            }
        });
            
        int result = fileChooser.showSaveDialog(this);
            
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            
            // Add .pcap extension if not present
            if (!filePath.toLowerCase().endsWith(".pcap")) {
                filePath += ".pcap";
            }
            
            // Here you would implement the actual saving logic
            // This is a placeholder since we don't have direct access to raw packet data
            JOptionPane.showMessageDialog(this,
                "Saving capture to file is not implemented in this version.",
                "Feature Not Available", JOptionPane.INFORMATION_MESSAGE);
            
            statusLabel.setText("Capture saved to: " + filePath);
        }
    }
    
    // Method to show network statistics
    private void showStatistics() {
        // Count packets by protocol
        int tcpCount = 0;
        int udpCount = 0;
        int icmpCount = 0;
        int otherCount = 0;
        int totalPackets = tableModel.getRowCount();
            
        for (int i = 0; i < totalPackets; i++) {
            String protocol = (String) tableModel.getValueAt(i, 4);
            if (protocol.contains("TCP")) {
                tcpCount++;
            } else if (protocol.contains("UDP")) {
                udpCount++;
            } else if (protocol.contains("ICMP")) {
                icmpCount++;
            } else {
                otherCount++;
            }
        }
            
        // Create statistics dialog
        JDialog statsDialog = new JDialog(this, "Network Statistics", true);
        statsDialog.setSize(400, 300);
        statsDialog.setLocationRelativeTo(this);
            
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
            
        // Create text area for statistics
        JTextArea statsArea = new JTextArea();
        statsArea.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        statsArea.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            
        // Build statistics text
        StringBuilder stats = new StringBuilder();
        stats.append("=== Packet Statistics ===\n\n");
        stats.append("Total Packets: ").append(totalPackets).append("\n\n");
        stats.append("By Protocol:\n");
        stats.append("  TCP:   ").append(tcpCount).append(" (").append(formatPercentage(tcpCount, totalPackets)).append(")\n");
        stats.append("  UDP:   ").append(udpCount).append(" (").append(formatPercentage(udpCount, totalPackets)).append(")\n");
        stats.append("  ICMP:  ").append(icmpCount).append(" (").append(formatPercentage(icmpCount, totalPackets)).append(")\n");
        stats.append("  Other: ").append(otherCount).append(" (").append(formatPercentage(otherCount, totalPackets)).append(")\n");
            
        statsArea.setText(stats.toString());
            
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
            
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> statsDialog.dispose());
            
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        buttonPanel.add(closeButton);
            
        panel.add(buttonPanel, BorderLayout.SOUTH);
            
        statsDialog.setContentPane(panel);
        statsDialog.setVisible(true);
    }
    
    // Helper method to format percentage
    private String formatPercentage(int count, int total) {
        if (total == 0) return "0%";
        return String.format("%.1f%%", (count * 100.0) / total);
    }
    
    // Method to show about dialog
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "About Network Analyzer", true);
        aboutDialog.setSize(400, 300);
        aboutDialog.setLocationRelativeTo(this);
            
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
            
        // Create text area for about information
        JTextArea aboutArea = new JTextArea();
        aboutArea.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
        aboutArea.setForeground(isDarkMode ? Color.RED : Color.BLACK);
        aboutArea.setEditable(false);
        aboutArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
        // Build about text
        StringBuilder about = new StringBuilder();
        about.append("Network Analyzer\n");
        about.append("Version 1.0\n\n");
        about.append("A network packet analyzer for monitoring and analyzing network traffic.\n\n");
        about.append("Features:\n");
        about.append("- Live packet capture\n");
        about.append("- PCAP file analysis\n");
        about.append("- Protocol filtering\n");
        about.append("- Packet inspection\n");
        about.append("- Alert detection\n\n");
        about.append("© 2023 Network Security Team\n");
            
        aboutArea.setText(about.toString());
            
        panel.add(new JScrollPane(aboutArea), BorderLayout.CENTER);
            
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> aboutDialog.dispose());
            
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(isDarkMode ? DARK_BG : LIGHT_BG);
        buttonPanel.add(closeButton);
            
        panel.add(buttonPanel, BorderLayout.SOUTH);
            
        aboutDialog.setContentPane(panel);
        aboutDialog.setVisible(true);
    }
    
    // Method to show filter dialog
    private void showFilterDialog() {
        String filter = JOptionPane.showInputDialog(this, 
            "Enter filter expression:", currentFilter);
                            
        if (filter != null) {
            currentFilter = filter.trim();
            statusLabel.setText(currentFilter.isEmpty() ? 
                "Filter cleared" : "Filter applied: " + currentFilter);
                                                    
            // Apply to existing packets if not capturing
            if (!isCapturing && !currentFilter.isEmpty()) {
                TableRowSorter<DefaultTableModel> sorter = 
                    (TableRowSorter<DefaultTableModel>) packetTable.getRowSorter();
                                                                                    
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                        String lowerFilter = currentFilter.toLowerCase();
                                                                                                                            
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            if (entry.getStringValue(i).toLowerCase().contains(lowerFilter)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }
}
