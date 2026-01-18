package com.therainbowville.minegasm.client;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.config.ConfigContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;

@Mod(value = Minegasm.MOD_ID, dist = Dist.CLIENT) 
@EventBusSubscriber(modid = Minegasm.MOD_ID)
public class ClientEventHandler {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private static int tickCounter = -1;
//    private static int clientTickCounter = -1;
//    private static boolean paused = false;
    private static UUID playerId;

    private static MinegasmConfigClient clientConfig = ConfigContainer.getMinegasmClient();
   

    private static boolean isPlayer(Entity entity) {
        try {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                UUID uuid = player.getGameProfile().getId();
                return uuid.equals(playerId);
            }
        } catch (Throwable e) {
            LOGGER.throwing(e);
        }
        return false;
    }

    /*private static void clearState() {
        tickCounter = -1;
        clientTickCounter = -1;
        paused = false;
        EventProcessor.clear();
    }*/

    /*@SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        try {
            if (isPlayer(event.getEntity())) {
                Player player = event.getEntity();

                tickCounter = (tickCounter + 1) % 100;
                if (tickCounter % clientConfig.tickFrequency.getInt() == 0)
                {
                    EventProcessor.processEvents();
                    double newVibrationLevel = EventProcessor.getIntensity();

                    if (ToyController.currentVibrationLevel != newVibrationLevel)
                        ToyController.setVibrationLevel(newVibrationLevel);
                }

            }
        } catch (Throwable e) {
            LOGGER.throwing(e);
        }
    }
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (tickCounter >= 0) {
            if (tickCounter != clientTickCounter) {
                clientTickCounter = tickCounter;
                paused = false;
            } else if (!paused){
                paused = true;
                ToyController.setVibrationLevel(0); // Pause vibrations
            }
        }
    }*/
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (Minecraft.getInstance().isPaused()) {
			ToyController.setVibrationLevel(0);
		} else {
			tickCounter = (tickCounter + 1) % 100;
			if (tickCounter % clientConfig.tickFrequency.getInt() == 0)
			{
				EventProcessor.processEvents();
				double newVibrationLevel = EventProcessor.getIntensity();

				if (ToyController.currentVibrationLevel != newVibrationLevel) {
					ToyController.setVibrationLevel(newVibrationLevel);
				}
			}
		}
	}
    
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (isPlayer(event.getEntity())) {
            EventProcessor.startEvent("attack");
            EventProcessor.startFeedbackEvent("attackFeedback", "attack");
        }
    }
    
    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        MinegasmConfig config = EventProcessor.getConfig();
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("attack");
        
        if (eventConfig.intensity == 0) { return; }
        
        if (isPlayer(event.getEntity()) && !EventProcessor.getConfig().accumulationModeEnabled()) {
            if (event.isCriticalHit()) {
                int criticalFeedback = eventConfig.intensity + eventConfig.feedbackBonus + 20;
                // Needs to be registered on startEvent since it uses custom intensity and duration                
                EventProcessor.startEvent("attackFeedback", criticalFeedback, Math.round(eventConfig.feedbackDuration * clientConfig.ticksPerSecond));
            }
        }
    }
    
    @SubscribeEvent
    public static void onHurt(LivingDamageEvent.Post event) {
        if (isPlayer(event.getEntity())) {
            EventProcessor.startEvent("hurt");
        }
    }
    
    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (isPlayer(event.getPlayer())) {
            String blockName = event.getState().getBlock().getName().getString();
            
            if (blockName.contains("Ore")) {
                EventProcessor.startFeedbackEvent("mineFeedback", "mine");
            }

            EventProcessor.startEvent("mine");
        }
    }
    
    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (isPlayer(event.getEntity())) {
            EventProcessor.startEvent("place");
            EventProcessor.startFeedbackEvent("placeFeedback", "place");
        }
    }

    public static void onPlace() {
        EventProcessor.startEvent("place");
        EventProcessor.startFeedbackEvent("placeFeedback", "place");
    }

    // Triggers when player starts to break block
    @SubscribeEvent
    public static void onHarvest(PlayerEvent.HarvestCheck event) {
        MinegasmConfig config = EventProcessor.getConfig();
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("harvest");
        
        if (isPlayer(event.getEntity())) {
            if (config.accumulationModeEnabled()) {
                EventProcessor.startEvent("mine", 0, 3);
            } else {
                EventProcessor.startEvent("harvest", eventConfig.intensity, 3);
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        if(isPlayer(player)) {
            EventProcessor.startEvent("fishing");  
        }
    }

    private static int lastLevel = -1;
    
    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        MinegasmConfig config = EventProcessor.getConfig();
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("xpChange");
        if (eventConfig.intensity == 0) { return; }
        
        if (isPlayer(event.getEntity())) {
            
            int level = ((Player) event.getEntity()).totalExperience;
            int amount = event.getAmount();
            
            if (lastLevel == -1) {
                lastLevel = level;
            }
            
            if (lastLevel != level) {
                amount *= 2;
                lastLevel = level;
            }
            
            amount = (int) Math.ceil(amount / 2);
            
            if (config.accumulationModeEnabled()) {
                EventProcessor.startEvent("xpChange", amount, Math.round(eventConfig.duration * clientConfig.ticksPerSecond));
            } else {
                int duration = Math.toIntExact(Math.round(Math.ceil(Math.log(amount + 0.5))));
                EventProcessor.startEvent("xpChange", eventConfig.intensity, Math.round(duration * clientConfig.ticksPerSecond));
                EventProcessor.startEvent("xpChangeFeedback", eventConfig.intensity + amount, Math.round(eventConfig.feedbackDuration * clientConfig.ticksPerSecond));
            }
        }
    }


    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("advancement");
        if (eventConfig.intensity == 0 ) { return; }
        
        if (isPlayer(event.getEntity())) {
            try {
                Advancement advancement = event.getAdvancement().value();
                AdvancementType type = advancement.display().get().getType();
                int duration = switch (type) {
                    case TASK -> 5;
                    case GOAL -> 7;
                    case CHALLENGE -> 10;
                };
                
                EventProcessor.startEvent("advancement", eventConfig.intensity, Math.round(duration * clientConfig.ticksPerSecond));
                EventProcessor.startFeedbackEvent("advancementFeedback", "advancement");
            } catch (Throwable e) {
                LOGGER.throwing(e);
            }
        }
    }
    
    // The amount of time in seconds for the user to reach 100 intensity    
    private static final int rampUpTime = 300;
    static private boolean targetMet = false;
    static private int intensityCooldown = 0;
    
    @SubscribeEvent
    public static void onVitalityTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        MinegasmConfig config = EventProcessor.getConfig();
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("vitality");
        
        if(isPlayer(player)) {
            float playerHealth = player.getHealth();
            float playerFoodLevel = player.getFoodData().getFoodLevel();

            if ((config.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST) && playerHealth > 0 && playerHealth <= 1)
            || (!config.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST) && playerHealth >= 20 && playerFoodLevel >= 20)) {
                // Only run if user hadn't reached target previous tick
                if (targetMet == false) {
                    targetMet = true;
                    intensityCooldown = 0;
                    EventProcessor.startFeedbackEvent("vitalityFeedback", "vitality");
                }
            } else {
                targetMet = false;                
            }

            if (config.accumulationModeEnabled() && targetMet) {
                if (intensityCooldown == 0) {
                    // Add 1
                    EventProcessor.startEvent("vitality", 1, 1);
                    intensityCooldown = Math.round(rampUpTime / 100 * clientConfig.ticksPerSecond);
                } else {
                    // Remain at the same level
                    EventProcessor.startEvent("vitality", 0, 1);
                }

                intensityCooldown = Math.max(0, intensityCooldown - 1);
            } else if (targetMet) {
                EventProcessor.startEvent("vitality", eventConfig.intensity, 1);
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        MinegasmConfig config = EventProcessor.getConfig();
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            return;
        }

        if (isPlayer(entity)) {
            try {
                if (!config.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST)) {
                    EventProcessor.clear();
                    ToyController.setVibrationLevel(0);                        
                }
            } catch (Throwable e) {
                LOGGER.throwing(e);
            }
        }
    }
    
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        MinegasmConfig config = EventProcessor.getConfig();
        MinegasmConfig.EventConfig eventConfig = EventProcessor.getEventConfig("hurt");
        if (isPlayer(event.getEntity())) {
            if (config.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST)) {
                EventProcessor.startEvent("death", eventConfig.intensity + 20, Math.round(eventConfig.duration * clientConfig.ticksPerSecond));                
            }
        }
    }

    @SubscribeEvent
    public static void onWorldEntry(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof Player) {
            new Thread(() -> {
                try {
                    Player player = (Player) entity;
                    UUID uuid = player.getGameProfile().getId();

                    if (uuid.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
                        playerId = uuid;
                        EventProcessor.setPlayerUUID(playerId);
                        if (ToyController.connectDevice()) {
                            EventProcessor.startEvent("loadInEvent", 5, 1 * clientConfig.ticksPerSecond);

                            if (clientConfig.showChatMessages) {
                                player.displayClientMessage(Component.literal(String.format("Connected to " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + " [%d]", ToyController.getDeviceName(), ToyController.getDeviceId())), true);
                            }
                        } else if (clientConfig.showChatMessages) {
                            player.displayClientMessage(Component.literal(String.format(ChatFormatting.YELLOW + "Minegasm " + ChatFormatting.RESET + "failed to start\n%s", ToyController.getLastErrorMessage())), false);
                        }
                    }
                } catch (Throwable e) {
                    LOGGER.throwing(e);
                }
            }).start();
        }
    }
}