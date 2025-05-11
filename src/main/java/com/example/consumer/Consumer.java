package com.example.consumer;

import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.engine.EngineIds;

public class Consumer implements ConsumerStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private boolean startFlag = false;

    public void start(BlockingQueue<String> queue) {
        startFlag = true;
        EngineIds engineIds = EngineIds.getInstance();
        logger.info("Consumer started.");
        while (startFlag) {
            try {
                String data = queue.take();
                // do some compute with data
                engineIds.notifyObservers(data);
                logger.debug("Processed data: {}", data);
            } catch (InterruptedException e) {
                logger.error("Consumer interrupted", e);
                // e.printStackTrace();
            }
        }
        logger.info("Consumer stopped.");
    }

    public void stop() {
        startFlag = false;
        logger.info("Consumer stop requested.");
    }
}

