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
    private static MinegasmConfig config = ConfigContainer.getMinegasmClient();
    private static final Map<String, EventData> activeEvents = new ConcurrentHashMap<>();
    private static MinegasmModifier activeModifier = null;
    private static UUID playerUUID;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private EventProcessor() {}
    
    public static MinegasmConfig getConfig() {
        return config;
    }
    
    public static MinegasmConfig.EventConfig getEventConfig(String eventType) {
        MinegasmConfig.EventConfig eventConfig = config.getModeConfig(eventType);
        
        if (eventConfig instanceof MinegasmConfigGroup.EventConfig) {
            if (((MinegasmConfigGroup.EventConfig) eventConfig).type == MinegasmConfig.TriggerType.USER_PREFERENCE) {
                eventConfig = clientConfig.getModeConfig(eventType);
            }
        }
        
        return eventConfig;
    }
    
    public static void refreshReferenceConfig () {
        MinegasmGroup group = MinegasmClient.getClientGroup();
        if (group == null || (!clientConfig.useGroupSettings && !group.config.syncConfig)) {
            config = ConfigContainer.getMinegasmClient();
        } else {
            config = group.config;
        }
    }
     
    public static void setPlayerUUID(UUID id) {
        playerUUID = id;
    }
    
    public static EventData adjustToClientConfig(String eventType, EventData event) {
        MinegasmConfig.EventConfig config = clientConfig.getModeConfig(eventType);
        
        /* Special Events that need processing:
        Feedbacks
        Critical feedback - Can be calculated without event data
        harvest - Can be calculated without event data
        xp - Requires xp amount
        advancement - requires advancement type (for duration only)
        vitality - Can be calculated without event data
        death - Can be calculated without event data

        */
        
        event.intensity = config.intensity;
        event.duration = Math.round(config.duration * clientConfig.ticksPerSecond);
        event.streakExtender = config.streakExtender;
        
        return event;
    }
    
    public static void setModifier(MinegasmModifier modifier) {
        activeModifier = modifier;
    }
    
    public static void receiveEvent(String eventType, EventData event) {
        if (!clientConfig.allowFromOthers) { return; }
        
        if (config instanceof MinegasmConfigGroup) {
            MinegasmConfigGroup groupConfig = (MinegasmConfigGroup) config;
            
            if (!groupConfig.syncConfig && clientConfig.adaptReceivedEvents) {
                setEvent(eventType, adjustToClientConfig(eventType, event));
            } else {
                setEvent(eventType, event);
            }
        } else {
            setEvent(eventType, event);
        }
    }
    
    public static void setEvent(String eventType, EventData event) {
        setEvent(eventType, eventType, event);
    }
    
    public static void setEvent(String eventType, String rawEventType, EventData event) {
        if (config.getModeConfig(rawEventType) instanceof MinegasmConfigGroup.EventConfig) {
            MinegasmConfigGroup.EventConfig eventConfig = (MinegasmConfigGroup.EventConfig) config.getModeConfig(rawEventType);
            
            if (eventConfig.type == MinegasmConfig.TriggerType.SHARED) {
                ClientPayloadDispatcher.sendEventPayload(eventType, event);                
            }
            
            if ((eventConfig.type == MinegasmConfig.TriggerType.SHARED || eventConfig.type == MinegasmConfig.TriggerType.SEPARATE) && !eventConfig.broadcastOnly) {
                activeEvents.put(eventType, event);
            } else if (eventConfig.type == MinegasmConfig.TriggerType.USER_PREFERENCE) {
                activeEvents.put(eventType, event);
            }
        } else {
            activeEvents.put(eventType, event);
        }
    }
    
    // Must use for accumulation mode. Recommended for standard events
    public static void startEvent(String eventType) {
        MinegasmConfig.EventConfig eventConfig = getEventConfig(eventType);

        EventData newEvent = null;
        if (config.accumulationModeEnabled() && activeEvents.containsKey(eventType)) {
            newEvent = new EventData(activeEvents.get(eventType));
            newEvent.intensity += eventConfig.intensity;
            newEvent.duration = Math.round(eventConfig.streakExtender * clientConfig.ticksPerSecond);
        } else {
            newEvent = new EventData(playerUUID, eventConfig);
        }
        
        setEvent(eventType, newEvent);
    }

    // Use for event feedback. Optionally use for non-accumulation mode
    public static void startEvent(String eventType, int intensity, int duration) {
        startEvent(eventType, intensity, duration, playerUUID);
    }

    public static void startEvent(String eventType, int intensity, int duration, UUID origin) {
        if (duration < 1) { return; }

        EventData newEvent = null;
        if (config.accumulationModeEnabled() && activeEvents.containsKey(eventType)) {
            newEvent = new EventData(activeEvents.get(eventType));
            newEvent.intensity += intensity;
            newEvent.duration = Math.max(newEvent.duration, duration);
        } else {
            newEvent = new EventData(origin, intensity, duration);           
        }
        
        setEvent(eventType, newEvent);
    }

    public static void startFeedbackEvent(String eventType, String rawEventType) {
        startFeedbackEvent(eventType, rawEventType, playerUUID);
    }

    public static void startFeedbackEvent(String eventType, String rawEventType, UUID origin) {
        MinegasmConfig.EventConfig eventConfig = getEventConfig(rawEventType);
        if (eventConfig.intensity == 0) { return; }
        
        EventData newEvent = null;
        newEvent = new EventData(origin, eventConfig.feedbackBonus, Math.round(eventConfig.feedbackDuration * clientConfig.ticksPerSecond));
        
        if (!config.mode.equals(MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)) {
            newEvent.intensity += eventConfig.intensity;
        }
        
        setEvent(eventType, rawEventType, newEvent);
    }

    public static void processEvents() {
        activeEvents.forEach((eventType, eventData) -> {
            if (eventData.duration > 0) {
                eventData.process();
            } else {
                activeEvents.remove(eventType);
            }
        });
        
        if (activeModifier != null && activeModifier.duration > 0) {
            activeModifier.duration = Math.min(0, activeModifier.duration - clientConfig.tickFrequency.getInt());
            if (activeModifier.duration == 0) {
                activeModifier = null;
            }
        }
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
        
        if (activeModifier != null) {
            switch (activeModifier.type) {
                case MinegasmModifier.ModifierType.FIXED:
                    intensity = Math.max(intensity, activeModifier.amount);
                    break;
                case MinegasmModifier.ModifierType.BONUS:
                    intensity += activeModifier.amount;
                    break;
                case MinegasmModifier.ModifierType.OVERRIDE:
                    intensity = activeModifier.amount;
                    break;
                case MinegasmModifier.ModifierType.MULTIPLIER:
                    intensity *= activeModifier.amount;
                    break;
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

        public EventData(EventData src) {
            this.origin = src.origin;
            this.intensity = src.intensity;
            this.duration = src.duration;
            this.streakExtender = src.streakExtender;
        }

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