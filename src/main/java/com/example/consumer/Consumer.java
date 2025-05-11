package com.example.consumer;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.engine.EngineIds;

import com.example.util.PacketRule;
import com.example.concurrent.*;
import com.example.database.mysql.*;
import com.example.util.PacketParser;
import com.example.database.ElasticSearch.ElasticsearchManager;
public class Consumer implements ConsumerStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private boolean startFlag = false;

    public void start(BlockingQueue<String> queue) {
        startFlag = true;
        logger.info("Consumer started.");
        while (startFlag) {
            try {
                String data = queue.take(); // block until packet available

                logger.debug("Processed data: {}", data);
                Map<Integer, PacketRule> queueRules = RuleQueue.getQueueRules();
                Map<String, String> parsed = PacketParser.parsePacket(data);

                if (parsed == null) continue; // skip invalid packets
                for (Map.Entry<Integer, PacketRule> entry : queueRules.entrySet()) {
                    Integer ruleId = entry.getKey();
                    PacketRule rule = entry.getValue();

                    if (rule.matches(data)) {
                        this.compute(data, parsed, ruleId);
                        break; 
                    }
                }

            } catch (InterruptedException e) {
                logger.error("Consumer interrupted", e);
                // e.printStackTrace();
            }
        }
        logger.info("Consumer stopped.");
    }

    public  void compute (String data ,Map<String, String> parsed,int ruleId){
        User user = new User();
        EngineIds engineIds = EngineIds.getInstance();
        Alerts alert = new Alerts();
        alert.insert(
            parsed.get("protocol"),
            parsed.get("srcMac"),
            parsed.get("srcIp"),
            parsed.get("srcPort"),
            parsed.get("dstMac"),
            parsed.get("dstIp"),
            parsed.get("dstPort"),
            user.getUserId(), 
            ruleId
        );
        ElasticsearchManager obj = new ElasticsearchManager();
        obj.indexUserPacket(user.getUserId(), data);
        engineIds.notifyObservers(data);
    }
    public  void stop(){
        startFlag=false;
        logger.info("Consumer stop requested.");
    }
}

