package com.example.designpatterns.ProducerConsumer;

import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProducerConsumer  {
    private ConsumerStrategy consumer;
    private ProducerStrategy producer;

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>(); 

    public ProducerConsumer(ConsumerStrategy consumer , ProducerStrategy producer) {
        this.consumer = consumer;
        this.producer = producer;
    }
    public void runConsumer() {
        consumer.start(queue);
    }

    public void runProducer() {
        producer.start(queue);
    }

    public void stopConsumer() {
        consumer.stop();
    }

    public void stopProducer() {
        producer.stop();
    }
}
