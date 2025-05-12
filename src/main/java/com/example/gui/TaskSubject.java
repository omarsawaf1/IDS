package com.example.gui;

import java.util.Observable;

/**
 * Subject class for the Observer pattern
 */
public class TaskSubject extends Observable {

    public void updateStatus(String status) {
        setChanged();
        notifyObservers(status);
    }
}
