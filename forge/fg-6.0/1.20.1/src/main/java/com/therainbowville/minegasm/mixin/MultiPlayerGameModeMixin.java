package com.therainbowville.minegasm.mixin;

import com.therainbowville.minegasm.client.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    private static Logger LOGGER = LogManager.getLogger();

    boolean placedBlock;

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    public void onDestroyBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            BreakEvent event = new BreakEvent(player.level(), blockPos, player.level().getBlockState(blockPos), player);
            ClientEventHandler.onBreak(event);
        }
    }

    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;useOn(Lnet/minecraft/world/item/context/UseOnContext;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void onUseItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (Minecraft.getInstance().isLocalServer()) {
            return;
        }

        if (player.getItemInHand(hand).getItem() instanceof BlockItem) {
            this.placedBlock = true;
        }

    }

    @Inject(method = "performUseItemOn", at = @At("RETURN"), cancellable = true)
    public void onUseItemOnReturn(LocalPlayer player, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir) {
        if (Minecraft.getInstance().isLocalServer() || !this.placedBlock) {
            return;
        }

        if (cir.getReturnValue() == InteractionResult.SUCCESS) {
            ClientEventHandler.onPlace();
        }
        this.placedBlock = false;
    }
}