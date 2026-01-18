package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.core.MinegasmGroupMember;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPayloadDispatcher {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
	
	public static void sendEventPayload(ServerPlayer player, String eventType, EventProcessor.EventData event) {
		PacketDistributor.sendToPlayer(player, new ClientboundEventPayload(eventType, event));
	}
	
    public static void sendProximityEventPayload(MinegasmGroup group, ServerPlayer source, String eventType, EventProcessor.EventData event) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        List<UUID> memberIDs = group.getPlayerUUIDs();
		
		final double radius = 50;
		
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (memberIDs.contains(player.getUUID()) && !player.getUUID().equals(source.getUUID())) {
				if (player.level().dimension() == source.level().dimension()) {
					double xOffset = source.getX() - player.getX();
					double yOffset = source.getY() - player.getY();
					double zOffset = source.getZ() - player.getZ();
					if (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset < 50 * 50) {
						sendEventPayload(player, eventType, event);
					}
				}
            }
		}
    }
	
	public static void sendGroupEventPayload(MinegasmGroup group, ServerPlayer exclude, String eventType, EventProcessor.EventData event) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        List<UUID> memberIDs = group.getPlayerUUIDs();
		
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (memberIDs.contains(player.getUUID()) && !player.getUUID().equals(exclude.getUUID())) {
                sendEventPayload(player, eventType, event);
            }
        }
	}
	
    public static void sendModifierPayload(MinegasmGroupMember member) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		ServerPlayer player = server.getPlayerList().getPlayer(member.uuid);
		if (player != null) {
			sendModifierPayload(player, member);
		}
    }
	
    public static void sendModifierPayload(ServerPlayer player, MinegasmGroupMember member) {
        PacketDistributor.sendToPlayer(player, new ClientboundModifierPayload(member.uuid, Optional.ofNullable(member.modifier)));
    }
	
    public static void sendGroupPayload(MinegasmGroup group) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        List<UUID> memberUUIDs = group.getPlayerUUIDs();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (memberUUIDs.contains(player.getUUID())) {
                PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(Optional.of(group)));                            
            }
        }
    }
    
    public static void sendGroupPayload(ServerPlayer player, MinegasmGroup group) {
        PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(Optional.ofNullable(group)));
    }
	
    public static void sendGroupPayload(MinegasmGroupMember member, MinegasmGroup group) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		ServerPlayer player = server.getPlayerList().getPlayer(member.uuid);
		
        PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(Optional.ofNullable(group)));
    }
	
    public static void sendRemovedGroupPayloads(MinegasmGroup group) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        List<UUID> memberUUIDs = group.getPlayerUUIDs();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (memberUUIDs.contains(player.getUUID())) {
                PacketDistributor.sendToPlayer(player, new ClientboundGroupPayload(Optional.ofNullable(null)));                            
            }
        }
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