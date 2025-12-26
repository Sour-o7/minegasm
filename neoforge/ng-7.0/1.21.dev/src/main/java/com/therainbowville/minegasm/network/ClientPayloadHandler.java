package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.gui.JoinGroupScreen;
import com.therainbowville.minegasm.gui.GroupScreen;
import com.therainbowville.minegasm.gui.GroupSettingsScreen;
import com.therainbowville.minegasm.gui.DialoguePopupScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientPayloadHandler {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void handleEventPayload(final CommonEventPayload data, final IPayloadContext context) {
        LOGGER.info("Payload Received on Client: " + data.eventType());
    }
    
    public static void handleGroupInfoPayload(final ClientboundGroupInfoPayload data, final IPayloadContext context) {
        MinegasmClient.groupInfo = data.groupInfo();
        if (Minecraft.getInstance().screen instanceof JoinGroupScreen) {
            Minecraft.getInstance().setScreen(new JoinGroupScreen());   
        }
    }
    
    public static void handleGroupPayload(final ClientboundGroupPayload data, final IPayloadContext context) {
        MinegasmClient.setClientGroup(data.group());
        EventProcessor.refreshReferenceConfig();
        
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof GroupScreen || screen instanceof JoinGroupScreen || screen instanceof GroupSettingsScreen) {
            Minecraft.getInstance().setScreen(new GroupScreen(data.group().name));            
        }
    }
    
    public static void handleMessagePayload(final ClientboundMessagePayload data, final IPayloadContext context) {
        ServerMessage message = ServerMessage.fromInt(data.messageCode());
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