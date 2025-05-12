package com.example.engine;
import com.example.designpatterns.ProducerConsumer.ProducerConsumer;
import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import java.util.ArrayList;
import java.util.List;
import com.example.consumer.*;
import com.example.producer.*;
import com.example.util.*;
import com.example.concurrent.PoolManager;
import com.example.designpatterns.ObserverPattern.*;
//applaying singleton
public class EngineIds implements Subject {
    private static boolean engineFlagRunning = false;
    private static final EngineIds instance = new EngineIds();
    private ProducerConsumer producerConsumer;
    private List<Observer> observers = new ArrayList<>();

    private EngineIds() {}

    public static boolean isEngineRunning() {
        return engineFlagRunning;
    }


    public static EngineIds getInstance() {
        return instance;
    }

    public  void startEngine() {
        engineFlagRunning = true;
        try {
            this.producerConsumer = new ProducerConsumer(new Consumer() , new ProducerLive("172.0.0.1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PoolManager.EngineIds().submit(() -> producerConsumer.runConsumer());
        PoolManager.EngineIds().submit(() -> producerConsumer.runProducer());

    }
    public void startEngine(String filePath) {
        engineFlagRunning = true;
        try {
            producerConsumer = new ProducerConsumer(new Consumer(), new ProducerPcap(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PoolManager.EngineIds().submit(() -> producerConsumer.runConsumer());
        PoolManager.EngineIds().submit(() -> producerConsumer.runProducer());
    }

    public void stopEngine() {
        engineFlagRunning = false;
        producerConsumer.stopConsumer();
        producerConsumer.stopProducer();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(ParsedData data) {
        for (Observer observer : observers) {
            PoolManager.EngineIds().submit(() -> observer.update(data));
        }
    }
}