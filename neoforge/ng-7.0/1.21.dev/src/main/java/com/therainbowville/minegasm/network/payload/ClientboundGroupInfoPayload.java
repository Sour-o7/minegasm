package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;
import java.util.List;

public record ClientboundGroupInfoPayload(List<MinegasmGroupInfo> groupInfo) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ClientboundGroupInfoPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_group_info_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundGroupInfoPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroupInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundGroupInfoPayload::groupInfo,
        ClientboundGroupInfoPayload::new);
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}