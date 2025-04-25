package com.example.designpatterns.StrategyPattern;
import java.util.concurrent.BlockingQueue;
public interface ProducerStrategy {
    void start(BlockingQueue<String> queue);  // Starts producing items and adding to the queue
    void stop();  // Stops the production process
}
