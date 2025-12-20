package com.therainbowville.minegasm.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record EventPayload(String eventType, UUID origin, int intensity, int duration) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<EventPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "event_payload"));
    
    public static final StreamCodec<ByteBuf, EventPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, EventPayload::eventType,
        UUIDUtil.STREAM_CODEC, EventPayload::origin,
        ByteBufCodecs.VAR_INT, EventPayload::intensity,
        ByteBufCodecs.VAR_INT, EventPayload::duration,
        EventPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}