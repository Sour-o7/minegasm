package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.EventProcessor.EventData;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
import net.minecraft.core.UUIDUtil;

public record ClientboundEventPayload(String eventType, EventData event) implements IClientboundPayload {
    
    public static final CustomPacketPayload.Type<ClientboundEventPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_event_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundEventPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ClientboundEventPayload::eventType,
        EventData.STREAM_CODEC, ClientboundEventPayload::event,
        ClientboundEventPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleOnClient(IPayloadContext context) {
		EventProcessor.receiveEvent(eventType, event);
	}
}