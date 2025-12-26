package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.common.MinegasmServer;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerPayloadHandler {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void handleEventPayload(final CommonEventPayload data, final IPayloadContext context) {
        //LOGGER.info("Payload Received on Server: " + data.eventType());
    }
    
    public static void handleCreateGroupPayload(final ServerboundCreateGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = data.group();
        group.addPlayer(new MinegasmGroupMember(context.player().getUUID(), context.player().getName().getString(), MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRole.SWITCH));
        MinegasmServer.updateGroup(group);
        
        ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) context.player(), group);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
    
    public static void handleUpdateGroupPayload(final ServerboundUpdateGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group().uuid);
        if (group == null) { return; }

        MinegasmGroupMember player = group.getPlayer(context.player().getUUID());

        if (player != null /*&& player.rank == MinegasmGroupMember.PlayerRank.LEADER*/) {
            group.name = data.group().name;
            group.password = data.group().password;
            group.config.copyFrom(data.group().config);
            MinegasmServer.updateGroup(group);
        } else {
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
        }
        
        ServerPayloadDispatcher.sendGroupPayload(group);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
    
    public static void handleJoinGroupPayload(final ServerboundJoinGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.uuid());
        if (group == null) { return; }
        
        if (group.isPrivate == false || (data.password().isPresent() && group.password.equals(data.password().get()))) {
            group.addPlayer(new MinegasmGroupMember(context.player().getUUID(), context.player().getName().getString(), MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRole.SWITCH));
            //group.addPlayer(new MinegasmGroupMember(context.player().getUUID(), context.player().getName().getString()));
            ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) context.player(), group);
        } else { 
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INCORRECT_PASSWORD);
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
    
    public static void handleLeaveGroupPayload(final ServerboundLeaveGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.uuid());
        if (group != null) {
            group.removePlayer(context.player().getUUID());
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
}