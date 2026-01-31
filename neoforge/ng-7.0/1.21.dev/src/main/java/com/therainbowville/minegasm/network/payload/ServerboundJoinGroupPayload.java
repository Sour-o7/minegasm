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

public record ServerboundJoinGroupPayload(UUID uuid, Optional<String> password) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundJoinGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "event_payload"));
    
    public static final StreamCodec<ByteBuf, ServerboundJoinGroupPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundJoinGroupPayload::uuid,
        ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), ServerboundJoinGroupPayload::password,
        ServerboundJoinGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
	
	@Override
	public void handleOnServer(Player player) {
        MinegasmGroup serverGroup = MinegasmServer.getGroupMap().get(uuid);
        if (serverGroup == null) { 
			ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) player, null);
			return;
		}
        
        if (serverGroup.isPrivate == false || (password.isPresent() && serverGroup.password.equals(password.get()))) {
			MinegasmGroupMember.PlayerRank rank = MinegasmGroupMember.PlayerRank.MEMBER;
			if (serverGroup.getPlayerCount() == 0) {
				rank = MinegasmGroupMember.PlayerRank.LEADER;
			}
			
            serverGroup.updatePlayer(new MinegasmGroupMember(player.getUUID(), player.getName().getString(), rank, MinegasmGroupMember.PlayerRole.DISABLE));
            ServerPayloadDispatcher.sendGroupPayload((ServerPlayer) player, serverGroup);
        } else { 
            ServerPayloadDispatcher.sendMessagePayload((ServerPlayer) player, ServerMessage.INCORRECT_PASSWORD);
        }
        
        ServerPayloadDispatcher.sendGroupInfoPayload(MinegasmServer.getGroupInfoList());
	}
}