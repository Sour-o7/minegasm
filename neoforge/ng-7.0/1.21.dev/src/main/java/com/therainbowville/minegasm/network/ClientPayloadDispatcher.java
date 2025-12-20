package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPayloadDispatcher {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void sendEventPayload(String eventType, EventProcessor.EventData event) {
        PacketDistributor.sendToServer(new EventPayload(eventType, event.getOrigin(), event.getIntensity(), event.getDuration()));
        LOGGER.info("Payload Sent to Server: " + eventType);
    }
}