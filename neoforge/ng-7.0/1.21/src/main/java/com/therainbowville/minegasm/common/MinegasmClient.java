package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.ConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Minegasm.MOD_ID, dist = Dist.CLIENT) 
public class MinegasmClient {
    public MinegasmClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> new ConfigScreen(screen));
    }
}