package com.therainbowville.minegasm.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPayloadHandler {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void handleEventPayload(final EventPayload data, final IPayloadContext context) {
        LOGGER.info("Payload Received on Server: " + data.eventType());
    }
}