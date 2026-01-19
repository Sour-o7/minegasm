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
	
	final MinegasmGroup group;
	final MinegasmGroupMember player;
	final MinegasmGroupMember member;
	
    public MinegasmScreenListener(Component text) {
		super(text);
		group = MinegasmClient.getClientGroup();
		if (group != null) {
			player = group.getPlayer(Minecraft.getInstance().player.getUUID());
		} else {
			player = null;
		}
		member = null;
    }

    public MinegasmScreenListener(Component text, MinegasmGroup group) {
		super(text);
		this.group = group;
		this.player = group.getPlayer(Minecraft.getInstance().player.getUUID());
		member = null;
    }
	
    public MinegasmScreenListener(Component text, MinegasmGroupMember member) {
		super(text);
		this.group = MinegasmClient.getClientGroup();
		this.player = group.getPlayer(Minecraft.getInstance().player.getUUID());
		this.member = member;
    }
	
	public void onGroupUpdate(MinegasmGroup newGroup) {
		if (newGroup == null) {
			Minecraft.getInstance().setScreen(new JoinGroupScreen());
		} else if (group.uuid.equals(newGroup.uuid)) {
			group.copyFrom(newGroup);
			
			if (!group.getPlayer(player.uuid).equals(player)) {
				onPlayerUpdate(group.getPlayer(player.uuid));
			}
			
			if (member != null) {
				MinegasmGroupMember newMember = group.getPlayer(member.uuid);
				if (newMember != null) {
					if (!newMember.equals(member)) {
						onMemberUpdate(newMember);
					}
				} else {
					onMemberUpdate(null);
				}
			}
		}
	}
	
	public void onPlayerUpdate(MinegasmGroupMember player) {
		player.copyFrom(player);
	}
	
	public void onMemberUpdate(MinegasmGroupMember member) {
		if (this.member != null) {
			this.member.copyFrom(member);
		}
	}
}