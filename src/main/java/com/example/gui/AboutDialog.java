package com.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AboutDialog extends JDialog {
    
    public AboutDialog(JFrame parent) {
        super(parent, "About Network Analyzer", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(30, 30, 30));
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(30, 30, 30));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // App logo/icon placeholder
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(30, 30, 30));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoLabel = new JLabel("Network Analyzer");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 150, 200));
        logoPanel.add(logoLabel);
        
        // App information
        JLabel versionLabel = createInfoLabel("Version 1.0.0");
        JLabel buildLabel = createInfoLabel("Build 2023.10.15");
        JLabel copyrightLabel = createInfoLabel("© 2023 Network Analyzer Team");
        
        // Description
        JTextArea descArea = new JTextArea(
                "Network Analyzer is an advanced tool for capturing, analyzing, and " +
                "monitoring network traffic. It provides detailed packet inspection, " +
                "protocol analysis, and security monitoring capabilities."
        );
        descArea.setBackground(new Color(30, 30, 30));
        descArea.setForeground(Color.LIGHT_GRAY);
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(450, 100));
        
        // Links
        JLabel websiteLink = createLinkLabel("Visit Website", "https://www.networkanalyzer.example.com");
        JLabel docsLink = createLinkLabel("Documentation", "https://docs.networkanalyzer.example.com");
        JLabel supportLink = createLinkLabel("Support", "https://support.networkanalyzer.example.com");
        
        // Credits panel
        JPanel creditsPanel = new JPanel();
        creditsPanel.setLayout(new BoxLayout(creditsPanel, BoxLayout.Y_AXIS));
        creditsPanel.setBackground(new Color(40, 40, 40));
        creditsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        creditsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        creditsPanel.setMaximumSize(new Dimension(450, 120));
        
        JLabel creditsTitle = new JLabel("Credits");
        creditsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        creditsTitle.setForeground(Color.WHITE);
        creditsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextArea creditsText = new JTextArea(
                "This application uses the following libraries:\n" +
                "• Java Packet Capture (jpcap)\n" +
                "• Apache Commons\n" +
                "• Log4j\n" +
                "• Swing UI components"
        );
        creditsText.setBackground(new Color(40, 40, 40));
        creditsText.setForeground(Color.LIGHT_GRAY);
        creditsText.setFont(new Font("Arial", Font.PLAIN, 12));
        creditsText.setEditable(false);
        creditsText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        creditsPanel.add(creditsTitle);
        creditsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        creditsPanel.add(creditsText);
        
        // Close button
        JButton closeButton = UIFactory.createStyledButton("Close", new Color(60, 60, 60));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dispose());
        
        // Add components to content panel
             // Add components to content panel
        contentPanel.add(logoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(versionLabel);
        contentPanel.add(buildLabel);
        contentPanel.add(copyrightLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(descArea);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(websiteLink);
        contentPanel.add(docsLink);
        contentPanel.add(supportLink);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(creditsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(closeButton);
        
        // Add content panel to dialog
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
    
    private JLabel createLinkLabel(String text, String url) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(100, 150, 255));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add underline
        label.setText("<html><u>" + text + "</u></html>");
        
        // Add click handler
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            AboutDialog.this,
                            "Could not open URL: " + url,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        
        return label;
    }
}
