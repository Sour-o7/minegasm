package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.network.EventPayload;
import com.therainbowville.minegasm.network.ClientPayloadHandler;
import com.therainbowville.minegasm.network.ServerPayloadHandler;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = Minegasm.MOD_ID)
public class PayloadRegister {
    
    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
            EventPayload.TYPE,
            EventPayload.STREAM_CODEC,
            new DirectionalPayloadHandler<>(
                ClientPayloadHandler::handleEventPayload,
                ServerPayloadHandler::handleEventPayload
            )
        );
    }
}