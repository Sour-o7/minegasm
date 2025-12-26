package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroup;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundUpdateGroupPayload(MinegasmGroup group) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ServerboundUpdateGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_update_group_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateGroupPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroup.STREAM_CODEC, ServerboundUpdateGroupPayload::group,
        ServerboundUpdateGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}