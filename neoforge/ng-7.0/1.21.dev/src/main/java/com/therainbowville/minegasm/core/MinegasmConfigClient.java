package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;

public class MinegasmConfigClient extends MinegasmConfig {
    public String serverUrl;
    public boolean vibrate;
    public boolean showChatMessages;
    public boolean useGroupSettings;
    public boolean receiveVibrationsFromOthers;
    public TickFrequencyOptions tickFrequency;
    public float ticksPerSecond;
    
    public void copyFrom(MinegasmConfigClient src) { 
        
        serverUrl = src.serverUrl;
        vibrate = src.vibrate;
        showChatMessages = src.showChatMessages;
        useGroupSettings = src.useGroupSettings;
        receiveVibrationsFromOthers = src.receiveVibrationsFromOthers;

        tickFrequency = src.tickFrequency;
        ticksPerSecond = Math.max(0, 20f / tickFrequency.getInt());

        super.copyFrom(src);
    }
}