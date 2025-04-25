package com.example.designpatterns.ObserverPattern;

public interface Subject {
    void addObserver(Observer observer);      // Add an observer
    void removeObserver(Observer observer);   // Remove an observer
    void notifyObservers(String data);        // Notify all observers of state change
}