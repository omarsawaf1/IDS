package com.example.designpatterns.ObserverPattern;

import com.example.util.ParsedData;

public interface Subject {
    void addObserver(Observer observer);      // Add an observer
    void removeObserver(Observer observer);   // Remove an observer
    void notifyObservers(ParsedData data);        // Notify all observers of state change
}