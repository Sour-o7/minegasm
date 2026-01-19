package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.gui.DialoguePopupScreen;
import com.therainbowville.minegasm.gui.JoinGroupScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import io.netty.buffer.ByteBuf;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.UUIDUtil;

public record ClientboundMessagePayload(int code) implements IClientboundPayload {
    
    public static final CustomPacketPayload.Type<ClientboundMessagePayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("minegasm", "clientbound_message_payload"));
    
    public static final StreamCodec<FriendlyByteBuf, ClientboundMessagePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ClientboundMessagePayload::code,
        ClientboundMessagePayload::new);
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
		
	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleOnClient(IPayloadContext context) {
        ServerMessage message = ServerMessage.fromInt(code);
        DialoguePopupScreen screen = null;
        
        switch (message) {
            case ServerMessage.INCORRECT_PASSWORD:
                screen = new DialoguePopupScreen("Incorrect Password", "", () -> Minecraft.getInstance().setScreen(new JoinGroupScreen()));
                break;           
            default:
                break;
        }
        
        if (screen != null) {
            Minecraft.getInstance().setScreen(screen);
        }
	}
}