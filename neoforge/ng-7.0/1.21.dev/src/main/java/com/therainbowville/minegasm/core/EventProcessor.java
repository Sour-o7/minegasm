package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.config.ConfigContainer;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;


// Client side processor
public final class EventProcessor {
    private static MinegasmConfigClient minegasmConfig = ConfigContainer.getMinegasmClient();
    private static final Map<String, EventData> activeEvents = new ConcurrentHashMap<>();
    private static UUID playerID;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private EventProcessor() {}
     
    public static void setPlayerUUID(UUID id) {
        playerID = id;
    }
    
    // Must use for accumulation mode. Recommended for standard events
    public static void startEvent(String eventType) {
        MinegasmConfig.EventConfig eventConfig = minegasmConfig.getModeConfig(eventType);

        // Add to existing event if accumulation mode is already enabled/exists
        if (ConfigContainer.getMinegasmClient().accumulationModeEnabled() && activeEvents.containsKey(eventType)) { 
            activeEvents.get(eventType).intensity += eventConfig.intensity;
            activeEvents.get(eventType).duration += eventConfig.streakExtender;
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
        if (intensity < 1 || duration < 1) { return; }

        if (ConfigContainer.getMinegasmClient().accumulationModeEnabled() && activeEvents.containsKey(eventType)) {
            activeEvents.get(eventType).intensity += intensity;
            activeEvents.get(eventType).duration = duration;
        } else {
            activeEvents.put(eventType, new EventData(origin, intensity, duration));            
        }
        
        ClientPayloadDispatcher.sendEventPayload(eventType, activeEvents.get(eventType));
    }

    public static void startFeedbackEvent(String eventType, String rawEventType) {
        startFeedbackEvent(eventType, rawEventType, playerID);
    }

    public static void startFeedbackEvent(String eventType, String rawEventType, UUID origin) {
        MinegasmConfig.EventConfig eventConfig = minegasmConfig.getModeConfig(rawEventType);
        if (eventConfig.intensity == 0) { return; }

        if (minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)) {
            activeEvents.put(eventType, new EventData(origin, eventConfig.feedbackBonus, Math.round(eventConfig.feedbackDuration * minegasmConfig.ticksPerSecond)));
        } else {
            activeEvents.put(eventType, new EventData(origin, eventConfig.intensity + eventConfig.feedbackBonus, Math.round(eventConfig.feedbackDuration * minegasmConfig.ticksPerSecond)));
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
            if (minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)) {
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
        final UUID originID;
        int intensity;
        int duration; // In ticks
        private int streakExtender = 0;

        public EventData(UUID origin, int intensity, int duration) {
            this.originID = origin;
            this.intensity = intensity;
            this.duration = duration;
        }
        
        public EventData(UUID origin, MinegasmConfig.EventConfig config) {
            this.originID = origin;
            this.intensity = config.intensity;
            this.duration = Math.round(config.duration * minegasmConfig.ticksPerSecond);
            this.streakExtender = config.streakExtender;
        }
        
        public void process() {
            if (ConfigContainer.getMinegasmClient().accumulationModeEnabled()) {
                if (duration > 0) {
                    duration--;
                } else if (intensity > 0) {
                    intensity = Math.max(0, intensity - 5);
                    duration = Math.round(streakExtender * ConfigContainer.getMinegasmClient().ticksPerSecond);
                }
            } else {
                duration = Math.max(0, duration - 1);
            }
        }
        
        public int getIntensity() {
            if (duration > 0)
                return intensity;
            else return 0;
        }
        
        public int getDuration() {
            return duration;
        }
        
        public UUID getOrigin() {
            return originID;
        }
    }
    
}   