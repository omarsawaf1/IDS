package com.example.designpatterns.ProducerConsumer;

import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerConsumer  {
    private static final Logger logger = LoggerFactory.getLogger(ProducerConsumer.class);
    private ConsumerStrategy consumer;
    private ProducerStrategy producer;

    private BlockingQueue<String> queue = new LinkedBlockingQueue<>(); 

    public ProducerConsumer(ConsumerStrategy consumer , ProducerStrategy producer) {
        this.consumer = consumer;
        this.producer = producer;
    }
    public void runConsumer() {
        logger.info("Starting consumer");
        consumer.start(queue);
    }

    public void runProducer() {
        logger.info("Starting producer");
        producer.start(queue);
    }

    public void stopConsumer() {
        logger.info("Stopping consumer");
        consumer.stop();
    }

    public void stopProducer() {
        logger.info("Stopping producer");
        producer.stop();
    }
}

