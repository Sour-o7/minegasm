package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.MinegasmConfig;

public class VibrationStateAttack extends AbstractVibrationState {
    public VibrationStateAttack() {
        super(3);
    }

    public void onAttack() {
        if (getIntensity("attack") == 0) return;

        if (accumulationEnabled()) {
            intensity = Math.min(100, intensity + 5);
            vibrationCountdown = streakCountdownAmount * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * 0;
        } else {
            vibrationCountdown = 3 * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        }
    }

    public int getIntensity() {
        if (accumulationEnabled())
            return Math.toIntExact(Math.round(intensity));
        else if (vibrationFeedbackCountdown > 0)
            return Math.min(100, getIntensity("attack") + 20);
        else if (vibrationCountdown > 0)
            return getIntensity("attack");
        else return 0;
    }
}