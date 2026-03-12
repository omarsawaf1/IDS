package com.example.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomeScreen2 extends JFrame {

    private static final Color PRIMARY_BACKGROUND = new Color(0x0D1117);
    private static final Color CONTAINER_BACKGROUND = new Color(0x161B22);
    private static final Color ACCENT_BLUE = new Color(0x3B82F6);
    private static final Color TEXT_COLOR = new Color(0xF5F5F5);
    private static final Color SUB_TEXT_COLOR = new Color(0xC9D1D9);
    private static final Color CARD_BORDER = new Color(0x2D333B);

    public WelcomeScreen2() {
        setTitle("Network Analyzer - Welcome");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        getContentPane().setBackground(PRIMARY_BACKGROUND);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 20, 30));

        JLabel titleLabel = new JLabel("Network Analyzer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(ACCENT_BLUE);

        JLabel subtitleLabel = new JLabel("Advanced Network Traffic Analysis Tool");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(TEXT_COLOR);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        optionsPanel.setBackground(PRIMARY_BACKGROUND);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

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

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(CONTAINER_BACKGROUND);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton exitButton = createStyledButton("Exit", CARD_BORDER);
        exitButton.addActionListener(e -> System.exit(0));

        footerPanel.add(exitButton);

        add(headerPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createOptionCard(String title, String description, String buttonText, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CONTAINER_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='width:200px'>" + description + "</div></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(SUB_TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton actionButton = createStyledButton(buttonText, ACCENT_BLUE);
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

