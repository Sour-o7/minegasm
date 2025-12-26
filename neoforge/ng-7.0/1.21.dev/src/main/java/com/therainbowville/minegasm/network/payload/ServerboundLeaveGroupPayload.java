package com.therainbowville.minegasm.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;

public record ServerboundLeaveGroupPayload(UUID uuid) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ServerboundLeaveGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_leave_group_payload"));
    
    public static final StreamCodec<ByteBuf, ServerboundLeaveGroupPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundLeaveGroupPayload::uuid,
        ServerboundLeaveGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}