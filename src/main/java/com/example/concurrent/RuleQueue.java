package com.example.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RuleQueue {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private RuleQueue() {} 

    public static BlockingQueue<String> getInstance() {
        return queue;
    }

}
