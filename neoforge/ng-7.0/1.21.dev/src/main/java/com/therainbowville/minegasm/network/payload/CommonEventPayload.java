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

public record CommonEventPayload(String eventType, EventData event) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<CommonEventPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "common_event_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, CommonEventPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, CommonEventPayload::eventType,
        EventData.STREAM_CODEC, CommonEventPayload::event,
        CommonEventPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}