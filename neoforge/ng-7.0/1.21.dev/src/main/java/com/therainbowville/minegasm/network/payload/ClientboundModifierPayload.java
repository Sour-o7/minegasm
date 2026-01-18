package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmModifier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import java.util.Optional;
import net.minecraft.core.UUIDUtil;

public record ClientboundModifierPayload(UUID member, Optional<MinegasmModifier> modifier) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ClientboundModifierPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_receive_modifier_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundModifierPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ClientboundModifierPayload::member,
        MinegasmModifier.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundModifierPayload::modifier,
        ClientboundModifierPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}