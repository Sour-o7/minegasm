package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;

import java.util.Objects;

public class MinegasmConfigGroup extends MinegasmConfig<MinegasmConfigGroup.EventConfig> {
    private final boolean isPrivate;
    private final boolean enforcedRoles;
    private boolean syncConfig; // Force users to use the group's config
    
    public MinegasmConfigGroup(boolean isPrivate, boolean enforcedRoles, boolean syncConfig) {
        this.isPrivate = isPrivate;
        this.syncConfig = syncConfig;
        if (isPrivate == false) {
            this.enforcedRoles = false;
        } else {
            this.enforcedRoles = enforcedRoles;            
        }
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

    public static class EventConfig extends MinegasmConfig.EventConfig {
        public TriggerType type;
        public boolean proximityEnabled;
        public boolean broadcastOnly;
        
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
            this.type = TriggerType.SEPARATE;
            this.proximityEnabled = false;
            this.broadcastOnly = false;
        }
        
        public EventConfig(int intensity, MinegasmConfig.EventConfig config) {
            super(intensity, config);
            this.type = TriggerType.SEPARATE;
            this.proximityEnabled = false;
            this.broadcastOnly = false;
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
    }
}