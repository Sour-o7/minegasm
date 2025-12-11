package com.therainbowville.minegasm.mixin;

import com.therainbowville.minegasm.client.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent.XpChange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    private static Logger LOGGER = LogManager.getLogger();

/*    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    public void onHeal(float amount, CallbackInfo ci) {
        if (amount > 0) {
            LivingHealEvent event = new LivingHealEvent((LocalPlayer) (Object) this, amount);
            ClientEventHandler.onHeal(event);
        }
    }*/

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        if (amount > 0) {
            LivingHurtEvent event = new LivingHurtEvent((LocalPlayer) (Object) this, source, amount);
            ClientEventHandler.onHurt(event);
        }
    }

    @Inject(method = "setExperienceValues", at = @At("HEAD"), cancellable = true)
    public void onSetExperienceValues(float xpProgress, int totalXp, int experienceLevel, CallbackInfo ci) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        int amount = totalXp - ((LocalPlayer) (Object) this).totalExperience;
        if (amount > 0) {
            XpChange event = new XpChange((LocalPlayer) (Object) this, amount);
            ClientEventHandler.onXpChange(event);
        }
    }
}