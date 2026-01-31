package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmServer;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundUpdateGroupMemberPayload(UUID group, MinegasmGroupMember updated) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundUpdateGroupMemberPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_update_group_member_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateGroupMemberPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundUpdateGroupMemberPayload::group,
        MinegasmGroupMember.STREAM_CODEC, ServerboundUpdateGroupMemberPayload::updated,
        ServerboundUpdateGroupMemberPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
	@Override
	public void handleOnServer(Player player) {
        MinegasmGroup serverGroup = MinegasmServer.getGroupMap().get(group);
        if (serverGroup == null) { return; }

        MinegasmGroupMember srcMember = serverGroup.getPlayer(player.getUUID());
        MinegasmGroupMember oldMember = serverGroup.getPlayer(updated.uuid);
		
		if (srcMember == null || oldMember == null) { return; }
        
        if (!oldMember.rank.equals(updated.rank)) {
            if (srcMember.rank == MinegasmGroupMember.PlayerRank.LEADER) {
                MinegasmGroupMember updatedMember = serverGroup.getPlayer(updated.uuid);
                updatedMember.rank = updated.rank;
                if (updatedMember.rank == MinegasmGroupMember.PlayerRank.LEADER) {
                    srcMember.rank = MinegasmGroupMember.PlayerRank.SUBLEADER;
                    serverGroup.updatePlayer(srcMember);
                }
                serverGroup.updatePlayer(updatedMember);
            } else {
                ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
                return;
            }
        }
        
        if (!oldMember.role.equals(updated.role)) {
            if (serverGroup.config.forcedRoles) {
                if (srcMember.rank == MinegasmGroupMember.PlayerRank.LEADER || srcMember.rank == MinegasmGroupMember.PlayerRank.SUBLEADER) {
                    MinegasmGroupMember updatedMember = serverGroup.getPlayer(updated.uuid);
                    updatedMember.role = updated.role;
                    serverGroup.updatePlayer(updatedMember);
                } else {
                    ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
                    return;
                }
            } else if (srcMember.uuid.equals(updated.uuid)) {
                MinegasmGroupMember updatedMember = serverGroup.getPlayer(updated.uuid);
                updatedMember.role = updated.role;
                serverGroup.updatePlayer(updatedMember);
            } else {
                ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
                return;
            }
        }
        
        ServerPayloadDispatcher.sendGroupPayload(serverGroup);
	}
}