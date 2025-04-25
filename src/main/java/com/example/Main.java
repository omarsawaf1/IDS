package com.example;

import javax.swing.*;
import com.example.gui.*;

public class Main {
    public static void main(String[] args) {
        // Correctly call createAndShowGUI
        SwingUtilities.invokeLater(() -> new Gui().createAndShowGUI());
    }
}