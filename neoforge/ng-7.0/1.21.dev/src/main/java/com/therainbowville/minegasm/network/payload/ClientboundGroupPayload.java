package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroup;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ClientboundGroupPayload(Optional<MinegasmGroup> group) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ClientboundGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_group_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundGroupPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroup.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundGroupPayload::group,
        ClientboundGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}