package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.UUID;

public class MinegasmConfigGroup extends MinegasmConfig<MinegasmConfigGroup.EventConfig> {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

    public final boolean forcedRoles;
    public boolean syncConfig; // Force users to use the group's config
    
    public static final StreamCodec<FriendlyByteBuf, MinegasmConfigGroup> STREAM_CODEC = StreamCodec.ofMember(MinegasmConfigGroup::write, MinegasmConfigGroup::read);
    
    private MinegasmConfigGroup(MinegasmConfig src, boolean forcedRoles, boolean syncConfig) {
        this(true, forcedRoles, syncConfig);
        populateFrom(src);
        this.mode = src.mode;
    }
    
    public MinegasmConfigGroup(MinegasmConfig src, boolean isPrivate, boolean forcedRoles, boolean syncConfig) {
        this(isPrivate, forcedRoles, syncConfig);
        populateFrom(src);
        this.mode = src.mode;
    }
    
    public MinegasmConfigGroup(boolean isPrivate, boolean forcedRoles, boolean syncConfig) {
        this.syncConfig = syncConfig;
        this.mode = MinegasmConfig.GameplayMode.CUSTOM;
        if (isPrivate == false) {
            this.forcedRoles = false;
        } else {
            this.forcedRoles = forcedRoles;            
        }
    }
    
    protected MinegasmConfigGroup() {
        this.forcedRoles = false;
        this.syncConfig = false;
        this.mode = MinegasmConfig.GameplayMode.CUSTOM;
        this.populateFrom(MinegasmConfigDefaults.getDefaultInstance());
    }
    
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
        buf.writeBoolean(forcedRoles);
        buf.writeBoolean(syncConfig);
    }
        
    private static MinegasmConfigGroup read(FriendlyByteBuf buf) {
        return new MinegasmConfigGroup(new MinegasmConfig<EventConfig>(
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
        ), buf.readBoolean(), buf.readBoolean());
    }
    
    @Override
    public void populateFrom(MinegasmConfig src) {
        if (attackConfig == null)
            attackConfig = new EventConfig(src.attackConfig);
        if (hurtConfig == null)
            hurtConfig = new EventConfig(src.hurtConfig);
        if (mineConfig == null)
            mineConfig = new EventConfig(src.mineConfig);
        if (placeConfig == null)
            placeConfig = new EventConfig(src.placeConfig);
        if (xpChangeConfig == null)
            xpChangeConfig = new EventConfig(src.xpChangeConfig);
        if (fishingConfig == null)
            fishingConfig = new EventConfig(src.fishingConfig);
        if (harvestConfig == null)
            harvestConfig = new EventConfig(src.harvestConfig);
        if (vitalityConfig == null)
            vitalityConfig = new EventConfig(src.vitalityConfig);
        if (advancementConfig == null)
            advancementConfig = new EventConfig(src.advancementConfig);        
    }
    
    public void copyFrom(MinegasmConfigGroup src) {
        super.copyFrom(src);
        syncConfig = src.syncConfig;
    }
    
    public void print() {
        LOGGER.info("Mode: " + mode.getTranslateKey());
        LOGGER.info("forcedRoles: " + forcedRoles);
        LOGGER.info("syncConfig: " + syncConfig);
        LOGGER.info("Attack Config: ");
        attackConfig.print();
        LOGGER.info("Hurt Config: ");
        hurtConfig.print();
        LOGGER.info("Mine Config: ");
        mineConfig.print();
        LOGGER.info("Place Config: ");
        placeConfig.print();
        LOGGER.info("XP Config: ");
        xpChangeConfig.print();
        LOGGER.info("Fishing Config: ");
        fishingConfig.print();
        LOGGER.info("Harvest Config: ");
        harvestConfig.print();
        LOGGER.info("Vitality Config: ");
        vitalityConfig.print();
        LOGGER.info("Advancement Config: ");
        advancementConfig.print();
    }

    public static class EventConfig extends MinegasmConfig.EventConfig {
        public TriggerType type;
        public boolean proximityEnabled;
        public boolean broadcastOnly;
        
        public static final StreamCodec<FriendlyByteBuf, EventConfig> STREAM_CODEC = StreamCodec.ofMember(EventConfig::write, EventConfig::read);
        
        public EventConfig(int intensity, float duration, int feedbackBonus, float feedbackDuration, int streakExtender, TriggerType type, boolean proximityEnabled, boolean broadcastOnly) {
            super(intensity, duration, feedbackBonus, feedbackDuration, streakExtender);
            this.type = type;
            this.proximityEnabled = proximityEnabled;
            this.broadcastOnly = broadcastOnly;
        }
        
        public EventConfig(MinegasmConfig.EventConfig config, TriggerType type, boolean proximityEnabled, boolean broadcastOnly) {
            super(config);
            this.type = type;
            this.proximityEnabled = proximityEnabled;
            this.broadcastOnly = broadcastOnly;
        }
        
        public EventConfig(MinegasmConfig.EventConfig config) {
            super(config);
            if (config instanceof EventConfig) {
                this.type = ((EventConfig) config).type;
                this.proximityEnabled = ((EventConfig) config).proximityEnabled;
                this.broadcastOnly = ((EventConfig) config).broadcastOnly;
            } else {
                this.type = TriggerType.SEPARATE;
                this.proximityEnabled = false;
                this.broadcastOnly = false;
            }

        }
        
        public EventConfig(int intensity, MinegasmConfig.EventConfig config) {
            super(intensity, config);
            this.type = TriggerType.SEPARATE;
            this.proximityEnabled = false;
            this.broadcastOnly = false;
        }
        
        private void write(FriendlyByteBuf buf) {
            MinegasmConfig.EventConfig.STREAM_CODEC.encode(buf, this);
            buf.writeEnum(type);
            buf.writeBoolean(proximityEnabled);
            buf.writeBoolean(broadcastOnly);
        }
        
        private static EventConfig read(FriendlyByteBuf buf) {
            return new EventConfig(MinegasmConfig.EventConfig.STREAM_CODEC.decode(buf), buf.readEnum(TriggerType.class), buf.readBoolean(), buf.readBoolean());
        }
        
        public void copyFrom(MinegasmConfig.EventConfig src) {
            intensity = src.intensity;
            duration = src.duration;
            feedbackBonus = src.feedbackBonus;
            feedbackDuration = src.feedbackDuration;
            streakExtender = src.streakExtender;
            if (src instanceof EventConfig) {
                type = ((EventConfig) src).type;
                proximityEnabled = ((EventConfig) src).proximityEnabled;
                broadcastOnly = ((EventConfig) src).broadcastOnly;
            }
        }
        
        public static EventConfig instantiateFrom(EventConfig src) {
            return new EventConfig(src);
        }
        
        public void print() {
            LOGGER.info("Type: " + type.getTranslateKey());
            LOGGER.info("Proximity: " + proximityEnabled);
            LOGGER.info("Broadcast: " + broadcastOnly);
            LOGGER.info("Intensity: " + broadcastOnly);
            LOGGER.info("Duration: " + broadcastOnly);
            LOGGER.info("Feedback Bonus: " + broadcastOnly);
            LOGGER.info("Feedback Duration: " + broadcastOnly);
            LOGGER.info("Streak Extender: " + broadcastOnly);
        }
    }

}