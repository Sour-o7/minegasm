package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.gui.JoinGroupScreen;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
import java.util.List;

public record ClientboundGroupInfoPayload(List<MinegasmGroupInfo> info) implements IClientboundPayload {
    
    public static final CustomPacketPayload.Type<ClientboundGroupInfoPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_group_info_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundGroupInfoPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroupInfo.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundGroupInfoPayload::info,
        ClientboundGroupInfoPayload::new);
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleOnClient(IPayloadContext context) {
		MinegasmClient.groupInfo = info;
        if (Minecraft.getInstance().screen instanceof JoinGroupScreen) {
            Minecraft.getInstance().setScreen(new JoinGroupScreen());   
        }
	}
}