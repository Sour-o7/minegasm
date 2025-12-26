package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.network.ClientPayloadHandler;
import com.therainbowville.minegasm.network.ServerPayloadHandler;
import com.therainbowville.minegasm.network.CommonEventPayload;
import com.therainbowville.minegasm.network.ClientboundGroupInfoPayload;
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
        
        // Common
        registrar.playBidirectional(
            CommonEventPayload.TYPE,
            CommonEventPayload.STREAM_CODEC,
            new DirectionalPayloadHandler<>(
                ClientPayloadHandler::handleEventPayload,
                ServerPayloadHandler::handleEventPayload
            )
        );
        
        // Client
        registrar.playToClient(
            ClientboundGroupInfoPayload.TYPE,
            ClientboundGroupInfoPayload.STREAM_CODEC,
            ClientPayloadHandler::handleGroupInfoPayload
        );
        
        registrar.playToClient(
            ClientboundGroupPayload.TYPE,
            ClientboundGroupPayload.STREAM_CODEC,
            ClientPayloadHandler::handleGroupPayload
        );
        
        registrar.playToClient(
            ClientboundMessagePayload.TYPE,
            ClientboundMessagePayload.STREAM_CODEC,
            ClientPayloadHandler::handleMessagePayload
        );

        // Server
        registrar.playToServer(
            ServerboundCreateGroupPayload.TYPE,
            ServerboundCreateGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleCreateGroupPayload
        );
        
        registrar.playToServer(
            ServerboundUpdateGroupPayload.TYPE,
            ServerboundUpdateGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleUpdateGroupPayload
        );

        registrar.playToServer(
            ServerboundJoinGroupPayload.TYPE,
            ServerboundJoinGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleJoinGroupPayload
        );
        
        registrar.playToServer(
            ServerboundLeaveGroupPayload.TYPE,
            ServerboundLeaveGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleLeaveGroupPayload
        );
    }
}