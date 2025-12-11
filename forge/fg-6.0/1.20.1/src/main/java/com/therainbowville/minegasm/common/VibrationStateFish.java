package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.config.MinegasmConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VibrationStateFish extends AbstractVibrationState {
    public VibrationStateFish() {
        super(0);
    }

    public void onTick(Player player) {
        if (player.fishing != null) {
            Vec3 vector = player.fishing.getDeltaMovement();
            double x = vector.x();
            double y = vector.y();
            double z = vector.z();
            if (y < -0.075 && !player.level().getFluidState(player.fishing.blockPosition()).isEmpty() && x == 0 && z == 0) {
                vibrationCountdown = 1.5f * MinegasmConfig.ticksPerSecond;
                LOGGER.info("Fishing!");
            }
        }
    }

    public int getIntensity() {
        if (vibrationCountdown > 0)
            return getIntensity("fishing");
        else return 0;
    }
}