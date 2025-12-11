package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.MinegasmConfig;

public class VibrationStateHurt extends AbstractVibrationState {
    public VibrationStateHurt() {
        super(3);
    }

    public void onHurt() {
        if (getIntensity("hurt") == 0) return;

        if (accumulationEnabled()) {
            intensity = Math.min(100, intensity + 10);
            vibrationCountdown = streakCountdownAmount * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        } else {
            vibrationCountdown = 3 * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        }
    }

    public int getIntensity() {
        if (accumulationEnabled())
            return Math.toIntExact(Math.round(intensity));
        else if (vibrationFeedbackCountdown > 0)
            return Math.min(100, getIntensity("hurt") + 20);
        else if (vibrationCountdown > 0)
            return getIntensity("hurt");
        else return 0;
    }
}