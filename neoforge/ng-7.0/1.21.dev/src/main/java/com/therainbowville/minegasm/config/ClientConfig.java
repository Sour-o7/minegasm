package com.therainbowville.minegasm.config;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.core.MinegasmConfigDefaults;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

public final class ClientConfig {

    // Client Config Settings
    public final ModConfigSpec.ConfigValue<String> serverUrl;
    public final ModConfigSpec.BooleanValue vibrate;
    public final ModConfigSpec.EnumValue<MinegasmConfig.GameplayMode> mode;
    public final ModConfigSpec.BooleanValue showChatMessages;
    public final ModConfigSpec.EnumValue<MinegasmConfig.TickFrequencyOptions> tickFrequency;
    public final ModConfigSpec.BooleanValue useGroupSettings;
    public final ModConfigSpec.BooleanValue receiveVibrationsFromOthers;

    // Toy Config Settings
    public final EventConfigSpec attackConfig;
    public final EventConfigSpec hurtConfig;
    public final EventConfigSpec mineConfig;
    public final EventConfigSpec placeConfig;
    public final EventConfigSpec harvestConfig;
    public final EventConfigSpec vitalityConfig;
    public final EventConfigSpec xpChangeConfig;
    public final EventConfigSpec advancementConfig;
    public final EventConfigSpec fishingConfig;
    
    public int ticksPerSecond = 20;

    public void fromMinegasmConfig(MinegasmConfigClient src) {
        serverUrl.set(src.serverUrl);
        vibrate.set(src.vibrate);
        mode.set(src.mode);
        showChatMessages.set(src.showChatMessages);
        useGroupSettings.set(src.useGroupSettings);
        receiveVibrationsFromOthers.set(src.receiveVibrationsFromOthers);
        tickFrequency.set(src.tickFrequency);
        
        attackConfig.fromEventConfig(src.attackConfig);
        hurtConfig.fromEventConfig(src.hurtConfig);
        mineConfig.fromEventConfig(src.mineConfig);
        placeConfig.fromEventConfig(src.placeConfig);
        harvestConfig.fromEventConfig(src.harvestConfig);
        vitalityConfig.fromEventConfig(src.vitalityConfig);
        xpChangeConfig.fromEventConfig(src.xpChangeConfig);
        advancementConfig.fromEventConfig(src.advancementConfig);
        fishingConfig.fromEventConfig(src.fishingConfig);
    }
    
    public MinegasmConfigClient toMinegasmConfig() {
        MinegasmConfigClient out = new MinegasmConfigClient();
        out.mode = mode.get();
        
        out.serverUrl = serverUrl.get();
        out.vibrate = vibrate.get();
        out.showChatMessages = showChatMessages.get();
        out.useGroupSettings = useGroupSettings.get();
        out.receiveVibrationsFromOthers = receiveVibrationsFromOthers.get();

        out.tickFrequency = tickFrequency.get();
        out.ticksPerSecond = Math.max(0, Math.round(20f / out.tickFrequency.getInt()));
        
        out.attackConfig = attackConfig.toEventConfig();
        out.hurtConfig = hurtConfig.toEventConfig();
        out.mineConfig = mineConfig.toEventConfig();
        out.placeConfig = placeConfig.toEventConfig();
        out.harvestConfig = harvestConfig.toEventConfig();
        out.vitalityConfig = vitalityConfig.toEventConfig();
        out.xpChangeConfig = xpChangeConfig.toEventConfig();
        out.advancementConfig = advancementConfig.toEventConfig();
        out.fishingConfig = fishingConfig.toEventConfig();
        
        return out;
    }

    ClientConfig(final ModConfigSpec.Builder builder) {
        builder.push("minegasm");
        
            builder.push("client");

                serverUrl = builder
                .comment("The Initface URL for Minegasm to connect to")
                .translation(Minegasm.MOD_ID + ".config.serverUrl")
                .define("serverUrl", MinegasmConfigDefaults.ClientConfig.serverUrl);
                    
                vibrate = builder
                .comment("Turn vibration on/off")
                .translation(Minegasm.MOD_ID + ".config.vibrate")
                .define("vibrate", MinegasmConfigDefaults.ClientConfig.vibrate);
                    
                mode = builder
                .comment("The vibration mode")
                .translation(Minegasm.MOD_ID + ".config.mode")
                .defineEnum("mode", MinegasmConfigDefaults.mode);
                
                showChatMessages = builder
                .comment("Toggle minegasm chat messages on/off")
                .translation(Minegasm.MOD_ID + ".config.showChatMessages")
                .define("showChatMessages", MinegasmConfigDefaults.ClientConfig.showChatMessages);
                
                useGroupSettings = builder
                .comment("Whether to use a joined group's settings instead of your own")
                .translation(Minegasm.MOD_ID + ".config.useGroupSettings")
                .define("useGroupSettings", MinegasmConfigDefaults.ClientConfig.useGroupSettings);
                
                receiveVibrationsFromOthers = builder
                .comment("Toggle receivinig vibrations from other people")
                .translation(Minegasm.MOD_ID + ".config.receiveVibrationsFromOthers")
                .define("receiveVibrationsFromOthers", MinegasmConfigDefaults.ClientConfig.receiveVibrationsFromOthers);
    
                tickFrequency = builder
                .comment("How frequently should Minegasm preform calculations. Warning: The less frequent it calculates, the more imprecise Minegasm will be.")
                .translation(Minegasm.MOD_ID + ".config.mode")
                .defineEnum("tickFrequency", MinegasmConfigDefaults.ClientConfig.tickFrequency);

            builder.pop();

            builder.push("settings");
            
                attackConfig = new EventConfigSpec(builder, "attack", MinegasmConfigDefaults.attackConfig);
                hurtConfig = new EventConfigSpec(builder, "hurtConfig", MinegasmConfigDefaults.hurtConfig);
                mineConfig = new EventConfigSpec(builder, "mineConfig", MinegasmConfigDefaults.mineConfig);
                placeConfig = new EventConfigSpec(builder, "placeConfig", MinegasmConfigDefaults.placeConfig);
                harvestConfig = new EventConfigSpec(builder, "harvestConfig", MinegasmConfigDefaults.harvestConfig);
                vitalityConfig = new EventConfigSpec(builder, "vitalityConfig", MinegasmConfigDefaults.vitalityConfig);
                xpChangeConfig = new EventConfigSpec(builder, "xpChangeConfig", MinegasmConfigDefaults.xpChangeConfig);
                advancementConfig = new EventConfigSpec(builder, "advancementConfig", MinegasmConfigDefaults.advancementConfig);
                fishingConfig = new EventConfigSpec(builder, "fishingConfig", MinegasmConfigDefaults.fishingConfig);

            builder.pop();
        
        builder.pop();

    }
    
    public static class EventConfigSpec {
        public final ModConfigSpec.IntValue intensity;
        public final ModConfigSpec.DoubleValue duration;
        public final ModConfigSpec.IntValue feedbackBonus;
        public final ModConfigSpec.DoubleValue feedbackDuration;
        public final ModConfigSpec.IntValue streakExtender;
        
        EventConfigSpec(final ModConfigSpec.Builder builder, String type, MinegasmConfig.EventConfig defaultConfig) {
            builder.push(type);

            intensity = builder
                .comment("The vibration intensity for custom mode")
                .translation(Minegasm.MOD_ID + ".config." + type +  ".intensity")
                .defineInRange("intensity", defaultConfig.intensity, 0, 100);
                
            duration = builder
                .comment("How long should the vibration last for custom mode")
                .translation(Minegasm.MOD_ID + ".config." + type +  ".duration")
                .defineInRange("duration", (double)defaultConfig.duration, 0, 10);
                
            feedbackBonus = builder
                .comment("Modifies the intensity for immediate feedback for custom mode")
                .translation(Minegasm.MOD_ID + ".config." + type +  ".feedbackBonus")
                .defineInRange("feedbackBonus", defaultConfig.feedbackBonus, 0, 20);
                
            feedbackDuration = builder
                .comment("How long in seconds the immediate feekback lasts for custom mode")
                .translation(Minegasm.MOD_ID + ".config." + type +  ".feedbackDuration")
                .defineInRange("feedbackDuration", (double)defaultConfig.feedbackDuration, 0, 5);
                
            // TODO: Either remove or properly impliment. See design document for ideas concerning this
            streakExtender = builder
                .comment("NOTE: NON FUNCTIONAL \nHow long to extend the streak in seconds before intensity begins to decrease during accumulation mode(s)")
                .translation(Minegasm.MOD_ID + ".config." + type +  ".streakExtender")
                .defineInRange("streakExtender", defaultConfig.streakExtender, 0, 10);
                
            builder.pop();
        }
        
        public void fromEventConfig(MinegasmConfig.EventConfig src) {
            intensity.set(src.intensity);
            duration.set((double) src.duration);
            feedbackBonus.set(src.feedbackBonus);
            feedbackDuration.set((double) src.feedbackDuration);
            streakExtender.set(src.streakExtender);
        }
        
        public MinegasmConfig.EventConfig toEventConfig() {
            MinegasmConfig.EventConfig out = new MinegasmConfig.EventConfig(
                intensity.get(),
                duration.get().floatValue(),
                feedbackBonus.get(),
                feedbackDuration.get().floatValue(),
                streakExtender.get()
            );
            
            return out;
        }
    }
}