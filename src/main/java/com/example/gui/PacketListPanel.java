package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketListPanel extends JPanel {
    private JTable packetTable;
    private DefaultTableModel tableModel;
    private JTextArea packetDetailsArea;
    private JTextArea hexDumpArea;
    private JLabel statusLabel;
    private JPanel controlPanel;
    private Timer animationTimer;
    private int captureCount = 0;
    private Map<String, Color> protocolColors;
    private List<Packet> packets;
    private JTextField filterField;
    private TableRowSorter<DefaultTableModel> sorter;
    
    public PacketListPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 240, 245));
        
        packets = new ArrayList<>();
        
        // Initialize protocol colors
        initProtocolColors();
        
        // Create top control panel with gradient
        createControlPanel();
        add(controlPanel, BorderLayout.NORTH);
        
        // Create table model with column names
        String[] columnNames = {"#", "Time", "Protocol", "Source", "Destination", "Length", "Info"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        // Create table with custom styling
        packetTable = createStyledTable();
        
        // Add sorting capability
        sorter = new TableRowSorter<>(tableModel);
        packetTable.setRowSorter(sorter);
        
        // Create packet details area with custom styling
        JPanel detailsPanel = createDetailsPanel();
        
        // Create split pane with custom divider
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(packetTable) {
                {
                    setBorder(BorderFactory.createEmptyBorder());
                    getViewport().setBackground(new Color(250, 250, 252));
                }
            },
            detailsPanel
        );
        splitPane.setDividerLocation(400);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Custom divider
        splitPane.setUI(new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    @Override
                    public void paint(Graphics g) {
                        g.setColor(new Color(220, 220, 225));
                        g.fillRect(0, 0, getSize().width, getSize().height);
                        
                        // Draw grip
                        int x = getSize().width / 2 - 15;
                        int y = getSize().height / 2 - 3;
                        for (int i = 0; i < 6; i++) {
                            g.setColor(new Color(180, 180, 185));
                            g.fillRect(x + (i * 6), y, 4, 4);
                        }
                    }
                };
            }
        });
        
        add(splitPane, BorderLayout.CENTER);
        
        // Create status bar
        statusLabel = new JLabel("Ready to capture packets");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(statusLabel, BorderLayout.SOUTH);
        
        // Add selection listener
        packetTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = packetTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Convert view row index to model row index
                    int modelRow = packetTable.convertRowIndexToModel(selectedRow);
                    displayPacketDetails(modelRow);
                    
                    // Animate the selection
                    animateSelection(selectedRow);
                }
            }
        });
    }
    
    private void initProtocolColors() {
        protocolColors = new HashMap<>();
        protocolColors.put("TCP", new Color(65, 105, 225));  // Royal Blue
        protocolColors.put("UDP", new Color(50, 205, 50));   // Lime Green
        protocolColors.put("HTTP", new Color(255, 69, 0));   // Orange Red
        protocolColors.put("HTTPS", new Color(148, 0, 211)); // Dark Violet
        protocolColors.put("DNS", new Color(255, 215, 0));   // Gold
        protocolColors.put("ICMP", new Color(220, 20, 60));  // Crimson
        protocolColors.put("ARP", new Color(0, 139, 139));   // Dark Cyan
        protocolColors.put("IPv6", new Color(106, 90, 205)); // Slate Blue
    }
    
    private JTable createStyledTable() {
        JTable table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setIntercellSpacing(new Dimension(10, 5));
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        
        // Custom font
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
        table.setFont(tableFont);
        
        // Custom header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 240, 245));
        header.setForeground(new Color(60, 60, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // #
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Time
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Protocol
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Source
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Destination
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Length
        table.getColumnModel().getColumn(6).setPreferredWidth(300); // Info
        
        // Custom cell renderer for protocol column
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String protocol = (String) value;
                    Color bgColor = protocolColors.getOrDefault(protocol, new Color(180, 180, 180));
                    
                    setBackground(bgColor);
                    setForeground(Color.WHITE);
                    
                    // Create rounded label
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                } else {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                
                return c;
            }
        });
        
        // Zebra striping for rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 250, 252) : new Color(240, 240, 245));
                    c.setForeground(new Color(50, 50, 50));
                }
                
                // Add some padding
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                
                return c;
            }
        });
        
        return table;
    }
    
    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout(5, 5));
        detailsPanel.setBackground(new Color(250, 250, 252));
        
        // Create packet details area
        packetDetailsArea = new JTextArea();
        packetDetailsArea.setEditable(false);
        packetDetailsArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        packetDetailsArea.setBackground(new Color(250, 250, 252));
        packetDetailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        packetDetailsArea.setLineWrap(true);
        packetDetailsArea.setWrapStyleWord(true);
        
        // Create hex dump area
        hexDumpArea = new JTextArea();
        hexDumpArea.setEditable(false);
        hexDumpArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        hexDumpArea.setBackground(new Color(250, 250, 252));
        hexDumpArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for details and hex dump
        JTabbedPane detailsPane = new JTabbedPane(JTabbedPane.TOP);
        detailsPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailsPane.addTab("Packet Details", new JScrollPane(packetDetailsArea) {
            {
                setBorder(BorderFactory.createEmptyBorder());
            }
        });
        detailsPane.addTab("Hex Dump", new JScrollPane(hexDumpArea) {
            {
                setBorder(BorderFactory.createEmptyBorder());
            }
        });
        
        detailsPane.setBorder(BorderFactory.createEmptyBorder());
        
        detailsPanel.add(detailsPane, BorderLayout.CENTER);
        
        return detailsPanel;
    }
    
    private void createControlPanel() {
        controlPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(60, 70, 92),
                    0, getHeight(), new Color(40, 50, 72)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        // Filter field with rounded border
        filterField = new JTextField(20);
        filterField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Filter button with hover effect
        JButton filterButton = createStyledButton("Apply Filter", new Color(65, 105, 225));
        
        // Export button
        JButton exportButton = createStyledButton("Export Packets", new Color(50, 150, 50));
        
        // Import button
        JButton importButton = createStyledButton("Import Packets", new Color(150, 100, 50));
        
        // Clear button
        JButton clearButton = createStyledButton("Clear", new Color(220, 50, 50));
        
        // Add components to control panel
        JLabel filterLabel = new JLabel("Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(Color.WHITE);
        
        controlPanel.add(filterLabel);
        controlPanel.add(filterField);
        controlPanel.add(filterButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(importButton);
        controlPanel.add(exportButton);
        controlPanel.add(clearButton);
        
        // Add action listeners
        filterButton.addActionListener(e -> applyFilter());
        exportButton.addActionListener(e -> exportPackets());
        importButton.addActionListener(e -> importPackets());
        clearButton.addActionListener(e -> clearPackets());
    }
    
    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
                
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setMargin(new Insets(8, 15, 8, 15));
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Determine colors based on hover state
                Color bgColor = hover ? baseColor.brighter() : baseColor;
                
                // Draw rounded rectangle background
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        return button;
    }
    
    public void addPacket(Packet packet) {
        // Add to internal list
        packets.add(packet);
        
        // Add to table
        tableModel.addRow(packet.toTableRow());
        captureCount++;
        
        // Update status
        statusLabel.setText("Captured " + captureCount + " packets");
        
        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            int lastRow = packetTable.getRowCount() - 1;
            if (lastRow >= 0) {
                packetTable.scrollRectToVisible(packetTable.getCellRect(lastRow, 0, true));
            }
        });
    }
    
    public void clearPackets() {
        // Clear internal list
        packets.clear();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Clear details
        packetDetailsArea.setText("");
        hexDumpArea.setText("");
        
        // Reset counter
        captureCount = 0;
        
        // Update status
        statusLabel.setText("Packet list cleared");
        
        // Show animation
        showClearAnimation();
    }
    
    private void showClearAnimation() {
        JPanel animPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(220, 50, 50, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                String message = "All packets cleared";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(message, x, y);
            }
        };
        
        animPanel.setOpaque(false);
        
        // Add panel as glass pane
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            Container contentPane = rootPane.getContentPane();
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            
            animPanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
            layeredPane.add(animPanel, JLayeredPane.POPUP_LAYER);
            
            // Create fade-out animation
            Timer timer = new Timer(50, null);
            final int[] alpha = {150};
            
            timer.addActionListener(e -> {
                alpha[0] -= 10;
                if (alpha[0] <= 0) {
                    timer.stop();
                    layeredPane.remove(animPanel);
                    layeredPane.repaint();
                } else {
                    animPanel.repaint();
                }
            });
            
            timer.start();
        }
    }
    
    private void animateSelection(int row) {
        // Cancel any existing animation
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        // Create new animation
        final Color originalColor = packetTable.getSelectionBackground();
        final Color highlightColor = new Color(100, 180, 255);
        
        animationTimer = new Timer(50, null);
        final int[] step = {0};
        final int totalSteps = 10;
        
        animationTimer.addActionListener(e -> {
            step[0]++;
            
            if (step[0] <= totalSteps / 2) {
                // Fade to highlight color
                float ratio = (float) step[0] / (totalSteps / 2);
                Color currentColor = interpolateColor(originalColor, highlightColor, ratio);
                packetTable.setSelectionBackground(currentColor);
            } else if (step[0] <= totalSteps) {
                // Fade back to original color
                float ratio = (float) (step[0] - totalSteps / 2) / (totalSteps / 2);
                Color currentColor = interpolateColor(highlightColor, originalColor, ratio);
                packetTable.setSelectionBackground(currentColor);
            } else {
                // End animation
                packetTable.setSelectionBackground(originalColor);
                animationTimer.stop();
            }
        });
        
        animationTimer.start();
    }
    
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int red = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int green = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int blue = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(red, green, blue);
    }
    
    private void displayPacketDetails(int rowIndex) {
        // Get packet ID from the first column
        int packetId = Integer.parseInt(tableModel.getValueAt(rowIndex, 0).toString());
        
        // Build packet details string
        StringBuilder details = new StringBuilder();
        details.append("Packet #").append(packetId).append("\n\n");
        details.append("Time: ").append(tableModel.getValueAt(rowIndex, 1)).append("\n");
        details.append("Protocol: ").append(tableModel.getValueAt(rowIndex, 2)).append("\n");
        details.append("Source: ").append(tableModel.getValueAt(rowIndex, 3)).append("\n");
        details.append("Destination: ").append(tableModel.getValueAt(rowIndex, 4)).append("\n");
        details.append("Length: ").append(tableModel.getValueAt(rowIndex, 5)).append(" bytes\n");
        details.append("Info: ").append(tableModel.getValueAt(rowIndex, 6)).append("\n\n");
        
        // Add more detailed information
        details.append("Header Information:\n");
        details.append("-------------------\n");
        details.append("Version: IPv4\n");
        details.append("Header Length: 20 bytes\n");
        details.append("Type of Service: 0x00\n");
        details.append("Total Length: ").append(tableModel.getValueAt(rowIndex, 5)).append(" bytes\n");
        details.append("Identification: 0x1234\n");
        details.append("Flags: Don't Fragment\n");
        details.append("Fragment Offset: 0\n");
        details.append("Time to Live: 64\n");
        details.append("Protocol: ").append(tableModel.getValueAt(rowIndex, 2)).append("\n");
        details.append("Header Checksum: 0x1234\n");
        details.append("Source IP: ").append(tableModel.getValueAt(rowIndex, 3)).append("\n");
        details.append("Destination IP: ").append(tableModel.getValueAt(rowIndex, 4)).append("\n");
        
        packetDetailsArea.setText(details.toString());
        packetDetailsArea.setCaretPosition(0);
        
        // Generate hex dump
        generateHexDump(rowIndex);
    }
    
    private void generateHexDump(int rowIndex) {
        // In a real application, this would use actual packet data
        // For this example, we'll generate a sample hex dump
        StringBuilder hexDump = new StringBuilder();
        hexDump.append("Offset    00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F    ASCII\n");
        hexDump.append("--------  -----------------------------------------------    ----------------\n");
        
        // Generate some sample data based on the packet info
        String protocol = tableModel.getValueAt(rowIndex, 2).toString();
        String source = tableModel.getValueAt(rowIndex, 3).toString();
        String dest = tableModel.getValueAt(rowIndex, 4).toString();
        
        // Convert strings to bytes for the hex dump
        byte[] protocolBytes = protocol.getBytes();
        byte[] sourceBytes = source.getBytes();
        byte[] destBytes = dest.getBytes();
        
        // Create a sample packet payload
        List<Byte> payload = new ArrayList<>();
        
        // Add header bytes
        for (int i = 0; i < 14; i++) {
            payload.add((byte) (i + 0x40));  // Ethernet header
        }
        
        // Add IP header
        payload.add((byte) 0x45);  // Version and header length
        payload.add((byte) 0x00);  // Type of service
        
        // Add protocol bytes
        for (byte b : protocolBytes) {
            payload.add(b);
        }
        
        // Add source bytes
        for (byte b : sourceBytes) {
            payload.add(b);
        }
        
        // Add destination bytes
        for (byte b : destBytes) {
            payload.add(b);
        }
        
        // Fill to at least 64 bytes (minimum Ethernet frame size)
        while (payload.size() < 64) {
            payload.add((byte) 0x00);
        }
        
        // Generate the hex dump
        for (int i = 0; i < payload.size(); i += 16) {
            // Offset
            hexDump.append(String.format("%08X  ", i));
            
            // Hex values
            for (int j = 0; j < 16; j++) {
                if (i + j < payload.size()) {
                    hexDump.append(String.format("%02X ", payload.get(i + j)));
                } else {
                    hexDump.append("   ");
                }
            }
            
            // ASCII representation
            hexDump.append("   ");
            for (int j = 0; j < 16; j++) {
                if (i + j < payload.size()) {
                    byte b = payload.get(i + j);
                    if (b >= 32 && b < 127) {
                        hexDump.append((char) b);
                    } else {
                        hexDump.append('.');
                    }
                }
            }
            
            hexDump.append("\n");
        }
        
        hexDumpArea.setText(hexDump.toString());
        hexDumpArea.setCaretPosition(0);
    }
    
    private void applyFilter() {
        String filterText = filterField.getText().trim();
        
        if (filterText.isEmpty()) {
            sorter.setRowFilter(null);
            statusLabel.setText("Filter cleared");
        } else {
            try {
                // Create a case-insensitive filter that searches all columns
                RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + filterText);
                sorter.setRowFilter(rowFilter);
                
                int matchCount = packetTable.getRowCount();
                statusLabel.setText("Filter applied: " + filterText + " (" + matchCount + " matches)");
                
                // Show animation for filter
                showFilterAnimation(filterText);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid filter pattern: " + ex.getMessage(),
                    "Filter Error",
                    JOptionPane.ERROR_MESSAGE
                );
                statusLabel.setText("Filter error: " + ex.getMessage());
            }
        }
    }
    
    private void showFilterAnimation(String filterText) {
        // Create a temporary label for animation
        JLabel animLabel = new JLabel("Filtering: " + filterText);
        animLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        animLabel.setForeground(new Color(65, 105, 225));
        animLabel.setBackground(new Color(240, 240, 245, 200));
        animLabel.setOpaque(true);
        animLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(65, 105, 225), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        animLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add to layered pane for animation
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            
            // Position in center
            Dimension labelSize = animLabel.getPreferredSize();
            int x = (layeredPane.getWidth() - labelSize.width) / 2;
            int y = (layeredPane.getHeight() - labelSize.height) / 2;
            animLabel.setBounds(x, y, labelSize.width, labelSize.height);
            
            layeredPane.add(animLabel, JLayeredPane.POPUP_LAYER);
            
            // Create fade-out animation
            Timer timer = new Timer(1500, e -> {
                Timer fadeTimer = new Timer(50, null);
                final float[] alpha = {1.0f};
                
                fadeTimer.addActionListener(evt -> {
                    alpha[0] -= 0.1f;
                    if (alpha[0] <= 0) {
                        fadeTimer.stop();
                        layeredPane.remove(animLabel);
                        layeredPane.repaint();
                    } else {
                        animLabel.setBackground(new Color(240, 240, 245, (int)(alpha[0] * 200)));
                        animLabel.setForeground(new Color(65, 105, 225, (int)(alpha[0] * 255)));
                        animLabel.setForeground(new Color(65, 105, 225, (int)(alpha[0] * 255)));
                        animLabel.repaint();
                    }
                });
                
                fadeTimer.start();
            });
            
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void exportPackets() {
        if (packets.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No packets to export",
                "Export Error",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Packets");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PCAP Files (*.pcap)", "pcap"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String extension = "";
            
            // Get selected file filter
            FileNameExtensionFilter filter = (FileNameExtensionFilter) fileChooser.getFileFilter();
            String description = filter.getDescription();
            
            // Determine file extension based on filter
            if (description.contains("CSV")) {
                extension = ".csv";
            } else if (description.contains("Text")) {
                extension = ".txt";
            } else if (description.contains("PCAP")) {
                extension = ".pcap";
            }
            
            // Add extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(extension)) {
                fileToSave = new File(fileToSave.getAbsolutePath() + extension);
            }
            
            try {
                // Export based on file type
                if (extension.equals(".csv")) {
                    exportToCSV(fileToSave);
                } else if (extension.equals(".txt")) {
                    exportToText(fileToSave);
                } else if (extension.equals(".pcap")) {
                    exportToPcap(fileToSave);
                }
                
                statusLabel.setText("Packets exported to: " + fileToSave.getName());
                
                // Show success animation
                showExportSuccessAnimation();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error exporting packets: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE
                );
                statusLabel.setText("Export failed: " + ex.getMessage());
            }
        }
    }
    
    private void exportToCSV(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("Number,Time,Protocol,Source,Destination,Length,Info");
            
            // Write data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder line = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    line.append(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) {
                        line.append(",");
                    }
                }
                writer.println(line.toString());
            }
        }
    }
    
    private void exportToText(File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("Network Packet Capture Export");
            writer.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.println("Total Packets: " + tableModel.getRowCount());
            writer.println("----------------------------------------");
            writer.println();
            
            // Write data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.println("Packet #" + tableModel.getValueAt(i, 0));
                writer.println("Time: " + tableModel.getValueAt(i, 1));
                writer.println("Protocol: " + tableModel.getValueAt(i, 2));
                writer.println("Source: " + tableModel.getValueAt(i, 3));
                writer.println("Destination: " + tableModel.getValueAt(i, 4));
                writer.println("Length: " + tableModel.getValueAt(i, 5) + " bytes");
                writer.println("Info: " + tableModel.getValueAt(i, 6));
                writer.println("----------------------------------------");
                writer.println();
            }
        }
    }
    
    private void exportToPcap(File file) throws IOException {
        // In a real application, this would use a library like Pcap4J to write PCAP files
        // For this example, we'll just create a placeholder file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // PCAP global header (magic number, version, timezone, etc.)
            byte[] pcapHeader = {
                (byte) 0xd4, (byte) 0xc3, (byte) 0xb2, (byte) 0xa1, // Magic number
                0x02, 0x00, 0x04, 0x00,                             // Version
                0x00, 0x00, 0x00, 0x00,                             // GMT to local correction
                0x00, 0x00, 0x00, 0x00,                             // Accuracy of timestamps
                (byte) 0xff, (byte) 0xff, 0x00, 0x00,               // Max length of captured packets
                0x01, 0x00, 0x00, 0x00                              // Data link type (Ethernet)
            };
            fos.write(pcapHeader);
            
            // Write packet data
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                // In a real application, this would be the actual packet data
                // For this example, we'll create dummy packet data
                
                // Packet header (timestamp, captured length, original length)
                byte[] packetHeader = {
                    0x00, 0x00, 0x00, 0x00,                         // Timestamp seconds
                    0x00, 0x00, 0x00, 0x00,                         // Timestamp microseconds
                    0x40, 0x00, 0x00, 0x00,                         // Captured length (64 bytes)
                    0x40, 0x00, 0x00, 0x00                          // Original length (64 bytes)
                };
                fos.write(packetHeader);
                
                // Dummy packet data (64 bytes, minimum Ethernet frame size)
                byte[] packetData = new byte[64];
                
                // Set some values based on table data
                String protocol = tableModel.getValueAt(i, 2).toString();
                byte[] protocolBytes = protocol.getBytes();
                
                // Copy protocol name into packet data
                System.arraycopy(protocolBytes, 0, packetData, 23, 
                        Math.min(protocolBytes.length, 10));
                
                fos.write(packetData);
            }
        }
    }
    
    private void showExportSuccessAnimation() {
        JPanel animPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Success message box
                int width = 300;
                int height = 150;
                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;
                
                // Draw box with gradient
                GradientPaint gp = new GradientPaint(
                    x, y, new Color(50, 150, 50),
                    x, y + height, new Color(30, 100, 30)
                );
                g2d.setPaint(gp);
                g2d.fillRoundRect(x, y, width, height, 20, 20);
                
                // Draw border
                g2d.setColor(new Color(200, 255, 200));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(x, y, width, height, 20, 20);
                
                // Draw checkmark
                int checkX = x + width / 2;
                int checkY = y + height / 2 - 10;
                int checkSize = 40;
                
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(checkX - checkSize/3, checkY, checkX, checkY + checkSize/3);
                g2d.drawLine(checkX, checkY + checkSize/3, checkX + checkSize/2, checkY - checkSize/3);
                
                // Draw text
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                String message = "Export Successful";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (width - fm.stringWidth(message)) / 2;
                int textY = y + height - 40;
                g2d.drawString(message, textX, textY);
            }
        };
        
        animPanel.setOpaque(false);
        
        // Add panel as glass pane
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            Container contentPane = rootPane.getContentPane();
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            
            animPanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
            layeredPane.add(animPanel, JLayeredPane.POPUP_LAYER);
            
            // Create fade-out animation
            Timer timer = new Timer(2000, e -> {
                Timer fadeTimer = new Timer(50, null);
                final int[] alpha = {150};
                
                fadeTimer.addActionListener(evt -> {
                    alpha[0] -= 10;
                    if (alpha[0] <= 0) {
                        fadeTimer.stop();
                        layeredPane.remove(animPanel);
                        layeredPane.repaint();
                    } else {
                        animPanel.repaint();
                    }
                });
                
                fadeTimer.start();
            });
            
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void importPackets() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Packets");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PCAP Files (*.pcap)", "pcap"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try {
                // Determine file type by extension
                if (fileToLoad.getName().toLowerCase().endsWith(".csv")) {
                    importFromCSV(fileToLoad);
                } else if (fileToLoad.getName().toLowerCase().endsWith(".pcap")) {
                    importFromPcap(fileToLoad);
                } else {
                    throw new IOException("Unsupported file format");
                }
                
                statusLabel.setText("Imported packets from: " + fileToLoad.getName());
                
                // Show success animation
                showImportSuccessAnimation();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error importing packets: " + ex.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE
                );
                statusLabel.setText("Import failed: " + ex.getMessage());
            }
        }
    }
    
    private void importFromCSV(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();
            
            // Read data
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    tableModel.addRow(parts);
                    captureCount++;
                }
            }
        }
    }
    
    private void importFromPcap(File file) throws IOException {
        // In a real application, this would use a library like Pcap4J to read PCAP files
        // For this example, we'll just create some sample data
        
        // COMMENT: In a real implementation, you would use your packet capture library here
        
        // Create some sample packets
        for (int i = 0; i < 10; i++) {
            String[] row = {
                String.valueOf(tableModel.getRowCount() + 1),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
                getRandomProtocol(),
                "192.168.1." + (int)(Math.random() * 255),
                "10.0.0." + (int)(Math.random() * 255),
                String.valueOf(64 + (int)(Math.random() * 1400)),
                "Imported packet from PCAP file"
            };
            
            tableModel.addRow(row);
            captureCount++;
        }
    }
    
    private String getRandomProtocol() {
        String[] protocols = {"TCP", "UDP", "HTTP", "HTTPS", "DNS", "ICMP", "ARP"};
        return protocols[(int)(Math.random() * protocols.length)];
    }
    
    private void showImportSuccessAnimation() {
        // Create a temporary panel for animation
        JPanel animPanel = new JPanel() {
            private final int numParticles = 50;
            private final int[] particleX = new int[numParticles];
            private final int[] particleY = new int[numParticles];
            private final int[] particleSize = new int[numParticles];
            private final Color[] particleColor = new Color[numParticles];
            private final int[] particleSpeed = new int[numParticles];
            
            {
                // Initialize particles
                for (int i = 0; i < numParticles; i++) {
                    particleX[i] = (int)(Math.random() * getWidth());
                    particleY[i] = -particleSize[i]; // Start above
                    particleSize[i] = 5 + (int)(Math.random() * 15);
                    particleColor[i] = new Color(
                        (int)(Math.random() * 100) + 155,
                        (int)(Math.random() * 100) + 155,
                        (int)(Math.random() * 100) + 155
                    );
                    particleSpeed[i] = 3 + (int)(Math.random() * 7);
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent background
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw message
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
                g2d.setColor(Color.WHITE);
                String message = "Import Successful";
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(message)) / 2;
                int textY = getHeight() / 2;
                g2d.drawString(message, textX, textY);
                
                // Draw particles
                for (int i = 0; i < numParticles; i++) {
                    g2d.setColor(particleColor[i]);
                    g2d.fillOval(particleX[i], particleY[i], particleSize[i], particleSize[i]);
                    
                    // Update position for next frame
                    particleY[i] += particleSpeed[i];
                    
                    // Reset if off screen
                    if (particleY[i] > getHeight()) {
                        particleY[i] = -particleSize[i];
                        particleX[i] = (int)(Math.random() * getWidth());
                    }
                }
            }
        };
        
        animPanel.setOpaque(false);
        
        // Add panel as glass pane
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane != null) {
            Container contentPane = rootPane.getContentPane();
            JLayeredPane layeredPane = rootPane.getLayeredPane();
            
            animPanel.setBounds(0, 0, contentPane.getWidth(), contentPane.getHeight());
            layeredPane.add(animPanel, JLayeredPane.POPUP_LAYER);
            
            // Create animation timer
            Timer timer = new Timer(30, null);
            final int[] frames = {0};
            final int totalFrames = 60;
            
            timer.addActionListener(e -> {
                frames[0]++;
                animPanel.repaint();
                
                if (frames[0] >= totalFrames) {
                    timer.stop();
                    layeredPane.remove(animPanel);
                    layeredPane.repaint();
                }
            });
            
            timer.start();
        }
    }
}
