package com.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeScreen2 extends JFrame {
    
    private static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    private static final Color ACCENT_COLOR = new Color(180, 0, 0);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    
    public WelcomeScreen2() {
        setTitle("Network Analyzer - Welcome");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Set dark theme
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Network Analyzer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(ACCENT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Advanced Network Traffic Analysis Tool");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_COLOR);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        // Create option cards
        JPanel captureCard = createOptionCard("Packet Capture", 
                "Capture and analyze network traffic in real-time", 
                "Start Capture", e -> launchMainApplication());
        
        JPanel rulesCard = createOptionCard("Security Rules", 
                "Configure and manage security rules for network monitoring", 
                "Manage Rules", e -> showRulesDialog());
        
        JPanel settingsCard = createOptionCard("Settings", 
                "Configure application preferences and options", 
                "Open Settings", e -> showSettingsDialog());
        
        JPanel aboutCard = createOptionCard("About", 
                "Information about Network Analyzer and its features", 
                "View Details", e -> showAboutDialog());
        
        optionsPanel.add(captureCard);
        optionsPanel.add(rulesCard);
        optionsPanel.add(settingsCard);
        optionsPanel.add(aboutCard);
        
        // Create footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(30, 30, 30));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton exitButton = createStyledButton("Exit", new Color(60, 60, 60));
        exitButton.addActionListener(e -> System.exit(0));
        
        footerPanel.add(exitButton);
        
        // Add components to frame
        add(headerPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createOptionCard(String title, String description, String buttonText, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(30, 30, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html><div style='width:200px'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(new Color(180, 180, 180));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton actionButton = createStyledButton(buttonText, ACCENT_COLOR);
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionButton.addActionListener(action);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));
        card.add(actionButton);
        
        return card;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }
    
    private void launchMainApplication() {
        NetworkAnalyzer app = new NetworkAnalyzer();
        app.setVisible(true);
        this.dispose();
    }
    
    private void showRulesDialog() {
        RulesDialog dialog = new RulesDialog(this);
        dialog.setVisible(true);
    }
    
    private void showSettingsDialog() {
        SettingsDialog dialog = new SettingsDialog(this);
        dialog.setVisible(true);
    }
    
    private void showAboutDialog() {
        AboutDialog dialog = new AboutDialog(this);
        dialog.setVisible(true);
    }
}

