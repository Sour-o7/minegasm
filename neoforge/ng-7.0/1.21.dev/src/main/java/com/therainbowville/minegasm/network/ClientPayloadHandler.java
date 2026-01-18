package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.gui.JoinGroupScreen;
import com.therainbowville.minegasm.gui.GroupScreen;
import com.therainbowville.minegasm.gui.GroupSettingsScreen;
import com.therainbowville.minegasm.gui.GroupPasswordScreen;
import com.therainbowville.minegasm.gui.DialoguePopupScreen;
import com.therainbowville.minegasm.gui.MemberScreenBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPayloadHandler {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void handleEventPayload(final ClientboundEventPayload data, final IPayloadContext context) {
        EventProcessor.receiveEvent(data.eventType(), data.event());
    }
	
	public static void handleModifierPayload(final ClientboundModifierPayload data, final IPayloadContext context) {
		if (data.member().equals(Minecraft.getInstance().player.getGameProfile().getId())) {
			EventProcessor.setModifier(data.modifier().orElse(null));
		}
		
		if (Minecraft.getInstance().screen instanceof MemberScreenBase) {
            ((MemberScreenBase) Minecraft.getInstance().screen).updateMemberModifier(data.member(), data.modifier().orElse(null));
        }
	}
    
    public static void handleGroupInfoPayload(final ClientboundGroupInfoPayload data, final IPayloadContext context) {
        MinegasmClient.groupInfo = data.info();
        if (Minecraft.getInstance().screen instanceof JoinGroupScreen) {
            Minecraft.getInstance().setScreen(new JoinGroupScreen());   
        }
    }
    
    public static void handleGroupPayload(final ClientboundGroupPayload data, final IPayloadContext context) {
        MinegasmClient.setClientGroup(data.group().orElse(null));
        EventProcessor.refreshReferenceConfig();
        
        Screen screen = Minecraft.getInstance().screen;
				
        if (screen instanceof GroupScreen || screen instanceof JoinGroupScreen || screen instanceof GroupSettingsScreen || screen instanceof GroupPasswordScreen) {
			if (data.group().isPresent()) {
				Minecraft.getInstance().setScreen(new GroupScreen(data.group().orElse(null).name));            				
			} else {
				Minecraft.getInstance().setScreen(new JoinGroupScreen());
			}
        } else if (screen instanceof MemberScreenBase) {
			if (!data.group().isPresent()) {
				Minecraft.getInstance().setScreen(new JoinGroupScreen());
			}
		}
    }
    
    public static void handleMessagePayload(final ClientboundMessagePayload data, final IPayloadContext context) {
        ServerMessage message = ServerMessage.fromInt(data.code());
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