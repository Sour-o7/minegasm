package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmModifier;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinegasmScreenListener extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public MinegasmScreenListener(Component text) {
        super(text);
    }
	
	public abstract void onGroupUpdate(MinegasmGroup group);
	
	public abstract void onMemberUpdate(MinegasmGroupMember member);
	
	public abstract void onPlayerUpdate(MinegasmGroupMember player);
}
