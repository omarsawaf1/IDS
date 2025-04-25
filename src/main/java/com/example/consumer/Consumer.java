package com.example.consumer;
import java.util.concurrent.BlockingQueue;

import  com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.engine.EngineIds;
public class Consumer implements ConsumerStrategy {
    private boolean startFlag = false;
    public  void start(BlockingQueue<String> queue){
        startFlag=true;
        EngineIds engineIds = EngineIds.getInstance();
        while(startFlag){
            try {
                
                String data = queue.take();
                // do some compute with data
                engineIds.notifyObservers(data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public  void stop(){
        startFlag=false;
    }
}
