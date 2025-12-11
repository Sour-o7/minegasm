package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.ClientConfig;
import com.therainbowville.minegasm.config.MinegasmConfig;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

// Architecture inspired from https://github.com/Fyustorm/mInetiface
public abstract class AbstractVibrationState {

    protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    protected final float streakCountdownAmount;
    protected float intensity;

    protected float vibrationCountdown;
    protected float vibrationFeedbackCountdown;

    protected AbstractVibrationState(float streakSeconds) {
        streakCountdownAmount = streakSeconds;
        vibrationCountdown = 0;
        vibrationFeedbackCountdown = 0;
        intensity = 0;
    }

    protected static boolean accumulationEnabled() {
        return MinegasmConfig.mode.equals(ClientConfig.GameplayMode.ACCUMULATION);
    }

    public static int getIntensity(String type) {
        Map<String, Integer> normal = new HashMap<>();
        normal.put("attack", 60);
        normal.put("hurt", 0);
        normal.put("mine", 80);
        normal.put("place", 20);
        normal.put("xpChange", 100);
        normal.put("harvest", 10);
        normal.put("fishing", 50);
        normal.put("vitality", 0);
        normal.put("advancement", 100);

        Map<String, Integer> masochist = new HashMap<>();
        masochist.put("attack", 0);
        masochist.put("hurt", 100);
        masochist.put("mine", 0);
        masochist.put("place", 0);
        masochist.put("xpChange", 0);
        masochist.put("fishing", 0);
        masochist.put("harvest", 0);
        masochist.put("vitality", 10);
        masochist.put("advancement", 0);

        Map<String, Integer> hedonist = new HashMap<>();
        hedonist.put("attack", 60);
        hedonist.put("hurt", 10);
        hedonist.put("mine", 80);
        hedonist.put("place", 20);
        hedonist.put("xpChange", 100);
        hedonist.put("fishing", 50);
        hedonist.put("harvest", 20);
        hedonist.put("vitality", 10);
        hedonist.put("advancement", 100);

        Map<String, Integer> accumulation = new HashMap<>();
        accumulation.put("attack", 1);
        accumulation.put("hurt", 1);
        accumulation.put("mine", 1);
        accumulation.put("place", 1);
        accumulation.put("xpChange", 1);
        accumulation.put("fishing", 50);
        accumulation.put("harvest", 10);
        accumulation.put("vitality", 0);
        accumulation.put("advancement", 1);

        Map<String, Integer> custom = new HashMap<>();
        custom.put("attack", MinegasmConfig.attackIntensity);
        custom.put("hurt", MinegasmConfig.hurtIntensity);
        custom.put("mine", MinegasmConfig.mineIntensity);
        custom.put("place", MinegasmConfig.placeIntensity);
        custom.put("xpChange", MinegasmConfig.xpChangeIntensity);
        custom.put("fishing", MinegasmConfig.fishingIntensity);
        custom.put("harvest", MinegasmConfig.harvestIntensity);
        custom.put("vitality", MinegasmConfig.vitalityIntensity);
        custom.put("advancement", MinegasmConfig.advancementIntensity);


        return switch (MinegasmConfig.mode) {
            case NORMAL -> normal.get(type);
            case MASOCHIST -> masochist.get(type);
            case HEDONIST -> hedonist.get(type);
            case ACCUMULATION -> accumulation.get(type);
            case CUSTOM -> custom.get(type);
        };
    }

    public void onTick() {
        if (accumulationEnabled()) {
            if (vibrationCountdown > 0)
                vibrationCountdown--;
            else if (intensity > 0) {
                intensity = Math.max(0, intensity - 5);
                vibrationCountdown = streakCountdownAmount * MinegasmConfig.ticksPerSecond;
            }
        } else {
            vibrationCountdown = Math.max(0, vibrationCountdown - 1);
        }

        vibrationFeedbackCountdown = Math.max(0, vibrationFeedbackCountdown - 1);
    }

    public void resetState() {
        vibrationCountdown = 0;
        vibrationFeedbackCountdown = 0;
        intensity = 0;
    }

    public abstract int getIntensity();
}