package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.ConfigContainer;

import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Minegasm.MOD_ID)
public class Minegasm {
    public static final String MOD_ID = "minegasm";
    public static final String NAME = "Minegasm";
    private static final Logger LOGGER = LogManager.getLogger();

    public Minegasm(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ConfigContainer.CLIENT_SPEC);
        container.registerConfig(ModConfig.Type.SERVER, ConfigContainer.SERVER_SPEC);
        
        //NeoForge.EVENT_BUS.register(this);
    }

}