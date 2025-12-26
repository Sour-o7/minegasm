package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import java.util.UUID;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPayloadDispatcher {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void sendEventPayload(String eventType, EventProcessor.EventData event) {
        PacketDistributor.sendToServer(new CommonEventPayload(eventType, event));
    }
    
    public static void sendJoinGroupPayload(UUID uuid) {
        sendJoinGroupPayload(uuid, Optional.empty());
    }
    
    public static void sendJoinGroupPayload(UUID uuid, Optional<String> password) {
        PacketDistributor.sendToServer(new ServerboundJoinGroupPayload(uuid, password));
    }
    
    public static void sendLeaveGroupPayload() {
        PacketDistributor.sendToServer(new ServerboundLeaveGroupPayload(MinegasmClient.getClientGroup().uuid));
    }
    
    public static void sendCreateGroupPayload(MinegasmGroup group) {
        PacketDistributor.sendToServer(new ServerboundCreateGroupPayload(group));
    }
    
    public static void sendUpdateGroupPayload(MinegasmGroup group) {
        PacketDistributor.sendToServer(new ServerboundUpdateGroupPayload(group));
    }
}