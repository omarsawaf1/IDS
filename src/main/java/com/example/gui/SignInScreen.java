package com.example.gui;

import javax.swing.*;
import java.awt.*;
import com.example.database.mysql.User;

public class SignInScreen extends JFrame {
    
    public SignInScreen() {
        setTitle("Sign In - Kaomi Intrusion Detection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.BLACK);
        setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.RED);
        add(userLabel, gbc);
        
        gbc.gridx = 1;
        JTextField userField = new JTextField(16);
        userField.setBackground(Color.DARK_GRAY);
        userField.setForeground(Color.WHITE);
        add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.RED);
        add(passLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(16);
        passField.setBackground(Color.DARK_GRAY);
        passField.setForeground(Color.WHITE);
        add(passField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton signInBtn = new JButton("Sign In");
        signInBtn.setBackground(Color.RED);
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setFocusPainted(false);
        signInBtn.setBorderPainted(false);
        signInBtn.setOpaque(true);
        signInBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(signInBtn, gbc);
        
        JLabel msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        gbc.gridy = 3;
        add(msgLabel, gbc);
        
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
                            
                            // Close this window
                            dispose();
                            // Open the welcome screen
                            SwingUtilities.invokeLater(() -> {
                            WelcomeScreen2 screen = new WelcomeScreen2();
                            screen.setVisible(true); // Make sure this is called
                             });
                            
                            // You can add additional actions here after successful login
                            // For example: load user preferences, set up session, etc.
                        } else {
                            // Failed login
                            msgLabel.setText("Invalid username or password.");
                            passField.setText(""); // Clear password field
                        }
                    } catch (Exception ex) {
                        msgLabel.setText("Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            };
            
            worker.execute();
        });
        
        setVisible(true);
    }
}
