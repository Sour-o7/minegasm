package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;

public class MinegasmConfigClient extends MinegasmConfig {
    public String serverUrl;
    public boolean vibrate;
    public boolean showChatMessages;
    public boolean useGroupSettings;
    public boolean allowFromOthers;
    public boolean adaptReceivedEvents;
    public TickFrequencyOptions tickFrequency;
    
    
    public final int ticksPerSecond = 20;
    
    public void copyFrom(MinegasmConfigClient src) { 
        
        serverUrl = src.serverUrl;
        vibrate = src.vibrate;
        showChatMessages = src.showChatMessages;
        useGroupSettings = src.useGroupSettings;
        allowFromOthers = src.allowFromOthers;
        adaptReceivedEvents = src.adaptReceivedEvents;

        tickFrequency = src.tickFrequency;

        super.copyFrom(src);
    }
}