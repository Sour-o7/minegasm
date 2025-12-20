package com.therainbowville.minegasm.client;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.config.ConfigContainer;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;

@EventBusSubscriber(modid = Minegasm.MOD_ID)
public class ClientEventHandler {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private static int tickCounter = -1;
    private static int clientTickCounter = -1;
    private static boolean paused = false;
    private static UUID playerId;

    private static MinegasmConfigClient minegasmConfig = ConfigContainer.getMinegasmClient();
   

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

    private static void clearState() {
        tickCounter = -1;
        clientTickCounter = -1;
        paused = false;
        EventProcessor.clear();
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        try {
            if (isPlayer(event.getEntity())) {
                Player player = event.getEntity();

                tickCounter = (tickCounter + 1) % 100;
                if (tickCounter % minegasmConfig.tickFrequency.getInt() == 0)
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
    }
    
    // This event runs every tick. Is there a way to optimize it?
    // Replaced with onItemFished. Triggers when the player catches an item, not when bobber is ready, but should probably be better for preformance. Dunno how to measure that
   /* @SubscribeEvent
    public static void onTickFishing(PlayerTickEvent.Post event) {
        if (minegasmConfig.getModeConfig("fishing").intensity == 0) { return; }
        
        Player player = event.getEntity();
        if(isPlayer(player)) {
            if (player.fishing != null) {
                Vec3 vector = player.fishing.getDeltaMovement();
                double x = vector.x();
                double y = vector.y();
                double z = vector.z();
                if (y < -0.075 && !player.level().getFluidState(player.fishing.blockPosition()).isEmpty() && x == 0 && z == 0) {
                    EventProcessor.startEvent("fishing");  
                }
            }
        }
    }*/
    
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (isPlayer(event.getEntity())) {
            EventProcessor.startEvent("attack");
            EventProcessor.startFeedbackEvent("attackFeedback", "attack");
        }
    }
    
    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (isPlayer(event.getEntity()) && !minegasmConfig.accumulationModeEnabled()) {
            if (event.isCriticalHit()) {
                int criticalFeedback = minegasmConfig.getModeConfig("attack").intensity + minegasmConfig.getModeConfig("attack").feedbackBonus + 20;
                // Needs to be registered on startEvent since it uses custom intensity and duration                
                EventProcessor.startEvent("attackFeedback", criticalFeedback, Math.round(minegasmConfig.attackConfig.feedbackDuration * minegasmConfig.ticksPerSecond));
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
        if (isPlayer(event.getEntity())) {
            if (minegasmConfig.accumulationModeEnabled()) {
                EventProcessor.startEvent("harvest", EventProcessor.getIntensityOf("mine") + minegasmConfig.getModeConfig("harvest").intensity, 3);
            } else {
                EventProcessor.startEvent("harvest", minegasmConfig.getModeConfig("harvest").intensity, 3);
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
        if (minegasmConfig.getModeConfig("xpChange").intensity == 0) { return; }
        
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
            
            if (minegasmConfig.accumulationModeEnabled()) {
                EventProcessor.startEvent("xpChange", amount, Math.round(minegasmConfig.getModeConfig("xpChange").duration * minegasmConfig.ticksPerSecond));
            } else {
                int duration = Math.toIntExact(Math.round(Math.ceil(Math.log(amount + 0.5))));
                EventProcessor.startEvent("xpChange", minegasmConfig.getModeConfig("xpChange").intensity, Math.round(duration * minegasmConfig.ticksPerSecond));
                EventProcessor.startEvent("xpChangeFeedback", minegasmConfig.getModeConfig("xpChange").intensity + amount, Math.round(minegasmConfig.getModeConfig("xpChange").feedbackDuration * minegasmConfig.ticksPerSecond));
            }
        }
    }


    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        if (minegasmConfig.getModeConfig("advancement").intensity == 0 ) { return; }
        
        if (isPlayer(event.getEntity())) {
            try {
                Advancement advancement = event.getAdvancement().value();
                AdvancementType type = advancement.display().get().getType();
                int duration = switch (type) {
                    case TASK -> 5;
                    case GOAL -> 7;
                    case CHALLENGE -> 10;
                };
                
                EventProcessor.startEvent("advancement", minegasmConfig.getModeConfig("advancement").intensity, Math.round(duration * minegasmConfig.ticksPerSecond));
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
        if(isPlayer(player)) {
            float playerHealth = player.getHealth();
            float playerFoodLevel = player.getFoodData().getFoodLevel();

            if ((minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST) && playerHealth > 0 && playerHealth <= 1)
            || (!minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST) && playerHealth >= 20 && playerFoodLevel >= 20)) {
                // Only run if user hadn't reached target previous tick
                if (targetMet == false) {
                    targetMet = true;
                    intensityCooldown = 0;
                    EventProcessor.startFeedbackEvent("vitalityFeedback", "vitality");
                }
            } else {
                targetMet = false;                
            }

            if (minegasmConfig.accumulationModeEnabled() && targetMet) {
                if (intensityCooldown == 0) {
                    // Add 1
                    EventProcessor.startEvent("vitality", 1, 1);
                    intensityCooldown = Math.round(rampUpTime / 100 * minegasmConfig.ticksPerSecond);
                } else {
                    // Remain at the same level
                    EventProcessor.startEvent("vitality", 0, 1);
                }

                intensityCooldown = Math.max(0, intensityCooldown - 1);
            } else if (targetMet) {
                EventProcessor.startEvent("vitality", minegasmConfig.getModeConfig("vitality").intensity, 1);
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            return;
        }

        if (isPlayer(entity)) {
            try {
                if (!minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST)) {
                    clearState();
                    ToyController.setVibrationLevel(0);                        
                }
            } catch (Throwable e) {
                LOGGER.throwing(e);
            }
        }
    }
    
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (isPlayer(event.getEntity())) {
            if (minegasmConfig.mode.equals(MinegasmConfig.GameplayMode.MASOCHIST)) {
                EventProcessor.startEvent("death", minegasmConfig.getModeConfig("hurt").intensity + 20, Math.round(minegasmConfig.getModeConfig("hurt").duration * minegasmConfig.ticksPerSecond));                
            }
        }
    }

    @SubscribeEvent
    public static void onWorldEntry(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            return;
        }

        if (ToyController.isConnected) return;

        if (entity instanceof Player) {
            new Thread(() -> {
                try {
                    Player player = (Player) entity;
                    UUID uuid = player.getGameProfile().getId();

                    if (uuid.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
                        playerId = uuid;
                        EventProcessor.setPlayerUUID(playerId);
                        if (ToyController.connectDevice()) {
                            EventProcessor.startEvent("loadInEvent", 5, 1 * minegasmConfig.tickFrequency.getInt());

                            if (minegasmConfig.showChatMessages) {
                                player.displayClientMessage(Component.literal(String.format("Connected to " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + " [%d]", ToyController.getDeviceName(), ToyController.getDeviceId())), true);
                            }
                        } else if (minegasmConfig.showChatMessages) {
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