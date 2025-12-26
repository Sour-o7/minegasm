package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

public class MinegasmConfig<T extends MinegasmConfig.EventConfig> {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

    public GameplayMode mode;

    public T attackConfig;
    public T hurtConfig;
    public T mineConfig;
    public T placeConfig;
    public T xpChangeConfig;
    public T fishingConfig;
    public T harvestConfig;
    public T vitalityConfig;
    public T advancementConfig;
    
    public static final StreamCodec<FriendlyByteBuf, MinegasmConfig> STREAM_CODEC = StreamCodec.ofMember(MinegasmConfig::write, MinegasmConfig::read);
    
    private void write(FriendlyByteBuf buf) {
        buf.writeEnum(mode);
        EventConfig.STREAM_CODEC.encode(buf, attackConfig);
        EventConfig.STREAM_CODEC.encode(buf, hurtConfig);
        EventConfig.STREAM_CODEC.encode(buf, mineConfig);
        EventConfig.STREAM_CODEC.encode(buf, placeConfig);
        EventConfig.STREAM_CODEC.encode(buf, xpChangeConfig);
        EventConfig.STREAM_CODEC.encode(buf, fishingConfig);
        EventConfig.STREAM_CODEC.encode(buf, harvestConfig);
        EventConfig.STREAM_CODEC.encode(buf, vitalityConfig);
        EventConfig.STREAM_CODEC.encode(buf, advancementConfig);
    }
        
    private static MinegasmConfig<EventConfig> read(FriendlyByteBuf buf) {
        return new MinegasmConfig<EventConfig>( 
            buf.readEnum(GameplayMode.class),
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf), 
            EventConfig.STREAM_CODEC.decode(buf)
        );
    }
    
    public MinegasmConfig() {
        this.mode = GameplayMode.NORMAL;
    }
    
    public MinegasmConfig(GameplayMode mode, T attackConfig, T hurtConfig, T mineConfig, T placeConfig, T xpChangeConfig, T fishingConfig, T harvestConfig, T vitalityConfig, T advancementConfig) {
        this.mode = mode;
        this.attackConfig = attackConfig;
        this.hurtConfig = hurtConfig;
        this.mineConfig = mineConfig;
        this.placeConfig = placeConfig;
        this.xpChangeConfig = xpChangeConfig;
        this.fishingConfig = fishingConfig;
        this.harvestConfig = harvestConfig;
        this.vitalityConfig = vitalityConfig;
        this.advancementConfig = advancementConfig;
    }

    public boolean accumulationModeEnabled() {
        return mode.equals(GameplayMode.ACCUMULATION) || mode.equals(GameplayMode.GLOBAL_ACCUMULATION);
    };
    
    public void populateFrom(MinegasmConfig<T> src) {
        if (attackConfig == null)
            attackConfig = src.attackConfig;
        if (hurtConfig == null)
            hurtConfig = src.hurtConfig;
        if (mineConfig == null)
            mineConfig = src.mineConfig;
        if (placeConfig == null)
            placeConfig = src.placeConfig;
        if (xpChangeConfig == null)
            xpChangeConfig = src.xpChangeConfig;
        if (fishingConfig == null)
            fishingConfig = src.fishingConfig;
        if (harvestConfig == null)
            harvestConfig = src.harvestConfig;
        if (vitalityConfig == null)
            vitalityConfig = src.vitalityConfig;
        if (advancementConfig == null)
            advancementConfig = src.advancementConfig;        
    }
    
    public void copyFrom(MinegasmConfig<T> src) {
        mode = src.mode;
        
        attackConfig.copyFrom(src.attackConfig);
        hurtConfig.copyFrom(src.hurtConfig);
        mineConfig.copyFrom(src.mineConfig);
        placeConfig.copyFrom(src.placeConfig);
        xpChangeConfig.copyFrom(src.xpChangeConfig);
        fishingConfig.copyFrom(src.fishingConfig);
        harvestConfig.copyFrom(src.harvestConfig);
        vitalityConfig.copyFrom(src.vitalityConfig);
        advancementConfig.copyFrom(src.advancementConfig);
    }
    
    public EventConfig getModeConfig(String type) {
        final Map<String, EventConfig> normal = new HashMap<>();
        normal.put("attack", new EventConfig(60, MinegasmConfigDefaults.attackConfig));
        normal.put("hurt", new EventConfig(0, MinegasmConfigDefaults.hurtConfig));
        normal.put("mine", new EventConfig(40, MinegasmConfigDefaults.mineConfig));
        normal.put("place", new EventConfig(40, MinegasmConfigDefaults.placeConfig));
        normal.put("xpChange", new EventConfig(80, MinegasmConfigDefaults.xpChangeConfig));
        normal.put("harvest", new EventConfig(10, MinegasmConfigDefaults.harvestConfig));
        normal.put("fishing", new EventConfig(50, MinegasmConfigDefaults.fishingConfig));
        normal.put("vitality", new EventConfig( 0, MinegasmConfigDefaults.vitalityConfig));
        normal.put("advancement", new EventConfig(90, MinegasmConfigDefaults.advancementConfig));

        final Map<String, EventConfig> masochist = new HashMap<>();
        masochist.put("attack", new EventConfig(0, MinegasmConfigDefaults.attackConfig));
        masochist.put("hurt", new EventConfig(100, MinegasmConfigDefaults.hurtConfig));
        masochist.put("mine", new EventConfig(0, MinegasmConfigDefaults.mineConfig));
        masochist.put("place", new EventConfig(0, MinegasmConfigDefaults.placeConfig));
        masochist.put("xpChange", new EventConfig(0, MinegasmConfigDefaults.xpChangeConfig));
        masochist.put("fishing", new EventConfig(0, MinegasmConfigDefaults.fishingConfig));
        masochist.put("harvest", new EventConfig(0, MinegasmConfigDefaults.harvestConfig));
        masochist.put("vitality", new EventConfig(10, MinegasmConfigDefaults.vitalityConfig));
        masochist.put("advancement", new EventConfig(0, MinegasmConfigDefaults.advancementConfig));

        final Map<String, EventConfig> hedonist = new HashMap<>();
        hedonist.put("attack", new EventConfig(60, MinegasmConfigDefaults.attackConfig));
        hedonist.put("hurt", new EventConfig(10, MinegasmConfigDefaults.hurtConfig));
        hedonist.put("mine", new EventConfig(80, MinegasmConfigDefaults.mineConfig));
        hedonist.put("place", new EventConfig(50, MinegasmConfigDefaults.placeConfig));
        hedonist.put("xpChange", new EventConfig(100, MinegasmConfigDefaults.xpChangeConfig));
        hedonist.put("fishing", new EventConfig(50, MinegasmConfigDefaults.fishingConfig));
        hedonist.put("harvest", new EventConfig(40, MinegasmConfigDefaults.harvestConfig));
        hedonist.put("vitality", new EventConfig(30, MinegasmConfigDefaults.vitalityConfig));
        hedonist.put("advancement", new EventConfig(100, MinegasmConfigDefaults.advancementConfig));
        
        final Map<String, EventConfig> accumulation = new HashMap<>();
        accumulation.put("attack", new EventConfig(5, MinegasmConfigDefaults.attackConfig));
        accumulation.put("hurt", new EventConfig(7, MinegasmConfigDefaults.hurtConfig));
        accumulation.put("mine", new EventConfig(1, MinegasmConfigDefaults.mineConfig));
        accumulation.put("place", new EventConfig(1, MinegasmConfigDefaults.placeConfig));
        accumulation.put("xpChange", new EventConfig(1, MinegasmConfigDefaults.xpChangeConfig));
        accumulation.put("fishing", new EventConfig(10, MinegasmConfigDefaults.fishingConfig));
        accumulation.put("harvest", new EventConfig(10, MinegasmConfigDefaults.harvestConfig));
        accumulation.put("vitality", new EventConfig(30, MinegasmConfigDefaults.vitalityConfig));
        accumulation.put("advancement", new EventConfig(20, MinegasmConfigDefaults.advancementConfig));

        Map<String, EventConfig> custom = new HashMap<>();
        custom.put("attack", attackConfig);
        custom.put("hurt", hurtConfig);
        custom.put("mine", mineConfig);
        custom.put("place", placeConfig);
        custom.put("xpChange", xpChangeConfig);
        custom.put("fishing", fishingConfig);
        custom.put("harvest", harvestConfig);
        custom.put("vitality", vitalityConfig);
        custom.put("advancement", advancementConfig);

        return switch (mode) {
            case NORMAL -> normal.get(type);
            case MASOCHIST -> masochist.get(type);
            case HEDONIST -> hedonist.get(type);
            case ACCUMULATION -> accumulation.get(type);
            case GLOBAL_ACCUMULATION -> accumulation.get(type);
            case CUSTOM -> custom.get(type);
            default -> custom.get(type);
        };
    }
    
    public static class EventConfig {
        public int intensity;
        public float duration;
        public int feedbackBonus;
        public float feedbackDuration;
        public int streakExtender;
        
        public static final StreamCodec<FriendlyByteBuf, EventConfig> STREAM_CODEC = StreamCodec.ofMember(EventConfig::write, EventConfig::read);
        
        public EventConfig(int intensity, float duration, int feedbackBonus, float feedbackDuration, int streakExtender) {
            this.intensity = intensity;
            this.duration = duration;
            this.feedbackBonus = feedbackBonus;
            this.feedbackDuration = feedbackDuration;
            this.streakExtender = streakExtender;
        }
        
        EventConfig(int intensity, EventConfig src) {
            this.intensity = intensity;
            this.duration = src.duration;
            this.feedbackBonus = src.feedbackBonus;
            this.feedbackDuration = src.feedbackDuration;
            this.streakExtender = src.streakExtender;
        }
        
        EventConfig(EventConfig src) {
            this.intensity = src.intensity;
            this.duration = src.duration;
            this.feedbackBonus = src.feedbackBonus;
            this.feedbackDuration = src.feedbackDuration;
            this.streakExtender = src.streakExtender;
        }
        
        private void write(FriendlyByteBuf buf) {
            buf.writeShort(intensity);
            buf.writeFloat(duration);
            buf.writeShort(feedbackBonus);
            buf.writeFloat(feedbackDuration);
            buf.writeShort(streakExtender);
        }
        
        private static EventConfig read(FriendlyByteBuf buf) {
            return new EventConfig(buf.readShort(), buf.readFloat(), buf.readShort(), buf.readFloat(), buf.readShort());
        }
        
        public void copyFrom(EventConfig src) {
            intensity = src.intensity;
            duration = src.duration;
            feedbackBonus = src.feedbackBonus;
            feedbackDuration = src.feedbackDuration;
            streakExtender = src.streakExtender;
        }

    }
    
    public enum GameplayMode {
        NORMAL("gui." + Minegasm.MOD_ID + ".config.mode.normal"),
        MASOCHIST("gui." + Minegasm.MOD_ID + ".config.mode.masochist"),
        HEDONIST("gui." + Minegasm.MOD_ID + ".config.mode.hedonist"),
        ACCUMULATION("gui." + Minegasm.MOD_ID + ".config.mode.accumulation"),
        GLOBAL_ACCUMULATION("gui." + Minegasm.MOD_ID + ".config.mode.global_accumulation"),
        CUSTOM("gui." + Minegasm.MOD_ID + ".config.mode.custom");

        private final String translateKey;

        GameplayMode(String translateKey) {
            this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
        }

        public String getTranslateKey() {
            return this.translateKey;
        }
    }
    
    public enum TickFrequencyOptions {
        EVERY_TICK(1),
        EVERY_OTHER_TICK(2),
        EVERY_5_TICKS(5),
        EVERY_10_TICKS(10),
        EVERY_20_TICKS(20);

        private int value;

        TickFrequencyOptions(int value) {
            this.value = value;
        }

        public static TickFrequencyOptions fromInt(int value) {
            for (TickFrequencyOptions type : values()) {
                if (type.getInt() == value) {
                    return type;
                }
            }
            return null;
        }

        public int getInt() {
            return value;
        }
    }
    
    public enum TriggerType {
        SEPARATE("gui." + Minegasm.MOD_ID + ".config.group.trigger.seperate"), // Controls intensity settings, each player experiences it seperatly
        SHARED("gui." + Minegasm.MOD_ID + ".config.group.trigger.shared"), // Controls intensity settings, each player experiences it together
        DISABLED("gui." + Minegasm.MOD_ID + ".config.group.trigger.disabled"), // Controls intensity settings, each player doesn't experience anything
        UNENFORCED("gui." + Minegasm.MOD_ID + ".config.group.trigger.unenforced"); // Players can choose to use group settings or not 

        private final String translateKey;

        TriggerType(String translateKey) {
            this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
        }

        public String getTranslateKey() {
            return this.translateKey;
        }
    }
}