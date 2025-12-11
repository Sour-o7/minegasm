package com.therainbowville.minegasm.config;

import com.therainbowville.minegasm.common.Minegasm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@EventBusSubscriber(modid = Minegasm.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class MinegasmConfig {
    private static final Logger LOGGER = LogManager.getLogger();

    // Client
    public static String serverUrl;

    public static boolean vibrate;
    public static ClientConfig.GameplayMode mode = ClientConfig.GameplayMode.NORMAL;
    public static boolean stealth;
    public static int tickFrequency;
    public static float ticksPerSecond;

    public static int attackIntensity;
    public static int hurtIntensity;
    public static int mineIntensity;
    public static int placeIntensity;
    public static int xpChangeIntensity;
    public static int fishingIntensity;
    public static int harvestIntensity;
    public static int vitalityIntensity;
    public static int advancementIntensity;

    // Server
    // -- none at the moment

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Re-bake the configs when they change
        if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
            ConfigHelper.bakeClient();
            LOGGER.debug("Baked client config");
        } else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            ConfigHelper.bakeServer();
            LOGGER.debug("Baked server config");
        }
    }

    public static void save() {
        ConfigHelper.saveClient();
    }

}

class MinegasmConfigBuffer {
    public String serverUrl;

    public boolean vibrate;
    public ClientConfig.GameplayMode mode = ClientConfig.GameplayMode.NORMAL;
    public boolean stealth;
    public int tickFrequency;

    public int attackIntensity;
    public int hurtIntensity;
    public int mineIntensity;
    public int placeIntensity;
    public int xpChangeIntensity;
    public int fishingIntensity;
    public int harvestIntensity;
    public int vitalityIntensity;
    public int advancementIntensity;

    MinegasmConfigBuffer() {
        this.serverUrl = MinegasmConfig.serverUrl;
        this.vibrate = MinegasmConfig.vibrate;
        this.mode = MinegasmConfig.mode;
        this.stealth = MinegasmConfig.stealth;
        this.tickFrequency = MinegasmConfig.tickFrequency;

        this.attackIntensity = MinegasmConfig.attackIntensity;
        this.hurtIntensity = MinegasmConfig.hurtIntensity;
        this.mineIntensity = MinegasmConfig.mineIntensity;
        this.placeIntensity = MinegasmConfig.placeIntensity;
        this.xpChangeIntensity = MinegasmConfig.xpChangeIntensity;
        this.fishingIntensity = MinegasmConfig.fishingIntensity;
        this.harvestIntensity = MinegasmConfig.harvestIntensity;
        this.vitalityIntensity = MinegasmConfig.vitalityIntensity;
        this.advancementIntensity = MinegasmConfig.advancementIntensity;
    }
}