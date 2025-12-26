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

public record ClientboundGroupInfoPayload(List<MinegasmGroupInfo> groupInfo) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ClientboundGroupInfoPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_group_info_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundGroupInfoPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroupInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundGroupInfoPayload::groupInfo,
        ClientboundGroupInfoPayload::new);
    
    /*public static final StreamCodec<FriendlyByteBuf, ClientboundGroupInfoPayload> STREAM_CODEC = StreamCodec.ofMember(ClientboundGroupInfoPayload::write, ClientboundGroupInfoPayload::read);
    
    private void write(FriendlyByteBuf buf) {
        buf.writeShort(groupInfo.size());
        for (int i = 0; i < groupInfo.size(); i++) {
            MinegasmGroupInfo.STREAM_CODEC.encode(buf, groupInfo.get(i));
        }
    }
    
    private static ClientboundGroupInfoPayload read(FriendlyByteBuf buf) {
        List<MinegasmGroupInfo> list = new ArrayList<MinegasmGroupInfo>();
        int count = buf.readShort();
        for (int i = 0; i < count; i++) {
            list.add(MinegasmGroupInfo.STREAM_CODEC.decode(buf));
        }
        return new ClientboundGroupInfoPayload(list);
    }*/
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}