package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.Minegasm;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.ByteBuf;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = Minegasm.MOD_ID)
public class PayloadRegister {
	
	private static <T extends IClientboundPayload> void registerClientbound(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec) {
		registrar.playToClient(type, codec, IClientboundPayload::handleOnClient);
	}
	
	private static <T extends IServerboundPayload> void registerServerbound(PayloadRegistrar registrar, CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec) {
		registrar.playToServer(type, codec, (packet, context) -> {
			packet.handleOnServer(context.player());
		});
	}
    
    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        
        // Client //	
		
		registerClientbound(registrar, ClientboundEventPayload.TYPE, ClientboundEventPayload.STREAM_CODEC);
		registerClientbound(registrar, ClientboundGroupInfoPayload.TYPE, ClientboundGroupInfoPayload.STREAM_CODEC);
		registerClientbound(registrar, ClientboundGroupPayload.TYPE, ClientboundGroupPayload.STREAM_CODEC);
		registerClientbound(registrar, ClientboundModifierPayload.TYPE, ClientboundModifierPayload.STREAM_CODEC);
		registerClientbound(registrar, ClientboundMessagePayload.TYPE, ClientboundMessagePayload.STREAM_CODEC);

        // Server //
		registerServerbound(registrar, ServerboundEventPayload.TYPE, ServerboundEventPayload.STREAM_CODEC);		
		registerServerbound(registrar, ServerboundCreateGroupPayload.TYPE, ServerboundCreateGroupPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundJoinGroupPayload.TYPE, ServerboundJoinGroupPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundUpdateGroupPayload.TYPE, ServerboundUpdateGroupPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundUpdateGroupMemberPayload.TYPE, ServerboundUpdateGroupMemberPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundRequestModifierPayload.TYPE, ServerboundRequestModifierPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundUpdateModifierPayload.TYPE, ServerboundUpdateModifierPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundRemoveGroupPayload.TYPE, ServerboundRemoveGroupPayload.STREAM_CODEC);
		registerServerbound(registrar, ServerboundRemoveGroupMemberPayload.TYPE, ServerboundRemoveGroupMemberPayload.STREAM_CODEC);
    }
}