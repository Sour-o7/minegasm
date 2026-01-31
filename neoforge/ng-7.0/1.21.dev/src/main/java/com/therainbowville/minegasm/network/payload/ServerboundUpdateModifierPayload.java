package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmServer;
import com.therainbowville.minegasm.core.MinegasmModifier;
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

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundUpdateModifierPayload(UUID group, UUID member, Optional<MinegasmModifier> modifier) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundUpdateModifierPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_update_modifier_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateModifierPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundUpdateModifierPayload::group,
        UUIDUtil.STREAM_CODEC, ServerboundUpdateModifierPayload::member,
        MinegasmModifier.STREAM_CODEC.apply(ByteBufCodecs::optional), ServerboundUpdateModifierPayload::modifier,
        ServerboundUpdateModifierPayload::new
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
		
		if (srcMember == null || serverMember == null) { return; }
        
		if (srcMember.role == MinegasmGroupMember.PlayerRole.DOM || srcMember.role == MinegasmGroupMember.PlayerRole.SWITCH) {
			if (serverMember.role == MinegasmGroupMember.PlayerRole.SWITCH || serverMember.role == MinegasmGroupMember.PlayerRole.SUB) {
				serverMember.modifier = modifier.orElse(null);
				ServerPayloadDispatcher.sendModifierPayload(serverMember);
			} else {
				ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
			}
		} else {
			ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INVALID_PERMISSION);
		}
	}
}