package com.example;
import com.example.database.*;
import javax.swing.*;
import com.example.gui.*;
import com.example.database.ElasticSearch.*;
import com.example.*;
import com.example.concurrent.RuleQueue;
import com.example.database.mysql.*;
public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Invoke the WelcomeScreen using SwingUtilities.invokeLater
        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcomeScreen = new WelcomeScreen();
            welcomeScreen.setVisible(true);
        });
        
    }
}
