package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor.EventData;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundEventPayload(UUID group, String eventType, EventData event) implements CustomPacketPayload {
    
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
}