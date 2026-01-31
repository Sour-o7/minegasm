package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigGroup;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.common.MinegasmServer;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ServerPayloadHandler {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void handleEventPayload(final ServerboundEventPayload data, final IPayloadContext context) {
		MinegasmGroup group = MinegasmServer.getGroup(data.group());
		MinegasmConfigGroup.EventConfig config = (MinegasmConfigGroup.EventConfig) group.config.getModeConfig(data.eventType());
		
		if (config.type == MinegasmConfig.TriggerType.SHARED) {
			if (config.proximityEnabled) {
				ServerPayloadDispatcher.sendProximityEventPayload(group, (ServerPlayer) context.player(), data.eventType(), data.event());
			} else {
				ServerPayloadDispatcher.sendGroupEventPayload(group, (ServerPlayer) context.player(), data.eventType(), data.event());
			}
		}
    }
    
    public static void handleCreateGroupPayload(final ServerboundCreateGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = data.group();
        group.updatePlayer(new MinegasmGroupMember(context.player().getUUID(), context.player().getName().getString(), MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRole.DISABLE));
        MinegasmServer.updateGroup(group);
        
        ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) context.player(), group);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
    
    public static void handleUpdateGroupPayload(final ServerboundUpdateGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group().uuid);
        if (group == null) { return; }

        MinegasmGroupMember player = group.getPlayer(context.player().getUUID());

        if (player != null && player.rank == MinegasmGroupMember.PlayerRank.LEADER) {
            group.name = data.group().name;
            group.password = data.group().password;
            group.config.copyFrom(data.group().config);
            MinegasmServer.updateGroup(group);
        } else {
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
            return;
        }
        
        ServerPayloadDispatcher.sendGroupPayload(group);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
	
    public static void handleRemoveGroupPayload(final ServerboundRemoveGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group());
        if (group == null) { return; }

        MinegasmGroupMember player = group.getPlayer(context.player().getUUID());

        if (player != null && player.rank == MinegasmGroupMember.PlayerRank.LEADER) {
			MinegasmServer.removeGroup(data.group());
			ServerPayloadDispatcher.sendRemovedGroupPayloads(group);
        } else {
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
            return;
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }

    public static void handleUpdateGroupMemberPayload(final ServerboundUpdateGroupMemberPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group());
        if (group == null) { return; }

        MinegasmGroupMember author = group.getPlayer(context.player().getUUID());
        MinegasmGroupMember received = data.updated();
        MinegasmGroupMember old = group.getPlayer(received.uuid);
		
		if (author == null || old == null) { return; }
        
        if (!old.rank.equals(received.rank)) {
            if (author.rank == MinegasmGroupMember.PlayerRank.LEADER) {
                MinegasmGroupMember updated = group.getPlayer(received.uuid);
                updated.rank = received.rank;
                if (updated.rank == MinegasmGroupMember.PlayerRank.LEADER) {
                    author.rank = MinegasmGroupMember.PlayerRank.SUBLEADER;
                    group.updatePlayer(author);
                }
                group.updatePlayer(updated);
            } else {
                ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
                return;
            }
        }
        
        if (!old.role.equals(received.role)) {
            if (group.config.forcedRoles) {
                if (author.rank == MinegasmGroupMember.PlayerRank.LEADER || author.rank == MinegasmGroupMember.PlayerRank.SUBLEADER) {
                    MinegasmGroupMember updated = group.getPlayer(received.uuid);
                    updated.role = received.role;
                    group.updatePlayer(updated);
                } else {
                    ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
                    return;
                }
            } else if (author.uuid.equals(received.uuid)) {
                MinegasmGroupMember updated = group.getPlayer(received.uuid);
                updated.role = received.role;
                group.updatePlayer(updated);
            } else {
                ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
                return;
            }
        }
        
        ServerPayloadDispatcher.sendGroupPayload(group);
    }
	
	public static void handleRequestModifierPayload(final ServerboundRequestModifierPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group());
        if (group == null) { return; }
		
		MinegasmGroupMember member = group.getPlayer(context.player().getUUID());
		if (member == null) { return; }
		
		ServerPayloadDispatcher.sendModifierPayload((ServerPlayer) context.player(), member);
    }
	
	public static void handleUpdateModifierPayload(final ServerboundUpdateModifierPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group());
        if (group == null) { return; }

        MinegasmGroupMember author = group.getPlayer(context.player().getUUID());
        MinegasmGroupMember member = group.getPlayer(data.member());
		
		if (author == null || member == null) { return; }
        
		if (author.role == MinegasmGroupMember.PlayerRole.DOM || author.role == MinegasmGroupMember.PlayerRole.SWITCH) {
			if (member.role == MinegasmGroupMember.PlayerRole.SWITCH || member.role == MinegasmGroupMember.PlayerRole.SUB) {
				member.modifier = data.modifier().orElse(null);
				ServerPayloadDispatcher.sendModifierPayload(member);
			} else {
				ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
			}
		} else {
			ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
		}
    }
    
    public static void handleJoinGroupPayload(final ServerboundJoinGroupPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.uuid());
        if (group == null) { 
			ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) context.player(), null);
			return;
		}
        
        if (group.isPrivate == false || (data.password().isPresent() && group.password.equals(data.password().get()))) {
			MinegasmGroupMember.PlayerRank rank = MinegasmGroupMember.PlayerRank.MEMBER;
			if (group.getPlayerCount() == 0) {
				rank = MinegasmGroupMember.PlayerRank.LEADER;
			}
			
            group.updatePlayer(new MinegasmGroupMember(context.player().getUUID(), context.player().getName().getString(), rank, MinegasmGroupMember.PlayerRole.DISABLE));
            ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) context.player(), group);
        } else { 
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INCORRECT_PASSWORD);
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
    
    public static void handleRemoveGroupMemberPayload(final ServerboundRemoveGroupMemberPayload data, final IPayloadContext context) {
        MinegasmGroup group = MinegasmServer.getGroupMap().get(data.group());
        if (group == null) { return; }

		MinegasmGroupMember author = group.getPlayer(context.player().getUUID());
		MinegasmGroupMember member = group.getPlayer(data.member());
		if (member == null || author == null) { return; }
		
		if (author.uuid.equals(member.uuid)) {
            group.removePlayer(context.player().getUUID());
			
			if (member.rank == MinegasmGroupMember.PlayerRank.LEADER) {
				group.promoteNewLeader();
			}
			
			ServerPayloadDispatcher.sendGroupPayload(member, null);
		} else if (author.rank.ordinal() < member.rank.ordinal()) {
            group.removePlayer(context.player().getUUID());
			ServerPayloadDispatcher.sendGroupPayload(member, null);
		} else {
			ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) context.player(), ServerMessage.INVALID_PERMISSION);
		}
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
    }
}