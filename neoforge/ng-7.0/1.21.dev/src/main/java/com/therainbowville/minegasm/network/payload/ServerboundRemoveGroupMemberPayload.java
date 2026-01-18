package com.therainbowville.minegasm.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;

public record ServerboundRemoveGroupMemberPayload(UUID group, UUID member) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ServerboundRemoveGroupMemberPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_remove_group_member_payload"));
    
    public static final StreamCodec<ByteBuf, ServerboundRemoveGroupMemberPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundRemoveGroupMemberPayload::group,
        UUIDUtil.STREAM_CODEC, ServerboundRemoveGroupMemberPayload::member,
        ServerboundRemoveGroupMemberPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}