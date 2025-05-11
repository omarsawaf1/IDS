package com.example.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.PacketFactoryPattern.PacketReader;
import com.example.PacketFactoryPattern.PacketReaderFactory;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;
// import java.util.concurrent.*;
import java.util.concurrent.BlockingQueue;

public class ProducerPcap implements ProducerStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ProducerPcap.class);

    private PacketReader packetReader;
    private volatile boolean producing = true;

    public ProducerPcap(String pcapFile) throws Exception {
        this.packetReader =  PacketReaderFactory.createPacketReader("offline", pcapFile);
    }

    @Override
    public void start(BlockingQueue<String> queue) {
        try {
            while (producing) {
                var packet = packetReader.getNextPacket();
                if (packet == null) break;
                logger.debug("Producing packet: {}", packet);
                queue.put(packet.toString());
            }
        } catch (InterruptedException e) {
            logger.error("InterruptedException while producing packets", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void stop() {
        producing = false;
        packetReader.close();
    }
}
