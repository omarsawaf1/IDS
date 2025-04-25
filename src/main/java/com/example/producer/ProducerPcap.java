package com.example.producer;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;
import java.util.concurrent.*;

public class ProducerPcap implements ProducerStrategy {
    private OfflinePacketReader packetReader;
    private volatile boolean producing = true;

    public ProducerPcap(String pcapFile) throws Exception {
        this.packetReader = new OfflinePacketReader(pcapFile);
    }

    @Override
    public void start(BlockingQueue<String> queue) {
        try {
            while (producing) {
                var packet = packetReader.getNextPacket();
                if (packet == null) break;
                queue.put(packet.toString());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        producing = false;
        packetReader.close();
    }
}