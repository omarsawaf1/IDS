package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.example.database.mysql.User;

public class SignInScreen extends JFrame {
    
    private JPanel personPanel;
    private Timer animationTimer;
    private int animationState = 0;
    private boolean loginSuccess = false;
    
    public SignInScreen() {
        setTitle("Sign In - Kaomi Intrusion Detection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        
        // Create main panel for login controls
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.BLACK);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.RED);
        mainPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        JTextField userField = new JTextField(16);
        userField.setBackground(Color.DARK_GRAY);
        userField.setForeground(Color.WHITE);
        mainPanel.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.RED);
        mainPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(16);
        passField.setBackground(Color.DARK_GRAY);
        passField.setForeground(Color.WHITE);
        mainPanel.add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton signInBtn = new JButton("Sign In");
        signInBtn.setBackground(Color.RED);
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setFocusPainted(false);
        signInBtn.setBorderPainted(false);
        signInBtn.setOpaque(true);
        signInBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        mainPanel.add(signInBtn, gbc);
        
        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        gbc.gridy = 3;
        mainPanel.add(msgLabel, gbc);
        
        // Add main panel to the top of the frame
        add(mainPanel, BorderLayout.NORTH);
        
        // Create person panel for animation
        personPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPerson(g, animationState, loginSuccess);
            }
        };
        personPanel.setBackground(Color.BLACK);
        personPanel.setPreferredSize(new Dimension(400, 150));
        add(personPanel, BorderLayout.CENTER);
        
        // Create User object for database operations
        User userDB = new User();
        
        signInBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            
            // Show a "logging in" message
            msgLabel.setText("Logging in...");
            
            // Use SwingWorker to perform database operation in background
            SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    // Call the login method from User class
                    return userDB.login(username, password);
                }
                
                @Override
                protected void done() {
                    try {
                        int userId = get();
                        if (userId > 0) {
                            // Successful login
                            msgLabel.setText("Login successful!");
                            loginSuccess = true;
                            
                            //  thumbs up animation
                            startAnimation(true);
                            
                            // Delay opening the next screen until animation completes
                            Timer delayTimer = new Timer(2000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    dispose();
                                    // Open the welcome screen
                                    SwingUtilities.invokeLater(() -> {
                                        WelcomeScreen2 screen = new WelcomeScreen2();
                                        screen.setVisible(true);
                                    });
                                }
                            });
                            delayTimer.setRepeats(false);
                            delayTimer.start();
                            
                        } else {
                            // Failed login
                            msgLabel.setText("Invalid username or password.");
                            passField.setText(""); // Clear password field
                            loginSuccess = false;
                            
                            // Start animation
                            startAnimation(false);
                        }
                    } catch (Exception ex) {
                        msgLabel.setText("Error: " + ex.getMessage());
                        ex.printStackTrace();
                        loginSuccess = false;
                        
                        // Start  animation
                        startAnimation(false);
                    }
                }
            };
            
            worker.execute();
        });
        
        setVisible(true);
    }
    
    private void startAnimation(boolean success) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        
        loginSuccess = success;
        animationState = 0;
        animationTimer = new Timer(150, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationState++;
                if (success && animationState > 5) {
                    animationState = 5; // Stay at thumbs up position
                    animationTimer.stop();
                } else if (!success && animationState > 3) {
                    animationState = 3; // Stay at crossed arms position
                    animationTimer.stop();
                }
                personPanel.repaint();
            }
        });
        animationTimer.start();
    }
    
    private void drawPerson(Graphics g, int state, boolean success) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = personPanel.getWidth() / 2;
        int baseY = personPanel.getHeight() - 30;
        
        // Set color for the person
        g2d.setColor(Color.RED);
        
        // Draw head
        g2d.fillOval(centerX - 15, baseY - 100, 30, 30);
        
        // Draw body
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(centerX, baseY - 70, centerX, baseY - 30);
        
        // Draw legs
        g2d.drawLine(centerX, baseY - 30, centerX - 15, baseY);
        g2d.drawLine(centerX, baseY - 30, centerX + 15, baseY);
        
        if (success) {
            // Draw successful animation (thumbs up)
            drawSuccessAnimation(g2d, centerX, baseY, state);
        } else {
            // Draw failure animation (crossed arms)
            drawFailureAnimation(g2d, centerX, baseY, state);
        }
    }
    
    private void drawSuccessAnimation(Graphics2D g2d, int centerX, int baseY, int state) {
        switch (state) {
            case 0: // Initial position - arms down
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 20, baseY - 40);
                
                // Neutral face
                g2d.setColor(Color.WHITE);
                g2d.drawLine(centerX - 8, baseY - 90, centerX + 8, baseY - 90);
                break;
                
            case 1: // Start raising right arm
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 15, baseY - 45);
                
                // Slight smile
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 8, baseY - 95, 16, 10, 0, -180);
                break;
                
            case 2: // Continue raising right arm
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 15, baseY - 50);
                
                // Bigger smile
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 10, baseY - 95, 20, 12, 0, -180);
                break;
                
            case 3: // Right arm horizontal
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 20, baseY - 60);
                
                // Bigger smile
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 10, baseY - 95, 20, 12, 0, -180);
                break;
                
            case 4: // Right arm up at angle
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 15, baseY - 75);
                
                // Happy face
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 10, baseY - 95, 20, 15, 0, -180);
                
                // Draw eyes
                g2d.fillOval(centerX - 8, baseY - 98, 4, 4);
                g2d.fillOval(centerX + 4, baseY - 98, 4, 4);
                break;
                
            case 5: // Thumbs up position
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                
                //  arm up
                g2d.drawLine(centerX, baseY - 60, centerX + 10, baseY - 80);
                
                // better thumbs up
                g2d.setStroke(new BasicStroke(6));
                g2d.drawLine(centerX + 10, baseY - 80, centerX + 10, baseY - 95);
                
                //  hand/fist
                g2d.setColor(Color.RED);
                g2d.fillOval(centerX + 5, baseY - 80, 12, 15);
                
                //  big smile and eyes
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawArc(centerX - 10, baseY - 95, 20, 15, 0, -180);
                g2d.fillOval(centerX - 8, baseY - 98, 4, 4);
                g2d.fillOval(centerX + 4, baseY - 98, 4, 4);
                break;
        }
    }
    
    private void drawFailureAnimation(Graphics2D g2d, int centerX, int baseY, int state) {
        switch (state) {
            case 0: // Initial position - arms down
                g2d.drawLine(centerX, baseY - 60, centerX - 20, baseY - 40);
                g2d.drawLine(centerX, baseY - 60, centerX + 20, baseY - 40);
                
                // Neutral face
                g2d.setColor(Color.WHITE);
                g2d.drawLine(centerX - 8, baseY - 90, centerX + 8, baseY - 90);
                break;
                
            case 1: // Start moving arms
                g2d.drawLine(centerX, baseY - 60, centerX - 15, baseY - 50);
                g2d.drawLine(centerX, baseY - 60, centerX + 15, baseY - 50);
                
                // Slight frown
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 8, baseY - 85, 16, 10, 0, 180);
                break;
                
            case 2: // Continue moving arms
                g2d.drawLine(centerX, baseY - 60, centerX - 10, baseY - 55);
                g2d.drawLine(centerX, baseY - 60, centerX + 10, baseY - 55);
                
                // Bigger frown
                g2d.setColor(Color.WHITE);
                g2d.drawArc(centerX - 10, baseY - 85, 20, 12, 0, 180);
                break;
                
            case 3: // Crossed arms
                
                g2d.setColor(Color.RED);
                g2d.drawLine(centerX, baseY - 60, centerX + 15, baseY - 50);
                g2d.drawLine(centerX, baseY - 60, centerX - 15, baseY - 50);
                
                // Draw crossed part
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(centerX - 5, baseY - 55, centerX + 5, baseY - 55);
                
                // Upset face
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawArc(centerX - 10, baseY - 85, 20, 15, 0, 180);
                
                // Draw angry eyes (slanted down)
                g2d.drawLine(centerX - 10, baseY - 95, centerX - 5, baseY - 93);
                g2d.drawLine(centerX + 5, baseY - 93, centerX + 10, baseY - 95);
                break;
        }
    }
}
