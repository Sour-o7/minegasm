package com.therainbowville.minegasm.core;

public final class MinegasmConfigDefaults {
    public static final class ClientConfig {
        public static final String serverUrl = "ws://localhost:12345/buttplug";
        public static final boolean vibrate = true;
        public static final boolean showChatMessages = true;
        public static final boolean useGroupSettings = false;
        public static final boolean receiveVibrationsFromOthers = true;
        public static final MinegasmConfigClient.TickFrequencyOptions tickFrequency = MinegasmConfigClient.TickFrequencyOptions.EVERY_TICK;
    }
    
    public static final MinegasmConfig.GameplayMode mode = MinegasmConfig.GameplayMode.NORMAL;
    
    // EventConfig(int intensity, float duration, int feedbackBonus, float feedbackDuration, int streakExtender)
    public static final MinegasmConfig.EventConfig attackConfig = new MinegasmConfig.EventConfig(60, 3, 20, 1, 3);
    public static final MinegasmConfig.EventConfig hurtConfig = new MinegasmConfig.EventConfig(0, 3, 20, 1, 3);
    public static final MinegasmConfig.EventConfig mineConfig = new MinegasmConfig.EventConfig(40, 3, 20, 0, 5);
    public static final MinegasmConfig.EventConfig placeConfig = new MinegasmConfig.EventConfig(40, 3, 20, 0, 5);
    public static final MinegasmConfig.EventConfig harvestConfig = new MinegasmConfig.EventConfig(10, 1.5f, 0, 0, 3);
    public static final MinegasmConfig.EventConfig vitalityConfig = new MinegasmConfig.EventConfig(0, 1, 20, 3, 1);
    public static final MinegasmConfig.EventConfig xpChangeConfig = new MinegasmConfig.EventConfig(80, 1, 20, 1, 1);
    public static final MinegasmConfig.EventConfig advancementConfig = new MinegasmConfig.EventConfig(90, 5, 20, 1.5f, 10);
    public static final MinegasmConfig.EventConfig fishingConfig = new MinegasmConfig.EventConfig(50, 1.5f, 0, 0, 10);
    
    public static MinegasmConfig getDefaultInstance() {
        MinegasmConfig inst = new MinegasmConfig();
        inst.attackConfig = attackConfig;
        inst.hurtConfig = hurtConfig;
        inst.mineConfig = mineConfig;
        inst.placeConfig = placeConfig;
        inst.harvestConfig = harvestConfig;
        inst.vitalityConfig = vitalityConfig;
        inst.xpChangeConfig = xpChangeConfig;
        inst.advancementConfig = advancementConfig;
        inst.fishingConfig = fishingConfig;
        return inst;
    }

}