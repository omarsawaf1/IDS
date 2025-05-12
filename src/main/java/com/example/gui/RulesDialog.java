package com.example.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
import java.io.*;
// import java.util.Arrays;

import com.example.concurrent.RuleQueue;
import com.example.util.PacketRule;
public class RulesDialog extends JDialog {
    
    private JTable rulesTable;
    private DefaultTableModel rulesTableModel;
    private boolean rulesChanged = false;
    
    public RulesDialog(JFrame parent) {
        super(parent, "Security Rules", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIFactory.getBackgroundColor());
        // RuleQueue.getQueueRules();
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UIFactory.getBackgroundColor());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create rules table
        String[] columns = {"RuleId","Protocol", "Source:MAC_Address", "Source:IP_Address", "Source:Port", 
                           "Destination:MAC_Address", "Destination:IP_Address", "Destination:Port"};
        
        rulesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable in the table
            }
        };
        // 
        // RuleQueue.queueLoadRulesList();
        for (PacketRule rule : RuleQueue.getQueueRules().values()) {
            rulesTableModel.addRow(new Object[]{rule.getId(), rule.getRawRule()[0], rule.getRawRule()[1], rule.getRawRule()[2], rule.getRawRule()[3], rule.getRawRule()[4], rule.getRawRule()[5], rule.getRawRule()[6]}); 
        }
        // 
        rulesTable = new JTable(rulesTableModel);
        rulesTable.setBackground(UIFactory.getSecondaryBackgroundColor());
        rulesTable.setForeground(UIFactory.getTextColor());
        rulesTable.setGridColor(UIFactory.isDarkMode() ? new Color(70, 70, 70) : new Color(200, 200, 200));
        rulesTable.getTableHeader().setBackground(UIFactory.getTertiaryBackgroundColor());
        rulesTable.getTableHeader().setForeground(UIFactory.getTextColor());
        rulesTable.setRowHeight(25);
        rulesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane tableScrollPane = new JScrollPane(rulesTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Create button panel for table actions
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableButtonPanel.setBackground(UIFactory.getBackgroundColor());
        
        JButton addButton = UIFactory.createStyledButton("Add Rule", new Color(0, 100, 0));
        JButton editButton = UIFactory.createStyledButton("Edit Rule", UIFactory.ACCENT_COLOR);
        JButton deleteButton = UIFactory.createStyledButton("Delete Rule", new Color(150, 0, 0));
        JButton importButton = UIFactory.createStyledButton("Import", new Color(0, 0, 100));
        JButton exportButton = UIFactory.createStyledButton("Export", new Color(0, 0, 100));
        
        tableButtonPanel.add(addButton);
        tableButtonPanel.add(editButton);
        tableButtonPanel.add(deleteButton);
        tableButtonPanel.add(Box.createHorizontalStrut(20));
        tableButtonPanel.add(importButton);
        tableButtonPanel.add(exportButton);
        
        // Create dialog button panel
        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dialogButtonPanel.setBackground(UIFactory.getTertiaryBackgroundColor());
        dialogButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton applyButton = UIFactory.createStyledButton("Apply", UIFactory.ACCENT_COLOR);
        JButton closeButton = UIFactory.createStyledButton("Close", new Color(80, 80, 80));
        
        dialogButtonPanel.add(applyButton);
        dialogButtonPanel.add(closeButton);
        
        // Add action listeners
        addButton.addActionListener(e -> {
            showRuleDialog(null);
            rulesChanged = true;
        });
        
        editButton.addActionListener(e -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                showRuleDialog(selectedRow);
                rulesChanged = true;
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a rule to edit.",
                    "No Rule Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = rulesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this rule?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // 
                    int ruleId = (int) rulesTableModel.getValueAt(selectedRow, 0);
                    RuleQueue.removeRuleFromQueueById(ruleId);
                    // 
                    rulesTableModel.removeRow(selectedRow);
                    rulesChanged = true;
                    JOptionPane.showMessageDialog(this, 
                        "Rule deleted successfully.",
                        "Rule Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a rule to delete.",
                    "No Rule Selected",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        importButton.addActionListener(e -> {
            if (importRules()) {
                rulesChanged = true;
            }
        });
        
        exportButton.addActionListener(e -> exportRules());
        
        applyButton.addActionListener(e -> {
            if (rulesChanged) {
                saveRules();
                JOptionPane.showMessageDialog(this, 
                    "Rules applied successfully.",
                    "Rules Applied",
                    JOptionPane.INFORMATION_MESSAGE);
                rulesChanged = false;
            } else {
                JOptionPane.showMessageDialog(this, 
                    "No changes to apply.",
                    "No Changes",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        closeButton.addActionListener(e -> {
            if (rulesChanged) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Do you want to save before closing?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    saveRules();
                    dispose();
                } else if (confirm == JOptionPane.NO_OPTION) {
                    dispose();
                }
                // If CANCEL, do nothing
            } else {
                dispose();
            }
        });
        
        // Add components to main panel
        JLabel titleLabel = new JLabel("Security Rules");
        titleLabel.setForeground(UIFactory.getTextColor());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        // Add components to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(dialogButtonPanel, BorderLayout.SOUTH);
    }
    
    private void showRuleDialog(Integer editRowIndex) {
        JDialog ruleDialog = new JDialog(this, editRowIndex == null ? "Add Rule" : "Edit Rule", true);
        ruleDialog.setSize(500, 400);
        ruleDialog.setLocationRelativeTo(this);
        ruleDialog.setLayout(new BorderLayout());
        ruleDialog.getContentPane().setBackground(UIFactory.getBackgroundColor());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIFactory.getBackgroundColor());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 5, 5, 10);
        
        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.insets = new Insets(5, 0, 5, 5);
        
        // Create form fields
        String[] protocols = {"TCP", "UDP", "ICMP", "HTTP", "HTTPS", "DNS", "Any"};
        JComboBox<String> protocolComboBox = new JComboBox<>(protocols);
        protocolComboBox.setBackground(UIFactory.getSecondaryBackgroundColor());
        protocolComboBox.setForeground(UIFactory.getTextColor());
        
        JTextField sourceMacField = new JTextField(20);
        sourceMacField.setBackground(UIFactory.getSecondaryBackgroundColor());
        sourceMacField.setForeground(UIFactory.getTextColor());
        sourceMacField.setCaretColor(UIFactory.getTextColor());
        
        JTextField sourceIpField = new JTextField(20);
        sourceIpField.setBackground(UIFactory.getSecondaryBackgroundColor());
        sourceIpField.setForeground(UIFactory.getTextColor());
        sourceIpField.setCaretColor(UIFactory.getTextColor());
        
        JTextField sourcePortField = new JTextField(20);
        sourcePortField.setBackground(UIFactory.getSecondaryBackgroundColor());
        sourcePortField.setForeground(UIFactory.getTextColor());
        sourcePortField.setCaretColor(UIFactory.getTextColor());
        
        JTextField destMacField = new JTextField(20);
        destMacField.setBackground(UIFactory.getSecondaryBackgroundColor());
        destMacField.setForeground(UIFactory.getTextColor());
        destMacField.setCaretColor(UIFactory.getTextColor());
        
        JTextField destIpField = new JTextField(20);
        destIpField.setBackground(UIFactory.getSecondaryBackgroundColor());
        destIpField.setForeground(UIFactory.getTextColor());
        destIpField.setCaretColor(UIFactory.getTextColor());
        
        JTextField destPortField = new JTextField(20);
        destPortField.setBackground(UIFactory.getSecondaryBackgroundColor());
        destPortField.setForeground(UIFactory.getTextColor());
        destPortField.setCaretColor(UIFactory.getTextColor());
        
        // If editing an existing rule, populate the fields
        if (editRowIndex != null) {
            // sourceMacField.setText((String) rulesTableModel.getValueAt(editRowIndex, 0));
            protocolComboBox.setSelectedItem(rulesTableModel.getValueAt(editRowIndex, 1));
            sourceMacField.setText((String) rulesTableModel.getValueAt(editRowIndex, 2));
            sourceIpField.setText((String) rulesTableModel.getValueAt(editRowIndex, 3));
            sourcePortField.setText((String) rulesTableModel.getValueAt(editRowIndex, 4));
            destMacField.setText((String) rulesTableModel.getValueAt(editRowIndex, 5));
            destIpField.setText((String) rulesTableModel.getValueAt(editRowIndex, 6));
            destPortField.setText((String) rulesTableModel.getValueAt(editRowIndex, 7));
        } else {
            sourceMacField.setText("Any");
            sourceIpField.setText("Any");
            sourcePortField.setText("Any");
            destMacField.setText("Any");
            destIpField.setText("Any");
            destPortField.setText("Any");
        }
        
        // Add components to form panel
        JLabel protocolLabel = new JLabel("Protocol:");
        protocolLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(protocolLabel, labelConstraints);
        formPanel.add(protocolComboBox, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel sourceMacLabel = new JLabel("Source MAC:");
        sourceMacLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(sourceMacLabel, labelConstraints);
        formPanel.add(sourceMacField, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel sourceIpLabel = new JLabel("Source IP:");
        sourceIpLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(sourceIpLabel, labelConstraints);
        formPanel.add(sourceIpField, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel sourcePortLabel = new JLabel("Source Port:");
        sourcePortLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(sourcePortLabel, labelConstraints);
        formPanel.add(sourcePortField, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel destMacLabel = new JLabel("Destination MAC:");
        destMacLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(destMacLabel, labelConstraints);
        formPanel.add(destMacField, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel destIpLabel = new JLabel("Destination IP:");
        destIpLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(destIpLabel, labelConstraints);
        formPanel.add(destIpField, fieldConstraints);
        
        labelConstraints.gridy++;
        fieldConstraints.gridy++;
        JLabel destPortLabel = new JLabel("Destination Port:");
        destPortLabel.setForeground(UIFactory.getTextColor());
        formPanel.add(destPortLabel, labelConstraints);
        formPanel.add(destPortField, fieldConstraints);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIFactory.getTertiaryBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton saveButton = UIFactory.createStyledButton("Save", UIFactory.ACCENT_COLOR);
        JButton cancelButton = UIFactory.createStyledButton("Cancel", new Color(80, 80, 80));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        saveButton.addActionListener(e -> {
            String ruleId="";
            String protocol = (String) protocolComboBox.getSelectedItem();
            String sourceMac = sourceMacField.getText().trim();
            String sourceIp = sourceIpField.getText().trim();
            String sourcePort = sourcePortField.getText().trim();
            String destMac = destMacField.getText().trim();
            String destIp = destIpField.getText().trim();
            String destPort = destPortField.getText().trim();
            
            if (sourceMac.isEmpty()) sourceMac = "Any";
            if (sourceIp.isEmpty()) sourceIp = "Any";
            if (sourcePort.isEmpty()) sourcePort = "Any";
            if (destMac.isEmpty()) destMac = "Any";
            if (destIp.isEmpty()) destIp = "Any";
            if (destPort.isEmpty()) destPort = "Any";
            
            String[] rowData = {
                ruleId,
                protocol,
                sourceMac,
                sourceIp,
                sourcePort,
                destMac,
                destIp,
                destPort
            };
            String[] queueData = {
                protocol,
                sourceMac,
                sourceIp,
                sourcePort,
                destMac,
                destIp,
                destPort
            };
            
            if (editRowIndex != null) {
                // Update existing row
                for (int i = 0; i < rowData.length; i++) {
                    rulesTableModel.setValueAt(rowData[i], editRowIndex, i);
                }
                JOptionPane.showMessageDialog(ruleDialog,
                    "Rule updated successfully.",
                    "Rule Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Add new row

                rowData[0]=Integer.toString(RuleQueue.addRuleToQueue(queueData));
                rulesTableModel.addRow(rowData);
                JOptionPane.showMessageDialog(ruleDialog,
                    "Rule added successfully.",
                    "Rule Added",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
            ruleDialog.dispose();
        });
        
        cancelButton.addActionListener(e -> ruleDialog.dispose());
        
        // Add components to dialog
        ruleDialog.add(formPanel, BorderLayout.CENTER);
        ruleDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        ruleDialog.setVisible(true);
    }
    
    private boolean importRules() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Rules");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                boolean isFirstLine = true;
                int importedCount = 0;
                
                // Clear existing rules if user confirms
                int clearConfirm = JOptionPane.showConfirmDialog(this,
                    "Do you want to clear existing rules before importing?",
                    "Clear Existing Rules",
                    JOptionPane.YES_NO_CANCEL_OPTION);
                
                if (clearConfirm == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
                
                if (clearConfirm == JOptionPane.YES_OPTION) {
                    rulesTableModel.setRowCount(0);
                }
                
                while ((line = reader.readLine()) != null) {
                    // Skip header row if present
                    if (isFirstLine) {
                        isFirstLine = false;
                        // Check if this is a header row by looking for column names
                        if (line.toLowerCase().contains("protocol") && 
                            line.toLowerCase().contains("source") && 
                            line.toLowerCase().contains("destination")) {
                            continue;
                        }
                    }
                    
                    // Parse CSV line
                    String[] values = parseCSVLine(line);
                    
                    // Ensure we have the right number of columns
                    if (values.length >= 7) {
                        rulesTableModel.addRow(new Object[] {
                            values[0], // Protocol
                            values[1], // Source MAC
                            values[2], // Source IP
                            values[3], // Source Port
                            values[4], // Destination MAC
                            values[5], // Destination IP
                            values[6]  // Destination Port
                        });
                        importedCount++;
                    }
                }
                
                JOptionPane.showMessageDialog(this,
                    importedCount + " rules imported successfully.",
                    "Import Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
                return importedCount > 0;
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error importing rules: " + e.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }
    
    private String[] parseCSVLine(String line) {
        // Simple CSV parser that handles quoted values
        java.util.List<String> result = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentValue = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentValue.toString().trim());
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
        
        // Add the last value
        result.add(currentValue.toString().trim());
        
        return result.toArray(new String[0]);
    }
    
    private void exportRules() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Rules");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            // Add .csv extension if not present
            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(selectedFile)) {
                // Write header
                writer.write("Protocol,Source:MAC_Address,Source:IP_Address,Source:Port," +
                             "Destination:MAC_Address,Destination:IP_Address,Destination:Port\n");
                
                // Write data
                for (int i = 0; i < rulesTableModel.getRowCount(); i++) {
                    for (int j = 0; j < rulesTableModel.getColumnCount(); j++) {
                        String value = rulesTableModel.getValueAt(i, j).toString();
                        
                        // Quote values that contain commas
                        if (value.contains(",")) {
                            writer.write("\"" + value + "\"");
                        } else {
                            writer.write(value);
                        }
                        
                        // Add comma except for last column
                        if (j < rulesTableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "Rules exported successfully to " + selectedFile.getName(),
                    "Export Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting rules: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveRules() {
        // This method would typically save the rules to a configuration file or database
        // For now, we'll just simulate saving by showing a success message
        
        // In a real application, you would implement code here to:
        // 1. Convert the rules to a suitable format (JSON, XML, etc.)
        // 2. Save them to a file or database
        // 3. Possibly notify other components of the application about the changes
        
        try {
            // Simulate a delay for saving
            Thread.sleep(500);
            
            // For demonstration purposes, we'll save to a temporary file
            File tempFile = new File(System.getProperty("java.io.tmpdir"), "security_rules.csv");
            try (FileWriter writer = new FileWriter(tempFile)) {
                // Write header
                writer.write("Protocol,Source:MAC_Address,Source:IP_Address,Source:Port," +
                             "Destination:MAC_Address,Destination:IP_Address,Destination:Port\n");
                
                // Write data
                for (int i = 0; i < rulesTableModel.getRowCount(); i++) {
                    for (int j = 0; j < rulesTableModel.getColumnCount(); j++) {
                        String value = rulesTableModel.getValueAt(i, j).toString();
                        
                        // Quote values that contain commas
                        if (value.contains(",")) {
                            writer.write("\"" + value + "\"");
                        } else {
                            writer.write(value);
                        }
                        
                        // Add comma except for last column
                        if (j < rulesTableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
            }
            
            System.out.println("Rules saved to: " + tempFile.getAbsolutePath());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving rules: " + e.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
