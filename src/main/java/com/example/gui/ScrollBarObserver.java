package com.example.gui;

import javax.swing.*;
import com.example.designpatterns.ObserverPattern.Observer;

public class ScrollBarObserver implements Observer {
    private JTextArea textArea;  // Text area to display the updated message
    private int counter = 0;
    public ScrollBarObserver(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public synchronized void update(String message) {
        //heavy code 

        try{
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // to not  update from background thread
        SwingUtilities.invokeLater(() ->{
            textArea.append(message + "\n");
            System.out.println(counter++);
            textArea.setCaretPosition(textArea.getDocument().getLength()); 
        });    
    }
}
