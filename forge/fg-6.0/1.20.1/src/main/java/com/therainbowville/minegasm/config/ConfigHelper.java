package com.therainbowville.minegasm.config;

import net.minecraftforge.client.ConfigScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void bakeClient() {
        MinegasmConfig.serverUrl = ConfigHolder.CLIENT.serverUrl.get();
        MinegasmConfig.vibrate = ConfigHolder.CLIENT.vibrate.get();
        MinegasmConfig.mode = ConfigHolder.CLIENT.mode.get();
        MinegasmConfig.stealth = ConfigHolder.CLIENT.stealth.get();
        MinegasmConfig.tickFrequency = ConfigHolder.CLIENT.tickFrequency.get().getInt();
        MinegasmConfig.ticksPerSecond = Math.max(0, 20f / MinegasmConfig.tickFrequency);

        MinegasmConfig.attackIntensity = ConfigHolder.CLIENT.attackIntensity.get();
        MinegasmConfig.hurtIntensity = ConfigHolder.CLIENT.hurtIntensity.get();
        MinegasmConfig.mineIntensity = ConfigHolder.CLIENT.mineIntensity.get();
        MinegasmConfig.placeIntensity = ConfigHolder.CLIENT.placeIntensity.get();
        MinegasmConfig.xpChangeIntensity = ConfigHolder.CLIENT.xpChangeIntensity.get();
        MinegasmConfig.fishingIntensity = ConfigHolder.CLIENT.fishingIntensity.get();
        MinegasmConfig.harvestIntensity = ConfigHolder.CLIENT.harvestIntensity.get();
        MinegasmConfig.vitalityIntensity = ConfigHolder.CLIENT.vitalityIntensity.get();
        MinegasmConfig.advancementIntensity = ConfigHolder.CLIENT.advancementIntensity.get();
    }

    public static void bakeServer() {
    }

    public static void saveClient() {
        try {

            MinegasmConfigBuffer buffer = new MinegasmConfigBuffer();

//        Field[] fields = MinegasmConfigBuffer.class.getFields();

//        for (Field field : fields)
//        {
//            Field configField = ConfigHolder.CLIENT.getClass().getDeclaredField(field.getName()); // Get Corisponding Field in CLIENT
//            Class configFieldClass = configField.getType(); // Get the configField Class
//            Method method = configFieldClass.getMethod("set", Object.class);
//            LOGGER.info(method);
//            method.invoke(configField.get(ConfigHolder.CLIENT), field.get(buffer));
//        }

            ConfigHolder.CLIENT.serverUrl.set(buffer.serverUrl);
            ConfigHolder.CLIENT.vibrate.set(buffer.vibrate);
            ConfigHolder.CLIENT.mode.set(buffer.mode);
            ConfigHolder.CLIENT.stealth.set(buffer.stealth);
            ConfigHolder.CLIENT.tickFrequency.set(ClientConfig.TickFrequencyOptions.fromInt(buffer.tickFrequency));

            ConfigHolder.CLIENT.attackIntensity.set(buffer.attackIntensity);
            ConfigHolder.CLIENT.hurtIntensity.set(buffer.hurtIntensity);
            ConfigHolder.CLIENT.mineIntensity.set(buffer.mineIntensity);
            ConfigHolder.CLIENT.placeIntensity.set(buffer.placeIntensity);
            ConfigHolder.CLIENT.xpChangeIntensity.set(buffer.xpChangeIntensity);
            ConfigHolder.CLIENT.fishingIntensity.set(buffer.fishingIntensity);
            ConfigHolder.CLIENT.harvestIntensity.set(buffer.harvestIntensity);
            ConfigHolder.CLIENT.vitalityIntensity.set(buffer.vitalityIntensity);
            ConfigHolder.CLIENT.advancementIntensity.set(buffer.advancementIntensity);
        } catch (Throwable e) {
            LOGGER.info(e);
        }
    }

    public static ConfigScreenHandler.ConfigScreenFactory createConfigScreenFactory() {
        return new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> new ConfigScreen(screen));
    }

}