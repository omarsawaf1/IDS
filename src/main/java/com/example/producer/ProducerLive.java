package com.example.producer;

import java.util.concurrent.BlockingQueue;

import com.example.PacketFactoryPattern.PacketReader;
import com.example.PacketFactoryPattern.PacketReaderFactory;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;
// import java.util.concurrent.*;

public class ProducerLive implements ProducerStrategy {
    private PacketReader packetReader;
    private volatile boolean producing = true;

    public ProducerLive(String networkInterfaceIp) throws Exception {
        this.packetReader =  PacketReaderFactory.createPacketReader("live", networkInterfaceIp);
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
