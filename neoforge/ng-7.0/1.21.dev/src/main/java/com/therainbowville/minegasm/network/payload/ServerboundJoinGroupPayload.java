package com.therainbowville.minegasm.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;

public record ServerboundJoinGroupPayload(UUID uuid, Optional<String> password) implements CustomPacketPayload {
    
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
}