package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.UUIDUtil;

public record ClientboundMessagePayload(int code) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ClientboundMessagePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_message_payload"));
    
    public static final StreamCodec<ByteBuf, ClientboundMessagePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ClientboundMessagePayload::code,
        ClientboundMessagePayload::new);
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}