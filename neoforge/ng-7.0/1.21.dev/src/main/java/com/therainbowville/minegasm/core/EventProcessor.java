package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.config.ConfigContainer;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;


// Client side processor
public final class EventProcessor {
    private static final MinegasmConfigClient clientConfig = ConfigContainer.getMinegasmClient();
    private static final Map<String, EventData> activeEvents = new ConcurrentHashMap<>();
    private static MinegasmConfig config = ConfigContainer.getMinegasmClient();
    private static UUID playerID;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private EventProcessor() {}
    
    public static MinegasmConfig getConfig() {
        return config;
    }
    
    public static void refreshReferenceConfig () {
        MinegasmGroup group = MinegasmClient.getClientGroup();
        if (group == null || (!clientConfig.useGroupSettings && !group.config.syncConfig)) {
            config = ConfigContainer.getMinegasmClient();            
            LOGGER.info("Config Type: Client");
            LOGGER.info("Config Mode: " + config.mode.getTranslateKey());
        } else {
            config = group.config;
            LOGGER.info("Config Type: Group");
            LOGGER.info("Config Mode: " + config.mode.getTranslateKey());
        }
    }
     
    public static void setPlayerUUID(UUID id) {
        playerID = id;
    }
    
    // Must use for accumulation mode. Recommended for standard events
    public static void startEvent(String eventType) {
        MinegasmConfig.EventConfig eventConfig = config.getModeConfig(eventType);

        // Add to existing event if accumulation mode is already enabled/exists
        if (config.accumulationModeEnabled() && activeEvents.containsKey(eventType)) { 
            activeEvents.get(eventType).intensity += eventConfig.intensity;
            activeEvents.get(eventType).duration = Math.round(eventConfig.streakExtender * clientConfig.ticksPerSecond);
        } else {
            activeEvents.put(eventType, new EventData(playerID, eventConfig));
        }
        
        ClientPayloadDispatcher.sendEventPayload(eventType, activeEvents.get(eventType));
    }

    // Use for event feedback. Optionally use for non-accumulation mode
    public static void startEvent(String eventType, int intensity, int duration) {
        startEvent(eventType, intensity, duration, playerID);
    }

    public static void startEvent(String eventType, int intensity, int duration, UUID origin) {
        if (duration < 1) { return; }

        if (config.accumulationModeEnabled() && activeEvents.containsKey(eventType)) {
            activeEvents.get(eventType).intensity += intensity;
            activeEvents.get(eventType).duration = Math.max(activeEvents.get(eventType).duration, duration);
        } else {
            activeEvents.put(eventType, new EventData(origin, intensity, duration));            
        }
        
        ClientPayloadDispatcher.sendEventPayload(eventType, activeEvents.get(eventType));
    }

    public static void startFeedbackEvent(String eventType, String rawEventType) {
        startFeedbackEvent(eventType, rawEventType, playerID);
    }

    public static void startFeedbackEvent(String eventType, String rawEventType, UUID origin) {
        MinegasmConfig.EventConfig eventConfig = config.getModeConfig(rawEventType);
        if (eventConfig.intensity == 0) { return; }

        if (config.mode.equals(MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)) {
            activeEvents.put(eventType, new EventData(origin, eventConfig.feedbackBonus, Math.round(eventConfig.feedbackDuration * clientConfig.ticksPerSecond)));
        } else {
            activeEvents.put(eventType, new EventData(origin, eventConfig.intensity + eventConfig.feedbackBonus, Math.round(eventConfig.feedbackDuration * clientConfig.ticksPerSecond)));
        }
        
        ClientPayloadDispatcher.sendEventPayload(eventType, activeEvents.get(eventType));
    }

    public static void processEvents() {
        activeEvents.forEach((eventType, eventData) -> {
            if (eventData.duration > 0) {
                eventData.process();
            } else {
                activeEvents.remove(eventType);
            }
        });
    }
    
    public static void clear() {
        activeEvents.clear();
    }
    
    public static double getIntensity() {
        double intensity = 0;
        for (EventData event : activeEvents.values()) {
            if (config.mode.equals(MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)) {
                intensity += event.getIntensity();
            } else {
                intensity = Math.max(intensity, event.getIntensity());
            }
        }
        return Math.min(100, intensity) / 100;
    }
    
    public static int getIntensityOf(String eventType) {
        if (activeEvents.containsKey(eventType)) {
            return activeEvents.get(eventType).getIntensity();            
        } else {
            return 0;
        }
    }

    public static class EventData {
        final UUID origin;
        int intensity;
        int duration; // Always in ticks, assuming 20 ticks per second. Duration is decremented by tickFrequency.
        private int streakExtender = 0;
        
        public static final StreamCodec<FriendlyByteBuf, EventData> STREAM_CODEC = StreamCodec.ofMember(EventData::write, EventData::read);

        public EventData(UUID origin, int intensity, int duration) {
            this.origin = origin;
            this.intensity = intensity;
            this.duration = duration;
        }
        
        public EventData(UUID origin, int intensity, int duration, int streakExtender) {
            this.origin = origin;
            this.intensity = intensity;
            this.duration = duration;
            this.streakExtender = streakExtender;
        }
        
        public EventData(UUID origin, MinegasmConfig.EventConfig config) {
            this.origin = origin;
            this.intensity = config.intensity;
            this.duration = Math.round(config.duration * clientConfig.ticksPerSecond);
            this.streakExtender = config.streakExtender;
        }
        
        private void write(FriendlyByteBuf buf) {
            buf.writeUUID(origin);
            buf.writeShort(intensity);
            buf.writeShort(duration);
            buf.writeShort(streakExtender);
        }

        private static EventData read(FriendlyByteBuf buf) {
            return new EventData(buf.readUUID(), buf.readShort(), buf.readShort(), buf.readShort());
        }
        
        public void process() {
            if (config.accumulationModeEnabled()) {
                if (duration > 0) {
                    duration = Math.max(0, duration - clientConfig.tickFrequency.getInt());
                }
                
                if (duration < 1 && intensity > 0) {
                    intensity = Math.max(0, intensity - 5);
                    duration = Math.round(streakExtender * clientConfig.ticksPerSecond);
                }
            } else {
                duration = Math.max(0, duration - clientConfig.tickFrequency.getInt());
            }
        }
        
        public int getIntensity() {
            if (duration > 0)
                return intensity;
            else return 0;
        }
    }
    
}   