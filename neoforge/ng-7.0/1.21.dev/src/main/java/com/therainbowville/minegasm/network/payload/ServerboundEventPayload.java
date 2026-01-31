package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmServer;
import com.therainbowville.minegasm.core.EventProcessor.EventData;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmConfigGroup;

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

public record ServerboundEventPayload(UUID group, String eventType, EventData event) implements IServerboundPayload {
    
    public static final CustomPacketPayload.Type<ServerboundEventPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_event_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundEventPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundEventPayload::group,
        ByteBufCodecs.STRING_UTF8, ServerboundEventPayload::eventType,
        EventData.STREAM_CODEC, ServerboundEventPayload::event,
        ServerboundEventPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
	
	@Override
	public void handleOnServer(Player player) {
		MinegasmGroup serverGroup = MinegasmServer.getGroup(group);
		MinegasmConfigGroup.EventConfig config = (MinegasmConfigGroup.EventConfig) serverGroup.config.getModeConfig(eventType);
		
		if (config.type == MinegasmConfig.TriggerType.SHARED) {
			if (config.proximityEnabled) {
				ServerPayloadDispatcher.sendProximityEventPayload(serverGroup, (ServerPlayer) player, eventType, event);
			} else {
				ServerPayloadDispatcher.sendGroupEventPayload(serverGroup, (ServerPlayer) player, eventType, event);
			}
		}
	}
}