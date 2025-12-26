package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPayloadDispatcher {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void sendEventPayload(String eventType, EventProcessor.EventData event) {
        PacketDistributor.sendToAllPlayers(new CommonEventPayload(eventType, event));
        //PacketDistributor.sendToPlayer(player, new EventPayload(eventType, event));
        //PacketDistributor.sendToPlayersNear(player, new EventPayload(eventType, event));
    }
    
    public static void sendGroupPayload(MinegasmGroup group) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        List<UUID> memberUUID = group.getPlayerUUIDs();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (memberUUID.contains(player.getUUID())) {
                PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(group));                            
            }
        }
    }
    
    public static void sendGroupPayload(ServerPlayer player, MinegasmGroup group) {
        PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(group));
    }
    
    public static void sendGroupInfoPayload(List<MinegasmGroupInfo> groupInfo) {
        PacketDistributor.sendToAllPlayers(new ClientboundGroupInfoPayload(groupInfo));
    }
    
    public static void sendGroupInfoPayload(ServerPlayer player, List<MinegasmGroupInfo> groupInfo) {
        PacketDistributor.sendToPlayer(player, new ClientboundGroupInfoPayload(groupInfo));
    }
    
    public static void sendMessagePayload(ServerPlayer player, ServerMessage message) {
        PacketDistributor.sendToPlayer(player, new ClientboundMessagePayload(message.toInt()));
    }
}