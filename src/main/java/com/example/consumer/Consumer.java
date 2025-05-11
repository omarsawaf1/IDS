package com.example.consumer;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import  com.example.designpatterns.StrategyPattern.ConsumerStrategy;
import com.example.engine.EngineIds;
import com.example.util.PacketRule;
import com.example.concurrent.*;
import com.example.database.mysql.*;
import com.example.util.PacketParser;
import com.example.database.*;
import com.example.database.ElasticSearch.ElasticsearchManager;
public class Consumer implements ConsumerStrategy {
    private boolean startFlag = false;

    public void start(BlockingQueue<String> queue) {
        startFlag = true;
        while (startFlag) {
            try {
                String data = queue.take(); // block until packet available

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
                e.printStackTrace();
            }
        }
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
    }
}
