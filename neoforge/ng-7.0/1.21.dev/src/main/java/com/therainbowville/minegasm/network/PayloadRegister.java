package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.Minegasm;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
        
        // Client //
		
		// Clientbound Event Payload
        registrar.playToClient(
            ClientboundEventPayload.TYPE,
            ClientboundEventPayload.STREAM_CODEC,
            ClientPayloadHandler::handleEventPayload
        );
		
		// Clientbound Group Info Payload
        registrar.playToClient(
            ClientboundGroupInfoPayload.TYPE,
            ClientboundGroupInfoPayload.STREAM_CODEC,
            ClientPayloadHandler::handleGroupInfoPayload
        );
        
		// Clientbound Group Payload
        registrar.playToClient(
            ClientboundGroupPayload.TYPE,
            ClientboundGroupPayload.STREAM_CODEC,
            ClientPayloadHandler::handleGroupPayload
        );
		
		// Clientbound Modifier Payload
        registrar.playToClient(
            ClientboundModifierPayload.TYPE,
            ClientboundModifierPayload.STREAM_CODEC,
            ClientPayloadHandler::handleModifierPayload
        );
		
		// Clientbound Message Payload
        registrar.playToClient(
            ClientboundMessagePayload.TYPE,
            ClientboundMessagePayload.STREAM_CODEC,
            ClientPayloadHandler::handleMessagePayload
        );

        // Server //
		
		// Serverbound Event Payload
        registrar.playToServer(
            ServerboundEventPayload.TYPE,
            ServerboundEventPayload.STREAM_CODEC,
            ServerPayloadHandler::handleEventPayload
        );
		
		// Serverbound Create Group Payload
        registrar.playToServer(
            ServerboundCreateGroupPayload.TYPE,
            ServerboundCreateGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleCreateGroupPayload
        );
        
		// Serverbound Update Group Payload
        registrar.playToServer(
            ServerboundUpdateGroupPayload.TYPE,
            ServerboundUpdateGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleUpdateGroupPayload
        );
		
		// Serverbound Remove Group Payload
        registrar.playToServer(
            ServerboundRemoveGroupPayload.TYPE,
            ServerboundRemoveGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleRemoveGroupPayload
        );
        
		// Serverbound Update Group Member Payload
        registrar.playToServer(
            ServerboundUpdateGroupMemberPayload.TYPE,
            ServerboundUpdateGroupMemberPayload.STREAM_CODEC,
            ServerPayloadHandler::handleUpdateGroupMemberPayload
        );
		
		// Serverbound Request Modifier Payload
        registrar.playToServer(
            ServerboundRequestModifierPayload.TYPE,
            ServerboundRequestModifierPayload.STREAM_CODEC,
            ServerPayloadHandler::handleRequestModifierPayload
        );
		
		// Serverbound Update Modifier Payload
        registrar.playToServer(
            ServerboundUpdateModifierPayload.TYPE,
            ServerboundUpdateModifierPayload.STREAM_CODEC,
            ServerPayloadHandler::handleUpdateModifierPayload
        );

		// Serverbound Join Group Payload
        registrar.playToServer(
            ServerboundJoinGroupPayload.TYPE,
            ServerboundJoinGroupPayload.STREAM_CODEC,
            ServerPayloadHandler::handleJoinGroupPayload
        );
        
		// Serverbound Remove Group Member Payload
        registrar.playToServer(
            ServerboundRemoveGroupMemberPayload.TYPE,
            ServerboundRemoveGroupMemberPayload.STREAM_CODEC,
            ServerPayloadHandler::handleRemoveGroupMemberPayload
        );
    }
}