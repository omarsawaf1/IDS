package com.example.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class PoolManager {
    // 4 threads for DB
    private static final ExecutorService EngineIds =
    Executors.newFixedThreadPool(5);

    // 4 threads for DB
    private static final ExecutorService dbPool =
        Executors.newFixedThreadPool(4);

    // 4 threads for GUI helpers
    private static final ExecutorService guiPool =
        Executors.newFixedThreadPool(4);

    // 4 threads for rule processing
    private static final ExecutorService rulePool =
        Executors.newFixedThreadPool(4);

    private PoolManager() {} // no instances
    public static ExecutorService EngineIds()     { return EngineIds;     }
    public static ExecutorService dbPool()        { return dbPool;        }
    public static ExecutorService guiPool()       { return guiPool;       }
    public static ExecutorService rulePool()      { return rulePool;      }

    public static void shutdownAll() {
        try {

            dbPool.shutdown();
            guiPool.shutdown();
            rulePool.shutdown();
            EngineIds.shutdown();
            if(!EngineIds.awaitTermination(5, TimeUnit.SECONDS)) {
                EngineIds.shutdownNow();
            }
            if (!dbPool.awaitTermination(5, TimeUnit.SECONDS)) {
                dbPool.shutdownNow();
            }
            if (!guiPool.awaitTermination(5, TimeUnit.SECONDS)) {
                guiPool.shutdownNow();
            }
            if (!rulePool.awaitTermination(5, TimeUnit.SECONDS)) {
                rulePool.shutdownNow();
            }
            //clear interrupt flag catch do that 
        } catch (InterruptedException e) {
            dbPool.shutdownNow();
            guiPool.shutdownNow();
            EngineIds.shutdownNow();
            rulePool.shutdownNow();
            //we need to throw it again to make the interrupt
            Thread.currentThread().interrupt();
        }
    }
}
