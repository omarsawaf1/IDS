package com.example.consumer;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import com.example.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.engine.EngineIds;
import com.example.database.mysql.User;
import com.example.util.*;
import com.example.concurrent.RuleQueue;
import com.example.database.mysql.Alerts;
import com.example.util.PacketParser;
import com.example.concurrent.PoolManager;
import co.elastic.clients.elasticsearch.nodes.Pool;

import com.example.database.ElasticSearch.ElasticsearchManager;

public class Consumer implements ConsumerStrategy {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private boolean startFlag = false;

    @Override
    public void start(BlockingQueue<String> queue) {
        startFlag = true;
        logger.info("Consumer started.");
        Integer ruleId = -1;
        PacketRule rule;
        int count = 0;
        while (startFlag) {
            try {
                String data = queue.take(); // block until packet available
                boolean alert = false;
                logger.debug("Processed data: {}", data);
                Map<Integer, PacketRule> queueRules = RuleQueue.getQueueRules();
                // Map<String, String> parsed = PacketParser.parsePacket(data);
                for (Map.Entry<Integer, PacketRule> entry : queueRules.entrySet()) {
                    ruleId = entry.getKey();
                    rule = entry.getValue();

                    if (rule.matches(data)) {
                        alert = true;
                        break;
                    }
                }
                compute(data, ruleId, alert);
                System.out.println(count++);
            } catch (InterruptedException e) {
                logger.error("Consumer interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Consumer stopped.");
    }

    public void compute(String data, Integer ruleId, boolean alertflag) {
        User user = new User();
        EngineIds engineIds = EngineIds.getInstance();
        Alerts alert = new Alerts();
        // if(alertflag){
        //     alert.insert(
        //             parsed.get("protocol"),
        //             parsed.get("srcMac"),
        //             parsed.get("srcIp"),
        //             parsed.get("srcPort"),
        //             parsed.get("dstMac"),
        //             parsed.get("dstIp"),
        //             parsed.get("dstPort"),
        //             user.getUserId(),
        //             ruleId
        //         );
        // }

        // ElasticsearchManager obj = new ElasticsearchManager();
        // obj.indexUserPacket(user.getUserId(), data);
        ParsedData parsedData = new ParsedData(data, alertflag, ruleId);
        engineIds.notifyObservers(parsedData);
    }

    @Override
    public void stop() {
        startFlag = false;
        logger.info("Consumer stop requested.");
    }
}

