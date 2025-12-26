package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.gui.ClientConfigScreen;
import com.therainbowville.minegasm.gui.JoinGroupScreen;
import com.therainbowville.minegasm.gui.GroupScreen;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;
import com.therainbowville.minegasm.network.ClientPayloadHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.ArrayList;

@Mod(value = Minegasm.MOD_ID, dist = Dist.CLIENT) 
public class MinegasmClient {    

    public static List<MinegasmGroupInfo> groupInfo = new ArrayList<MinegasmGroupInfo>();
    private static MinegasmGroup group = null;
    
    public static void setClientGroup(MinegasmGroup group) {
        MinegasmClient.group = group;
    }

    public static MinegasmGroup getClientGroup() {
        return group;
    }

    public static final Lazy<KeyMapping> OPEN_GROUP_MENU = Lazy.of(() -> new KeyMapping(
        "key.minegasm.open_group_menu", // Will be localized using this translation key
        InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
        GLFW.GLFW_KEY_V, // Default key is P
        "key.categories.multiplayer" // Mapping will be in the misc category
    ));

    public MinegasmClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> new ClientConfigScreen(screen));
        
        modEventBus.addListener(MinegasmClient::registerBindings);
        NeoForge.EVENT_BUS.addListener(MinegasmClient::onClientTick);
        
        //container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GROUP_MENU.get());
    }
    
    @SubscribeEvent // on the game event bus only on the physical client
    public static void onClientTick(ClientTickEvent.Post event) {
        if (OPEN_GROUP_MENU.get().consumeClick()) {
            if (group == null) {
                Minecraft.getInstance().setScreen(new JoinGroupScreen());
            } else {
                Minecraft.getInstance().setScreen(new GroupScreen(group.name));
            }
        }
    }
}