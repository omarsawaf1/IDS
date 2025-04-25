package com.example.designpatterns.StrategyPattern;

import java.util.concurrent.BlockingQueue;

public interface ConsumerStrategy {
    void start(BlockingQueue<String> queue);
    void stop();
}
