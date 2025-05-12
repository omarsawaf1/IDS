package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Adapter class that maintains compatibility with the old Observer pattern
 * while using the new functional approach internally
 */
public class TaskObserverAdapter extends JLabel implements Observer {

    public TaskObserverAdapter() {
        setText("Waiting for task...");
        setForeground(Color.RED);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            // Ensure GUI updates happen on EDT
            SwingUtilities.invokeLater(() -> {
                setText((String) arg);
            });
        }
    }
}
