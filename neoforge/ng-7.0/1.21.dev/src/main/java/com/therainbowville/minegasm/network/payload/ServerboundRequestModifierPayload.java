package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmServer;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import com.therainbowville.minegasm.core.MinegasmModifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundRequestModifierPayload(UUID group, UUID member) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundRequestModifierPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_request_modifier_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundRequestModifierPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundRequestModifierPayload::group,
        UUIDUtil.STREAM_CODEC, ServerboundRequestModifierPayload::member,
        ServerboundRequestModifierPayload::new
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
		if (member == null) { return; }
		
		ServerPayloadDispatcher.sendModifierPayload((ServerPlayer) player, member);
	}
}