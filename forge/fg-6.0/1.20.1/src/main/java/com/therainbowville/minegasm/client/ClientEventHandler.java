package com.therainbowville.minegasm.client;

import com.therainbowville.minegasm.common.*;
import com.therainbowville.minegasm.config.MinegasmConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Minegasm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEventHandler {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private static int tickCounter = -1;
    private static int clientTickCounter = -1;
    private static boolean paused = false;
    private static UUID playerId = null;

    private static Map<String, AbstractVibrationState> vibrationStates = new HashMap<String, AbstractVibrationState>();

    static {
        vibrationStates.put("advancement", new VibrationStateAdvancement());
        vibrationStates.put("attack", new VibrationStateAttack());
        vibrationStates.put("fish", new VibrationStateFish());
        vibrationStates.put("hurt", new VibrationStateHurt());
        vibrationStates.put("mine", new VibrationStateMine());
        vibrationStates.put("place", new VibrationStatePlace());
        vibrationStates.put("harvest", new VibrationStateHarvest((VibrationStateMine) vibrationStates.get("mine")));
        vibrationStates.put("vitality", new VibrationStateVitality());
        vibrationStates.put("xpChange", new VibrationStateXpChange());
        vibrationStates.put("generic", new VibrationStateClient());
    }

    public static void afterConnect() {
        //setState(getStateCounter(), 5);
        ((VibrationStateClient) vibrationStates.get("generic")).setVibration(5, 1);
    }

    private static double getIntensity() {
        double intensity = 0;
        for (Map.Entry<String, AbstractVibrationState> state : vibrationStates.entrySet()) {
            intensity = Math.max(intensity, state.getValue().getIntensity());
//            LOGGER.info(state.getKey() + ": " + state.getValue().getIntensity());

        }
        return intensity / 100;
    }

    private static void tickAll() {
        for (AbstractVibrationState state : vibrationStates.values()) {
            state.onTick();
        }
    }

    private static void resetAll() {
        for (AbstractVibrationState state : vibrationStates.values()) {
            state.resetState();
        }
    }

    private static boolean isPlayer(Entity entity) {
        try {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                ;
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
        resetAll();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        try {
            if (event.phase == TickEvent.Phase.END && isPlayer(event.player)) {
                Player player = event.player;

                tickCounter = (tickCounter + 1) % 100;


                if (tickCounter % MinegasmConfig.tickFrequency == 0) // TODO: Add ticks per second config option (Default: Every tick)
                {
                    tickAll();

                    ((VibrationStateVitality) vibrationStates.get("vitality")).onTick(player);
                    ((VibrationStateFish) vibrationStates.get("fish")).onTick(player);

                    double newVibrationLevel = getIntensity();

                    if (ToyController.currentVibrationLevel != newVibrationLevel)
                        ToyController.setVibrationLevel(newVibrationLevel);
                }

            }
        } catch (Throwable e) {
            LOGGER.throwing(e);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (tickCounter >= 0) {
                if (tickCounter != clientTickCounter) {
                    clientTickCounter = tickCounter;
                    paused = false;
                } else {
                    if (!paused) {
                        paused = true;
                        LOGGER.debug("Pausing");
                        ToyController.setVibrationLevel(0);
                    }

                    if (paused) {
                        LOGGER.trace("Paused");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStateAttack) vibrationStates.get("attack")).onAttack();
        }
    }

    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        LOGGER.debug("Critical: " + event.isVanillaCritical());
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStateHurt) vibrationStates.get("hurt")).onHurt();
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (isPlayer(event.getPlayer())) {
            ((VibrationStateMine) vibrationStates.get("mine")).onBreak(event.getState());
        }
    }

    // Triggers when player starts to break block
    @SubscribeEvent
    public static void onHarvest(PlayerEvent.HarvestCheck event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStateHarvest) vibrationStates.get("harvest")).onHarvest();
        }
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStatePlace) vibrationStates.get("place")).onPlace();
        }
    }

    public static void onPlace() {
        ((VibrationStatePlace) vibrationStates.get("place")).onPlace();
    }


    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        LOGGER.info("Pickup item: " + event.getItem().toString());
    }

    @SubscribeEvent
    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        //LOGGER.info("Pickup XP: " + event.getOrb().xpValue);
    }

    @SubscribeEvent
    public static void onXpChange(PlayerXpEvent.XpChange event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStateXpChange) vibrationStates.get("xpChange")).onXpChange(((Player) event.getEntity()).totalExperience, event.getAmount());
        }
    }


    @SubscribeEvent
    public static void onAdvancementEvent(AdvancementEvent.AdvancementEarnEvent event) {
        if (isPlayer(event.getEntity())) {
            ((VibrationStateAdvancement) vibrationStates.get("advancement")).onAdvancement(event);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) {
            return;
        }

        if (entity instanceof Player) {
            LOGGER.info("Client Entered world: " + entity.toString());

            try {
                Player player = (Player) entity;
                UUID uuid = player.getGameProfile().getId();

                if (uuid.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
                    LOGGER.info("Player in: " + player.getGameProfile().getName() + " " + player.getGameProfile().getId().toString());
                    clearState();
                    ToyController.setVibrationLevel(0);
                    playerId = uuid;
                }
            } catch (Throwable e) {
                LOGGER.throwing(e);
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
            LOGGER.info("Player respawn world: " + entity.toString());

            new Thread(() -> {
                try {
                    Player player = (Player) entity;
                    UUID uuid = player.getGameProfile().getId();

                    if (uuid.equals(Minecraft.getInstance().player.getGameProfile().getId())) {
                        LOGGER.info("Player in: " + player.getGameProfile().getName() + " " + player.getGameProfile().getId().toString());
                        LOGGER.info("Stealth: " + MinegasmConfig.stealth);
                        if (ToyController.connectDevice()) {
                            ((VibrationStateClient) vibrationStates.get("generic")).setVibration(5, 1);
                            if (!MinegasmConfig.stealth) {
                                player.displayClientMessage(Component.literal(String.format("Connected to " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + " [%d]", ToyController.getDeviceName(), ToyController.getDeviceId())), true);
                            }
                        } else if (!MinegasmConfig.stealth) {
                            player.displayClientMessage(Component.literal(String.format(ChatFormatting.YELLOW + "Minegasm " + ChatFormatting.RESET + "failed to start\n%s", ToyController.getLastErrorMessage())), false);
                        }
                        playerId = uuid;
                    }
                } catch (Throwable e) {
                    LOGGER.throwing(e);
                }
            }).start();
        }
    }
}

