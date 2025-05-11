package com.example.concurrent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


//over engineering 
import com.example.util.*;
import com.example.database.mysql.*;
public class RuleQueue {
    private static final Map<Integer, PacketRule> queueRules = new ConcurrentHashMap<>();
    private static int  count=0;
    private RuleQueue() {}
    public static Map<Integer, PacketRule> getQueueRules() {
        return queueRules;
    }
    public static int queueLoadRulesList() {
        if(User.getUserId()>0){
            Rules rules = new Rules();
            List<Map<String, Object>> rulesList = rules.searchUserRulesList(User.getUserId());
            for (Map<String, Object> rule : rulesList) {
                System.out.println(rule);
                String protocol = (String) rule.get("protocol");
                String srcIP = (String) rule.get("srcIP");
                String dstPort = (String) rule.get("dstPort");
                String dstMac = (String) rule.get("dstMac");
                String srcMac = (String) rule.get("srcMac");
                String srcPort = (String) rule.get("srcPort");
                String dstIP = (String) rule.get("dstIP");
                Integer ruleId = (Integer) rule.get("rulesid");
                String[] rawRuleMap = {protocol, srcMac, srcIP, srcPort, dstMac, dstIP, dstPort};
                queueRules.put(ruleId, new PacketRule(ruleId, rawRuleMap));
                count++;
            }
        }

        return count;
    }
    public static int addRuleToQueue( String[] rawRuleMap) {
      Rules rules = new Rules();
      User user = new User();
     // (protocol, srcIP, dstPort, dstMac, srcMac, srcPort, dstIP, userid)
      int ruleId = rules.insert(rawRuleMap[0], rawRuleMap[1], rawRuleMap[2], rawRuleMap[3], rawRuleMap[4], rawRuleMap[5], rawRuleMap[6],user.getUserId()); 
      queueRules.put(ruleId, new PacketRule(ruleId, rawRuleMap));
      count++;
      return ruleId;
    }

    public static boolean removeRuleFromQueueById(int ruleId) {
        Rules rules = new Rules();
        rules.delete(ruleId);
        if (count>0) {
            count--;
        }
        return queueRules.remove(ruleId) != null;
    }

}
