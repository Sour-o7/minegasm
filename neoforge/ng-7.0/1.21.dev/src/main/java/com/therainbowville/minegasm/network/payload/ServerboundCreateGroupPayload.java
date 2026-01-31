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

public record ServerboundCreateGroupPayload(MinegasmGroup group) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundCreateGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_create_group_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundCreateGroupPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroup.STREAM_CODEC, ServerboundCreateGroupPayload::group,
        ServerboundCreateGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
	@Override
	public void handleOnServer(Player player) {
        MinegasmGroup serverGroup = group;
        serverGroup.updatePlayer(new MinegasmGroupMember(player.getUUID(), player.getName().getString(), MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRole.DISABLE));
        MinegasmServer.updateGroup(serverGroup);
        
        ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) player, serverGroup);
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
	}
}