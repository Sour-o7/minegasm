package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroupMember;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ServerboundUpdateGroupMemberPayload(UUID group, MinegasmGroupMember updated) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ServerboundUpdateGroupMemberPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "serverbound_update_group_member_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ServerboundUpdateGroupMemberPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ServerboundUpdateGroupMemberPayload::group,
        MinegasmGroupMember.STREAM_CODEC, ServerboundUpdateGroupMemberPayload::updated,
        ServerboundUpdateGroupMemberPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}