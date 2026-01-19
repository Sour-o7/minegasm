package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmModifier;
import com.therainbowville.minegasm.gui.MemberScreenBase;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import io.netty.buffer.ByteBuf;

import net.minecraft.client.Minecraft;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
import java.util.Optional;
import net.minecraft.core.UUIDUtil;

public record ClientboundModifierPayload(UUID member, Optional<MinegasmModifier> modifier) implements IClientboundPayload {
    
    public static final CustomPacketPayload.Type<ClientboundModifierPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_receive_modifier_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundModifierPayload> STREAM_CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, ClientboundModifierPayload::member,
        MinegasmModifier.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundModifierPayload::modifier,
        ClientboundModifierPayload::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleOnClient(IPayloadContext context) {
		if (member.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
			EventProcessor.setModifier(modifier.orElse(null));
		}
		
		if (Minecraft.getInstance().screen instanceof MemberScreenBase) {
            ((MemberScreenBase) Minecraft.getInstance().screen).updateMemberModifier(member, modifier.orElse(null));
        }
	}
}