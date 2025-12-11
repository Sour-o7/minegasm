package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.ConfigHelper;
import com.therainbowville.minegasm.config.ConfigHolder;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Minegasm.MOD_ID)
public class Minegasm {
    public static final String MOD_ID = "minegasm";
    public static final String NAME = "Minegasm";
    private static final Logger LOGGER = LogManager.getLogger();

    public Minegasm() {
        ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ConfigHelper::createConfigScreenFactory);
    }
}
