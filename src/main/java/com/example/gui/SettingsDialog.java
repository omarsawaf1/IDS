package com.example.gui;
import javax.swing.*;
import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

public class SettingsDialog extends JDialog {
    
    private JComboBox<String> themeComboBox;
    private JComboBox<String> fontSizeComboBox;
    private JComboBox<String> timeFormatComboBox;
    private JCheckBox autoSaveCheckBox;
    private JComboBox<String> interfaceComboBox;
    private JCheckBox promiscuousCheckBox;
    private JSpinner bufferSizeSpinner;
    private JCheckBox[] columnCheckBoxes;
    private JCheckBox macResolutionCheckBox;
    private JCheckBox networkResolutionCheckBox;
    private JCheckBox transportResolutionCheckBox;
    
    public SettingsDialog(JFrame parent) {
        super(parent, "Settings", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIFactory.getBackgroundColor());
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIFactory.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Create tabbed pane for settings categories
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIFactory.getBackgroundColor());
        tabbedPane.setForeground(UIFactory.getTextColor());
        
        // Add tabs
        tabbedPane.addTab("General", createGeneralPanel());
        tabbedPane.addTab("Capture", createCapturePanel());
        tabbedPane.addTab("Display", createDisplayPanel());
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UIFactory.getTertiaryBackgroundColor());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton saveButton = UIFactory.createStyledButton("Save", UIFactory.ACCENT_COLOR);
        JButton cancelButton = UIFactory.createStyledButton("Cancel", new Color(80, 80, 80));
        
        saveButton.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        contentPanel.add(tabbedPane);
        
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIFactory.getBackgroundColor());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Theme setting
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setForeground(UIFactory.getTextColor());
        panel.add(themeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] themes = {"Light", "Dark", "System Default"};
        themeComboBox = new JComboBox<>(themes);
        themeComboBox.setSelectedItem(UIFactory.isDarkMode() ? "Dark" : "Light");
        themeComboBox.setBackground(UIFactory.getSecondaryBackgroundColor());
        themeComboBox.setForeground(UIFactory.getTextColor());
        panel.add(themeComboBox, gbc);
        
        // Font size setting
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        
        JLabel fontSizeLabel = new JLabel("Font Size:");
        fontSizeLabel.setForeground(UIFactory.getTextColor());
        panel.add(fontSizeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        String[] fontSizes = {"Small", "Medium", "Large"};
        fontSizeComboBox = new JComboBox<>(fontSizes);
        fontSizeComboBox.setSelectedItem("Medium");
        fontSizeComboBox.setBackground(UIFactory.getSecondaryBackgroundColor());
        fontSizeComboBox.setForeground(UIFactory.getTextColor());
        panel.add(fontSizeComboBox, gbc);
        
        // Time format setting
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        
        JLabel timeFormatLabel = new JLabel("Time Format:");
        timeFormatLabel.setForeground(UIFactory.getTextColor());
        panel.add(timeFormatLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        String[] timeFormats = {"Absolute", "Relative", "Delta", "UTC"};
        timeFormatComboBox = new JComboBox<>(timeFormats);
        timeFormatComboBox.setSelectedItem("Absolute");
        timeFormatComboBox.setBackground(UIFactory.getSecondaryBackgroundColor());
        timeFormatComboBox.setForeground(UIFactory.getTextColor());
        panel.add(timeFormatComboBox, gbc);
        
        // Auto-save setting
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        
        JLabel autoSaveLabel = new JLabel("Auto-save Captures:");
        autoSaveLabel.setForeground(UIFactory.getTextColor());
        panel.add(autoSaveLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        autoSaveCheckBox = new JCheckBox();
        autoSaveCheckBox.setSelected(true);
        autoSaveCheckBox.setBackground(UIFactory.getBackgroundColor());
        panel.add(autoSaveCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createCapturePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIFactory.getBackgroundColor());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Default interface setting
        // Default interface setting
        JLabel interfaceLabel = new JLabel("Default Interface:");
        interfaceLabel.setForeground(UIFactory.getTextColor());
        panel.add(interfaceLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        String[] interfaces = {"eth0", "wlan0", "lo", "Any"};
        interfaceComboBox = new JComboBox<>(interfaces);
        interfaceComboBox.setSelectedItem("Any");
        interfaceComboBox.setBackground(UIFactory.getSecondaryBackgroundColor());
        interfaceComboBox.setForeground(UIFactory.getTextColor());
        panel.add(interfaceComboBox, gbc);
        
        // Promiscuous mode setting
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        
        JLabel promiscuousLabel = new JLabel("Promiscuous Mode:");
        promiscuousLabel.setForeground(UIFactory.getTextColor());
        panel.add(promiscuousLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        promiscuousCheckBox = new JCheckBox();
        promiscuousCheckBox.setSelected(true);
        promiscuousCheckBox.setBackground(UIFactory.getBackgroundColor());
        panel.add(promiscuousCheckBox, gbc);
        
        // Buffer size setting
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        
        JLabel bufferSizeLabel = new JLabel("Buffer Size (MB):");
        bufferSizeLabel.setForeground(UIFactory.getTextColor());
        panel.add(bufferSizeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        bufferSizeSpinner = new JSpinner(new SpinnerNumberModel(64, 16, 1024, 16));
        bufferSizeSpinner.setBackground(UIFactory.getSecondaryBackgroundColor());
        bufferSizeSpinner.getEditor().getComponent(0).setBackground(UIFactory.getSecondaryBackgroundColor());
        bufferSizeSpinner.getEditor().getComponent(0).setForeground(UIFactory.getTextColor());
        panel.add(bufferSizeSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIFactory.getBackgroundColor());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Column display setting
        JLabel columnsLabel = new JLabel("Visible Columns:");
        columnsLabel.setForeground(UIFactory.getTextColor());
        panel.add(columnsLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        
        JPanel columnsPanel = new JPanel(new GridLayout(0, 2));
        columnsPanel.setBackground(UIFactory.getBackgroundColor());
        
        String[] columns = {"No.", "Time", "Source", "Destination", "Protocol", "Length", "Info"};
        columnCheckBoxes = new JCheckBox[columns.length];
        
        for (int i = 0; i < columns.length; i++) {
            columnCheckBoxes[i] = new JCheckBox(columns[i]);
            columnCheckBoxes[i].setSelected(true);
            columnCheckBoxes[i].setBackground(UIFactory.getBackgroundColor());
            columnCheckBoxes[i].setForeground(UIFactory.getTextColor());
            columnsPanel.add(columnCheckBoxes[i]);
        }
        
        panel.add(columnsPanel, gbc);
        
        // Name resolution settings
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        
        JLabel resolutionLabel = new JLabel("Name Resolution:");
        resolutionLabel.setForeground(UIFactory.getTextColor());
        panel.add(resolutionLabel, gbc);
        
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        
        JPanel resolutionPanel = new JPanel(new GridLayout(0, 1));
        resolutionPanel.setBackground(UIFactory.getBackgroundColor());
        
        macResolutionCheckBox = new JCheckBox("Resolve MAC Addresses");
        macResolutionCheckBox.setSelected(true);
        macResolutionCheckBox.setBackground(UIFactory.getBackgroundColor());
        macResolutionCheckBox.setForeground(UIFactory.getTextColor());
        
        networkResolutionCheckBox = new JCheckBox("Resolve Network Addresses");
        networkResolutionCheckBox.setSelected(true);
        networkResolutionCheckBox.setBackground(UIFactory.getBackgroundColor());
        networkResolutionCheckBox.setForeground(UIFactory.getTextColor());
        
        transportResolutionCheckBox = new JCheckBox("Resolve Transport Names");
        transportResolutionCheckBox.setSelected(true);
        transportResolutionCheckBox.setBackground(UIFactory.getBackgroundColor());
        transportResolutionCheckBox.setForeground(UIFactory.getTextColor());
        
        resolutionPanel.add(macResolutionCheckBox);
        resolutionPanel.add(networkResolutionCheckBox);
        resolutionPanel.add(transportResolutionCheckBox);
        
        panel.add(resolutionPanel, gbc);
        
        return panel;
    }
    
    private void saveSettings() {
        // Save theme settings
        String selectedTheme = (String) themeComboBox.getSelectedItem();
        if (selectedTheme.equals("Dark") && !UIFactory.isDarkMode()) {
            UIFactory.toggleTheme();
            SwingUtilities.updateComponentTreeUI(getOwner());
        } else if (selectedTheme.equals("Light") && UIFactory.isDarkMode()) {
            UIFactory.toggleTheme();
            SwingUtilities.updateComponentTreeUI(getOwner());
        }
        
        // In a real application, we would save all settings to a configuration file
        // For this demo, we'll just show a confirmation message
        JOptionPane.showMessageDialog(this,
            "Settings saved successfully.\n\n" +
            "Theme: " + selectedTheme + "\n" +
            "Font Size: " + fontSizeComboBox.getSelectedItem() + "\n" +
            "Time Format: " + timeFormatComboBox.getSelectedItem() + "\n" +
            "Auto-save: " + (autoSaveCheckBox.isSelected() ? "Enabled" : "Disabled") + "\n" +
            "Default Interface: " + interfaceComboBox.getSelectedItem() + "\n" +
            "Promiscuous Mode: " + (promiscuousCheckBox.isSelected() ? "Enabled" : "Disabled") + "\n" +
            "Buffer Size: " + bufferSizeSpinner.getValue() + " MB",
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
