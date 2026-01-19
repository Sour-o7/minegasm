package com.therainbowville.minegasm.mixin;

import com.therainbowville.minegasm.client.ClientEventHandler;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.sugar.Local;

import java.util.Map;

@Mixin(ClientAdvancements.class)
public class ClientAdvancementsMixin {

    private final Minecraft minecraft = Minecraft.getInstance();
    private static Logger LOGGER = LogManager.getLogger();

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/Advancement;display()Ljava/util/Optional;"), cancellable = true)
    public void onUpdate(ClientboundUpdateAdvancementsPacket advancementInfoPacket, CallbackInfo ci, @Local AdvancementNode var4) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }
		
		AdvancementHolder advancement = var4.holder();
		Player player = minecraft.player;
		AdvancementEarnEvent event = new AdvancementEarnEvent(player, advancement);
		ClientEventHandler.onAdvancementEvent(event);
    }
}