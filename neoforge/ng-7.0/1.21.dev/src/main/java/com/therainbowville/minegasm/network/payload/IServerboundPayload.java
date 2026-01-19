package com.therainbowville.minegasm.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public interface IServerboundPayload extends CustomPacketPayload {
	void handleOnServer(IPayloadContext context);
}