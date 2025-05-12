package com.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private Timer captureTimer;
    private boolean isCapturing = false;
    private Random random = new Random();
    private String currentFilter = "";
    private JFrame parentFrame;
    
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
        
        initUI();
        createMenu();
        
        // Setup capture timer for live mode
        captureTimer = new Timer(1000, e -> addSamplePacket());
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
            // Code for Live Packet Data mode
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
        String[] columns = {"No.", "Time", "Source", "Destination", "Protocol", "Length", "Info"};
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
        addMenuItem(captureMenu, "Restart", e -> { stopCapture(); startCapture(); });
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
            
            if ("Live Packet Data".equals(selectedMode)) {
                // Use the timer for sample data in Live mode
                captureTimer.start();
            } else {
                // For Normal mode, prompt for file selection
                showFileSelectionPopup();
            }
        }
    }
        
    private void stopCapture() {
        if (isCapturing) {
            // Stop the timer if it's running
            captureTimer.stop();
            
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
    @Override
    public void update(ParsedData parsedData) {
        // This method is called when new packet data is available from the engine
        SwingUtilities.invokeLater(() -> {
            addPacketFromParsedData(parsedData);
        });
    }
    
    // Method to add a packet from ParsedData
        // Method to add a packet from ParsedData
    private void addPacketFromParsedData(ParsedData parsedData) {
        if (parsedData == null) {
            return;
        }
        
        // Extract data from ParsedData based on its actual structure
        // Assuming ParsedData has methods to access packet information
        String protocol = parsedData.getProtocol();
        String srcIp = parsedData.getSourceIp();
        String srcPort = parsedData.getSourcePort();
        String dstIp = parsedData.getDestinationIp();
        String dstPort = parsedData.getDestinationPort();
        String data = parsedData.getData();
        
        // Format the data for display
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String time = sdf.format(new Date());
        
        String source = srcIp + ":" + srcPort;
        String destination = dstIp + ":" + dstPort;
        
        // Generate info based on protocol
        String info = generateInfoFromProtocol(protocol, srcPort, dstPort, data);
        
        // Calculate or estimate length
        int length = estimatePacketLength(protocol, data);
        
        // Check if packet matches filter
        if (!currentFilter.isEmpty()) {
            boolean matches = protocol.toLowerCase().contains(currentFilter.toLowerCase()) ||
                             source.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            destination.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            info.toLowerCase().contains(currentFilter.toLowerCase());
            if (!matches) return;
        }
        
        // Add to table
        Object[] rowData = {
            tableModel.getRowCount() + 1,
            time,
            source,
            destination,
            protocol,
            length,
            info
        };
        
        tableModel.addRow(rowData);
        
        // Store packet in Elasticsearch
        String rawContent = String.format(
            "Time: %s, Source: %s, Destination: %s, Protocol: %s, Length: %d, Info: %s",
            time, source, destination, protocol, length, info
        );
        
        // Index the packet asynchronously to avoid UI freezes
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return elasticsearchManager.indexUserPacket(currentUserId, rawContent);
            }
        }.execute();
        
        // Auto-scroll to bottom
        packetTable.scrollRectToVisible(
            packetTable.getCellRect(packetTable.getRowCount() - 1, 0, true)
        );
    }
    
    // Helper method to generate info from protocol data
    private String generateInfoFromProtocol(String protocol, String srcPort, String dstPort, String data) {
        if (protocol == null) {
            return "Unknown protocol";
        }
        
        switch (protocol) {
            case "TCP":
                return "Port " + srcPort + " → " + dstPort;
            case "UDP":
                return "Port " + srcPort + " → " + dstPort;
            case "HTTP":
                if (data != null && !data.isEmpty()) {
                    // Try to extract HTTP method or response code
                    if (data.startsWith("GET") || data.startsWith("POST") || 
                        data.startsWith("PUT") || data.startsWith("DELETE")) {
                        // Extract first line of request
                        int endOfLine = data.indexOf('\n');
                        if (endOfLine > 0) {
                            return data.substring(0, endOfLine).trim();
                        }
                        return data.length() > 50 ? data.substring(0, 50) + "..." : data;
                    } else if (data.startsWith("HTTP/")) {
                        // Extract status code
                        int endOfLine = data.indexOf('\n');
                        if (endOfLine > 0) {
                            return data.substring(0, endOfLine).trim();
                        }
                    }
                }
                return "HTTP " + srcPort + " → " + dstPort;
            default:
                return protocol + " " + srcPort + " → " + dstPort;
        }
    }
    
    // Helper method to estimate packet length
    private int estimatePacketLength(String protocol, String data) {
        // If we have actual data, use its length
        if (data != null && !data.isEmpty()) {
            return data.length();
        }
        
        // Otherwise estimate based on protocol
        if (protocol == null) {
            return 128; // Default size
        }
        
        switch (protocol) {
            case "TCP":
                return 64; // Typical TCP header + some data
            case "UDP":
                return 32; // Typical UDP packet
            case "HTTP":
                return 512; // Typical HTTP packet
            default:
                return 128; // Default size
        }
    }

        
    private void addSamplePacket() {
        // Generate sample packet data
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        String time = sdf.format(new Date());
                
        String[] protocols = {"TCP", "UDP", "HTTP", "DNS", "ICMP", "ARP", "HTTPS"};
        String protocol = protocols[random.nextInt(protocols.length)];
                
        String source = "192.168." + random.nextInt(256) + "." + random.nextInt(256);
        String destination = "10.0." + random.nextInt(256) + "." + random.nextInt(256);
        int length = 64 + random.nextInt(1400);
        String info = generatePacketInfo(protocol);
                
        // Check if packet matches filter
        if (!currentFilter.isEmpty()) {
            boolean matches = protocol.toLowerCase().contains(currentFilter.toLowerCase()) ||
                             source.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            destination.toLowerCase().contains(currentFilter.toLowerCase()) ||
                            info.toLowerCase().contains(currentFilter.toLowerCase());
            if (!matches) return;
        }
                
        // Add to table
        Object[] rowData = {
            tableModel.getRowCount() + 1,
            time,
            source,
            destination,
            protocol,
            length,
            info
        };
                
        tableModel.addRow(rowData);
        
        // Store packet in Elasticsearch
        String rawContent = String.format(
            "Time: %s, Source: %s, Destination: %s, Protocol: %s, Length: %d, Info: %s",
            time, source, destination, protocol, length, info
        );
        
        // Index the packet asynchronously to avoid UI freezes
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return elasticsearchManager.indexUserPacket(currentUserId, rawContent);
            }
        }.execute();
                
        // Auto-scroll to bottom
        packetTable.scrollRectToVisible(
            packetTable.getCellRect(packetTable.getRowCount() - 1, 0, true)
        );
    }
        
    private String generatePacketInfo(String protocol) {
        switch (protocol) {
            case "TCP":
                return "Port " + (1024 + random.nextInt(60000)) + " → " +
                        (1024 + random.nextInt(60000)) + " [" +
                        (random.nextBoolean() ? "SYN" : random.nextBoolean() ? "ACK" : "FIN") + "]";
            case "UDP":
                return "Port " + (1024 + random.nextInt(60000)) + " → " +
                        (1024 + random.nextInt(60000)) + " Len=" + (random.nextInt(1000) + 8);
            case "HTTP":
                return random.nextBoolean() ?
                        "GET /" + (random.nextBoolean() ? "" : "index.html") + " HTTP/1.1" :
                        "HTTP/1.1 " + (random.nextBoolean() ? "200 OK" : "404 Not Found");
            case "DNS":
                return "Standard query " + (random.nextBoolean() ? "response " : "") +
                        "0x" + Integer.toHexString(random.nextInt(65535)) + " A " +
                        generateDomainName();
            case "ICMP":
                return "Echo " + (random.nextBoolean() ? "request" : "reply") +
                        " id=0x" + Integer.
                toHexString(random.nextInt(65535));
            case "ARP":
                return random.nextBoolean() ?
                        "Who has " + generateIPAddress() + "? Tell " + generateIPAddress() :
                        generateIPAddress() + " is at " + generateMacAddress();
            case "HTTPS":
                return "TLSv1.2 " + (random.nextBoolean() ? "Client Hello" : "Server Hello");
            default:
                return "Unknown protocol data";
        }
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
        details.append("Info: ").append(tableModel.getValueAt(row, 6)).append("\n\n");
                    
        // Add protocol-specific details
        switch (protocol) {
            case "TCP":
                details.append("Flags: SYN, ACK\n");
                details.append("Sequence Number: ").append(random.nextInt(1000000)).append("\n");
                details.append("Acknowledgment Number: ").append(random.nextInt(1000000)).append("\n");
                details.append("Window Size: ").append(random.nextInt(65535)).append("\n");
                break;
            case "UDP":
                details.append("Source Port: ").append(1024 + random.nextInt(60000)).append("\n");
                details.append("Destination Port: ").append(1024 + random.nextInt(60000)).append("\n");
                details.append("Checksum: 0x").append(Integer.toHexString(random.nextInt(65535))).append("\n");
                break;
            case "HTTP":
                details.append("Version: HTTP/1.1\n");
                details.append("Host: ").append(generateDomainName()).append("\n");
                details.append("User-Agent: Mozilla/5.0\n");
                details.append("Accept: text/html,application/xhtml+xml\n");
                break;
        }
                    
        detailsArea.setText(details.toString());
                    
        // Generate hex dump
        StringBuilder hex = new StringBuilder();
        hex.append("00000000  ");
                    
        byte[] randomBytes = new byte[128];
        random.nextBytes(randomBytes);
                    
        for (int i = 0; i < randomBytes.length; i++) {
            hex.append(String.format("%02x ", randomBytes[i]));
                                
            if ((i + 1) % 16 == 0) {
                hex.append(" |");
                for (int j = i - 15; j <= i; j++) {
                    char c = (char) randomBytes[j];
                    hex.append(Character.isISOControl(c) ? '.' : c);
                }
                hex.append("|\n").append(String.format("%08x  ", i + 1));
            }
        }
                    
        hexArea.setText(hex.toString());
    }
        
    private String generateIPAddress() {
        return random.nextInt(256) + "." + random.nextInt(256) + "." +
                random.nextInt(256) + "." + random.nextInt(256);
    }
        
    private String generateMacAddress() {
        StringBuilder mac = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            mac.append(String.format("%02x", random.nextInt(256)));
            if (i < 5) mac.append(":");
        }
        return mac.toString();
    }
        
    private String generateDomainName() {
        String[] domains = {"example.com", "test.org", "domain.net", "server.io", "site.edu"};
        String[] prefixes = {"www", "mail", "api", "dev", "blog", "shop", "m", "app"};
                    
        if (random.nextBoolean()) {
            return domains[random.nextInt(domains.length)];
        } else {
            return prefixes[random.nextInt(prefixes.length)] + "." +
                    domains[random.nextInt(domains.length)];
        }
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
                    info
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
                    rawPacket
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
        
    private void showStatistics() {
        int totalPackets = tableModel.getRowCount();
        if (totalPackets == 0) {
            JOptionPane.showMessageDialog(this, "No packets to analyze.",
                "Statistics", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
                    
        // Count protocols
        java.util.Map<String, Integer> protocolCount = new java.util.HashMap<>();
        for (int i = 0; i < totalPackets; i++) {
            String protocol = (String) tableModel.getValueAt(i, 4);
            protocolCount.put(protocol, protocolCount.getOrDefault(protocol, 0) + 1);
        }
                    
        // Build statistics message
        StringBuilder stats = new StringBuilder("Packet Statistics:\n\n");
        stats.append("Total Packets: ").append(totalPackets).append("\n\n");
        stats.append("Protocol Distribution:\n");
                    
        for (java.util.Map.Entry<String, Integer> entry : protocolCount.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalPackets;
            stats.append(String.format("- %s: %d (%.1f%%)\n",
                entry.getKey(), entry.getValue(), percentage));
        }
                    
        JOptionPane.showMessageDialog(this, stats.toString(),
            "Network Statistics", JOptionPane.INFORMATION_MESSAGE);
    }
            
    private void openCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            processPcapFile(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
        
    private void saveCaptureFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                "This would save to: " + fileChooser.getSelectedFile().getName(),
                "Save Capture", JOptionPane.INFORMATION_MESSAGE);
        }
    }
        
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "kAOMY IDS v1.0\n" +
            "A simple network packet capture and analysis tool\n\n" +
            "Created for demonstration purposes",
            "About KAOMY",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Method to set the current user ID
    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
        
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
                    
        SwingUtilities.invokeLater(() -> {
            new NetworkAnalyzer().setVisible(true);
        });
    }
}
