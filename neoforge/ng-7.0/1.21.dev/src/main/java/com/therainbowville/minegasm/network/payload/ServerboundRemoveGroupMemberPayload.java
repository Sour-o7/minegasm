package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmServer;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;

public record ServerboundRemoveGroupMemberPayload(UUID group, UUID member) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundRemoveGroupMemberPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_remove_group_member_payload"));
    
    public static final StreamCodec<ByteBuf, ServerboundRemoveGroupMemberPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundRemoveGroupMemberPayload::group,
        UUIDUtil.STREAM_CODEC, ServerboundRemoveGroupMemberPayload::member,
        ServerboundRemoveGroupMemberPayload::new
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
		MinegasmGroupMember serverMember = serverGroup.getPlayer(member);
		if (serverMember == null || srcMember == null) { return; }
		
		if (srcMember.uuid.equals(serverMember.uuid)) {
            serverGroup.removePlayer(player.getUUID());
			
			if (serverMember.rank == MinegasmGroupMember.PlayerRank.LEADER) {
				serverGroup.promoteNewLeader();
			}
			
			ServerPayloadDispatcher.sendGroupPayload(serverMember, null);
		} else if (srcMember.rank.ordinal() < serverMember.rank.ordinal()) {
            serverGroup.removePlayer(player.getUUID());
			ServerPayloadDispatcher.sendGroupPayload(serverMember, null);
		} else {
			ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
		}
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
	}
}