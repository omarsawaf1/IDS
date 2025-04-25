package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.example.concurrent.PoolManager;
import com.example.engine.EngineIds;

public class Gui {
    private JTextField textField;
    private JTextArea textArea;
    private JLabel label;
    private JButton buttonStart;
    private JButton buttonExist;
    private JButton buttonChangeColor;
    private JTextField rightTextField;
    private JButton buttonAddText;

    public void createAndShowGUI() {
        // Create the main window
        JFrame frame = new JFrame("Simple Swing Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // Create panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create label (green by default)
        label = new JLabel("Label: Green");
        label.setForeground(Color.GREEN);

        // Create text area with a scroll bar
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false); // To prevent editing directly
        JScrollPane scrollPane = new JScrollPane(textArea); // Add JScrollPane

        ScrollBarObserver scrollPanel = new ScrollBarObserver(textArea);
        EngineIds engineIds = EngineIds.getInstance();
        engineIds.addObserver(scrollPanel);

        // Buttons
        buttonStart = new JButton("Start Action");
        buttonExist = new JButton("Exist Action");
        buttonChangeColor = new JButton("Change Label Color");

        // Action listener for "Start Action" button
        buttonStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!engineIds.isEngineRunning()) {
                    engineIds.startEngine("C:\\Users\\ahmed\\OneDrive\\Desktop\\information\\college\\second term year 3\\oop\\learning\\capture.pcapng");
                }
            }
        });

        // Action listener for "Exist Action" button
        buttonExist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PoolManager.shutdownAll();
                engineIds.stopEngine();
                frame.dispose();
            }
        });

        // Action listener for "Change Label Color" button
        buttonChangeColor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Change label color from green to red
                if (label.getForeground() == Color.GREEN) {
                    label.setText("Label: Red");
                    label.setForeground(Color.RED);
                } else {
                    label.setText("Label: Green");
                    label.setForeground(Color.GREEN);
                }
            }
        });

        // Panel for buttons (bottom of the window)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // To align buttons horizontally
        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonExist);
        buttonPanel.add(buttonChangeColor);

        // Create the new text field and button for the right side
        rightTextField = new JTextField(15);
        buttonAddText = new JButton("Add Text");

        buttonAddText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = rightTextField.getText();
                if (!text.isEmpty()) {
                    textArea.append(text + "\n"); // Add the text to the text area
                    rightTextField.setText(""); // Clear the text field
                }
            }
        });

        // Panel for the right side components (text field + button)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // Vertical layout
        rightPanel.add(rightTextField);
        rightPanel.add(buttonAddText);

        // Use JSplitPane to split the panel into two parts
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, rightPanel);
        splitPane.setDividerLocation(350); // Set the initial divider position
        splitPane.setDividerSize(5); // Set the divider size for better aesthetics

        // Add components to the main panel
        panel.add(label, BorderLayout.NORTH); // Label on top-left
        panel.add(scrollPane, BorderLayout.CENTER); // Text area and scrollbar on the center
        panel.add(buttonPanel, BorderLayout.SOUTH); // Buttons below the text area

        // Add splitPane to the frame
        frame.add(splitPane);

        // Make the window visible
        frame.setVisible(true);
    }
}
