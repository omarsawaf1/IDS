package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class TaskObserver extends JLabel implements Consumer<String> {

    public TaskObserver() {
        setText("Waiting for task...");
        setForeground(Color.RED);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    @Override
    public void accept(String message) {
        // Ensure GUI updates happen on EDT
        SwingUtilities.invokeLater(() -> {
            setText(message);
        });
    }
}
