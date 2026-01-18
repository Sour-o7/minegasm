package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmModifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundRequestModifierPayload(UUID group, UUID member) implements CustomPacketPayload {
    
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
    
}