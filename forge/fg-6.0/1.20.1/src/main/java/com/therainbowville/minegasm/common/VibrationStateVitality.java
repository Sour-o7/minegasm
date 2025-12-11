package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.ClientConfig;
import com.therainbowville.minegasm.config.MinegasmConfig;
import net.minecraft.world.entity.player.Player;

public class VibrationStateVitality extends AbstractVibrationState {
    private int intensityCooldown;
    private boolean targetMet;

    public VibrationStateVitality() {
        super(1);
        intensityCooldown = 0;
        targetMet = false;
    }

    public void onTick(Player player) {
        float playerHealth = player.getHealth();
        float playerFoodLevel = player.getFoodData().getFoodLevel();

        if ((MinegasmConfig.mode.equals(ClientConfig.GameplayMode.MASOCHIST) && playerHealth > 0 && playerHealth <= 1)
                || (!MinegasmConfig.mode.equals(ClientConfig.GameplayMode.MASOCHIST) && playerHealth >= 20 && playerFoodLevel >= 20)) {

            if (targetMet == false) {
                targetMet = true;
                vibrationFeedbackCountdown = 3 * MinegasmConfig.ticksPerSecond;
            }
        } else
            targetMet = false;

//        if (accumulationEnabled() && targetMet)
//        {
//            if (intensityCooldown == 0) {
//                intensity = Math.min(100, intensity + .1f);
//                intensityCooldown = 10 * 20;
//            }
//            vibrationCountdown = streakCountdownAmount;
//            intensityCooldown = Math.max(0, intensityCooldown - 1);
//        } else 
        if (targetMet) {
            vibrationCountdown = 1;
        }
    }

    public int getIntensity() {
        if (getIntensity("vitality") == 0) return 0;

        //if (accumulationEnabled())
        //return Math.toIntExact(Math.round(intensity));
        //else 
        if (vibrationFeedbackCountdown > 0)
            return Math.min(100, getIntensity("vitality") + 20);
        else if (vibrationCountdown > 0)
            return getIntensity("vitality");
        else return 0;
    }
}