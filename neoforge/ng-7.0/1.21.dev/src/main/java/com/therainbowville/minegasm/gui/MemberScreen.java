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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;

public class MemberScreen extends MemberScreenBase {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private final GroupScreen lastScreen;
	final MinegasmGroupMember player;
	
	Button resetModifierButton;
	Button changeModifierButton;
	
    public MemberScreen(GroupScreen lastScreen, MinegasmGroupMember member) {
		super(lastScreen, lastScreen.group, member);
		super.title = "View Member";
		this.lastScreen = lastScreen;
        this.player = lastScreen.player;
		ClientPayloadDispatcher.sendRequestModifierPayload(group.uuid, member.uuid);
    }
	
	@Override
	public void tick() {
		super.tick();
		
		boolean playerDom = player.role == MinegasmGroupMember.PlayerRole.DOM || player.role == MinegasmGroupMember.PlayerRole.SWITCH;
		boolean memberSub = member.role == MinegasmGroupMember.PlayerRole.SUB || member.role == MinegasmGroupMember.PlayerRole.SWITCH;
		
		changeModifierButton.active = playerDom && memberSub;
		resetModifierButton.active = member.modifier != null && playerDom && memberSub;
	}
    
    @Override
    protected void init() { 
		int middle = this.width / 2;
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        
        CycleButton playerRoleButton = CycleButton.builder((MinegasmGroupMember.PlayerRole role) ->
            Component.literal(switch (role) {
                case DOM -> "Dominate";
                case SWITCH -> "Switch";
                case SUB -> "Submissive";
                case DISABLE -> "Disabled";
            }))
        .withValues(MinegasmGroupMember.PlayerRole.SWITCH, MinegasmGroupMember.PlayerRole.DOM, MinegasmGroupMember.PlayerRole.SUB, MinegasmGroupMember.PlayerRole.DISABLE)
        .withInitialValue(member.role)
        //.displayOnlyValue()
        .create(middle + 8, y, 100, Button.DEFAULT_HEIGHT,
        Component.literal("Role"), (button, value) -> {
            member.role = value;
			ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
        });
        playerRoleButton.active = (group.config.forcedRoles && player.rank != MinegasmGroupMember.PlayerRank.MEMBER) || (member.uuid.equals(player.uuid) && !group.config.forcedRoles);
        this.addRenderableWidget(playerRoleButton);
		
		Button manageUserButton  = new Button.Builder(Component.literal("Manage User"), button -> {
			minecraft.setScreen(new ManageMemberScreen(this, member));
        }).pos(middle + 8, y + 24).size(100, Button.DEFAULT_HEIGHT).build();
		
		manageUserButton.active = this.player.rank != MinegasmGroupMember.PlayerRank.MEMBER;
		// Commented out for testing, re-add for release
		//manageUserButton.active = manageUserButton.active && member.rank != MinegasmGroupMember.PlayerRank.LEADER;
		
		this.addRenderableWidget(manageUserButton);
		
		changeModifierButton = new Button.Builder(Component.literal("Change Modifier"), button -> {
			minecraft.setScreen(new RewardScreen(this, member));
        }).pos(middle + 8, y + 48 + 8).size(100, Button.DEFAULT_HEIGHT).build();
        this.addRenderableWidget(changeModifierButton);
		
		resetModifierButton = new Button.Builder(Component.literal("Reset Modifier"), button -> {
			member.modifier = null;
			ClientPayloadDispatcher.sendUpdateModifierPayload(group.uuid, member.uuid, member.modifier);
        }).pos(middle + 8, y + 72 + 8).size(100, Button.DEFAULT_HEIGHT).build();
        this.addRenderableWidget(resetModifierButton);

        
        this.addRenderableWidget(new Button.Builder(Component.literal("Done"), button -> {
            ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
            this.onClose();
        }).pos((this.width - Button.DEFAULT_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build());
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
	
    @Override
    public void onClose() {
		lastScreen.setSelected(null);	
        this.minecraft.setScreen(lastScreen);
    }
}
