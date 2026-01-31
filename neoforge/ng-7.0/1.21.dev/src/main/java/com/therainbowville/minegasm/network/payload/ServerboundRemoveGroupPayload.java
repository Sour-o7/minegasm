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

public record ServerboundRemoveGroupPayload(UUID group) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundRemoveGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_remove_group_payload"));
    
    public static final StreamCodec<ByteBuf, ServerboundRemoveGroupPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundRemoveGroupPayload::group,
        ServerboundRemoveGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
	
	@Override
	public void handleOnServer(Player player) {
        MinegasmGroup serverGroup = MinegasmServer.getGroupMap().get(group);
        if (serverGroup == null) { return; }

        MinegasmGroupMember member = serverGroup.getPlayer(player.getUUID());

        if (member != null && member.rank == MinegasmGroupMember.PlayerRank.LEADER) {
			MinegasmServer.removeGroup(group);
			ServerPayloadDispatcher.sendRemovedGroupPayloads(serverGroup);
        } else {
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
            return;
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
	}
}