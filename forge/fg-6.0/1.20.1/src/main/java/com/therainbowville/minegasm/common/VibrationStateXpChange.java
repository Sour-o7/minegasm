package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.MinegasmConfig;

public class VibrationStateXpChange extends AbstractVibrationState {
    private int lastLevel;

    public VibrationStateXpChange() {
        super(1);
        lastLevel = -1;
    }

    // Code adapted from https://github.com/Fyustorm/mInetiface
    public void onXpChange(int level, int amount) {
        if (amount == 0 || getIntensity("xpChange") == 0)
            return;

        if (lastLevel == -1) {
            lastLevel = level;
        }

        if (lastLevel != level) {
            amount *= 2;
        }

        lastLevel = level;

        if (accumulationEnabled()) {
            intensity = Math.min(100, intensity + amount / 5);
            vibrationCountdown = streakCountdownAmount * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        } else {
            int duration = Math.toIntExact(Math.round(Math.ceil(Math.log(amount + 0.5))));
            vibrationCountdown = duration * MinegasmConfig.ticksPerSecond;
            vibrationFeedbackCountdown = 1 * MinegasmConfig.ticksPerSecond;
        }
    }

    public int getIntensity() {
        if (accumulationEnabled())
            return Math.toIntExact(Math.round(intensity));
        else if (vibrationFeedbackCountdown > 0)
            return Math.min(100, getIntensity("xpChange") + 20);
        else if (vibrationCountdown > 0)
            return getIntensity("xpChange");
        else return 0;
    }
}

