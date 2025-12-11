package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.MinegasmConfig;

public class VibrationStatePlace extends AbstractVibrationState {
    public VibrationStatePlace() {
        super(5);
    }

    public void onPlace() {
        if (getIntensity("place") == 0) return;

        if (accumulationEnabled()) {
            intensity = Math.min(100, intensity + .5f);
            vibrationCountdown = streakCountdownAmount * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        } else {
            vibrationCountdown = 3 * MinegasmConfig.ticksPerSecond;
        }
    }

    public int getIntensity() {
        if (accumulationEnabled())
            return Math.toIntExact(Math.round(intensity));
        else if (vibrationFeedbackCountdown > 0)
            return Math.min(100, getIntensity("place") + 20);
        else if (vibrationCountdown > 0)
            return getIntensity("place");
        else return 0;
    }
}