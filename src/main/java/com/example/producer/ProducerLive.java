package com.example.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

import com.example.PacketFactoryPattern.PacketReader;
import com.example.PacketFactoryPattern.PacketReaderFactory;
import com.example.designpatterns.StrategyPattern.ProducerStrategy;

public class ProducerLive implements ProducerStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ProducerLive.class);
    private PacketReader packetReader;
    private volatile boolean producing = true;

    public ProducerLive(String networkInterfaceIp) throws Exception {
        this.packetReader = PacketReaderFactory.createPacketReader("live", networkInterfaceIp);
        logger.info("Initialized ProducerLive with network interface IP: {}", networkInterfaceIp);
    }

    @Override
    public void start(BlockingQueue<String> queue) {
        logger.info("Starting packet production");
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
        logger.info("Packet production stopped");
    }

    @Override
    public void stop() {
        producing = false;
        logger.info("Stopping packet production");
        packetReader.close();
    }
}

