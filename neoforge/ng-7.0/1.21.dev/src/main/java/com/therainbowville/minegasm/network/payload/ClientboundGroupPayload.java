package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.gui.MinegasmScreenListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ClientboundGroupPayload(Optional<MinegasmGroup> group) implements IClientboundPayload {
    
    public static final CustomPacketPayload.Type<ClientboundGroupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_group_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundGroupPayload> STREAM_CODEC = StreamCodec.composite(
        MinegasmGroup.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundGroupPayload::group,
        ClientboundGroupPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleOnClient(IPayloadContext context) {
		MinegasmClient.setClientGroup(group.orElse(null));
        EventProcessor.refreshReferenceConfig();
        
        Screen screen = Minecraft.getInstance().screen;
				
        if (screen instanceof MinegasmScreenListener) {
			((MinegasmScreenListener) screen).onGroupUpdate(MinegasmClient.getClientGroup());
		}
	}
}