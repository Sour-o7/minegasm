package com.therainbowville.minegasm.network;

import net.minecraft.world.entity.player.Player;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IServerboundPayload extends CustomPacketPayload {
	void handleOnServer(Player player);
}