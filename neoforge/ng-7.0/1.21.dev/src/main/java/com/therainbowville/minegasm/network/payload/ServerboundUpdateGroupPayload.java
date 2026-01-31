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

public record ServerboundUpdateGroupPayload(MinegasmGroup group) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundUpdateGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_update_group_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateGroupPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroup.STREAM_CODEC, ServerboundUpdateGroupPayload::group,
        ServerboundUpdateGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
	@Override
	public void handleOnServer(Player player) {
        MinegasmGroup serverGroup = MinegasmServer.getGroupMap().get(group.uuid);
        if (serverGroup == null) { return; }

        MinegasmGroupMember member = serverGroup.getPlayer(player.getUUID());

        if (member != null && member.rank == MinegasmGroupMember.PlayerRank.LEADER) {
            serverGroup.name = group.name;
            serverGroup.password = group.password;
            serverGroup.config.copyFrom(group.config);
            MinegasmServer.updateGroup(serverGroup);
        } else {
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
            return;
        }
        
        ServerPayloadDispatcher.sendGroupPayload(serverGroup);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
	}
}