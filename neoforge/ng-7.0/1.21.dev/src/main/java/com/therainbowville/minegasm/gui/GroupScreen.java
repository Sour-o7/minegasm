package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.config.ConfigContainer;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;

public class GroupScreen extends MinegasmScreenListener {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_selection_panel.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;

    private static final ResourceLocation VIBRATION_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/vibration_icon.png");
    private static final ResourceLocation VIBRATION_OTHER_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/vibration_others_icon.png");
    private static final ResourceLocation GROUP_SYNC_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/group_sync_icon.png");
    private static final ResourceLocation SETTINGS_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/settings_icon.png");
    private static final ResourceLocation LEAVE_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/leave_icon.png");

    private final String name;
    private MinegasmGroupMember selected = null;
	
	PlayerSelectionList playerList;
	Button settingsButton;
	

    public GroupScreen(String name) {
        super(Component.literal(name));
        this.name = name;
    }
	
	@Override
	public void onGroupUpdate(MinegasmGroup group) {
		super.onGroupUpdate(group);
		
		if (group != null) {
			playerList.populateFromGroup(group);			
		}
	}
	
	@Override
	public void onPlayerUpdate(MinegasmGroupMember player) {
		super.onPlayerUpdate(player);
		
		settingsButton.active = player.rank == MinegasmGroupMember.PlayerRank.LEADER;
	}
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int xRight = (this.width + TEXTURE_WIDTH) / 2 - 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
		
        playerList = new PlayerSelectionList(this, Minecraft.getInstance(), 220, 127, y, 24);
        playerList.setRectangle(220, 127, x, y);
        playerList.populateFromGroup(group);
        this.addRenderableWidget(playerList);
        
		// Vibration Toggle
        this.addRenderableWidget(new Button.Builder(Component.literal(""), button -> {
			ConfigContainer.getMinegasmClient().vibrate = !ConfigContainer.getMinegasmClient().vibrate;
            ConfigContainer.bakeClientInstance();
		}).pos(x, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build());
		
		// Use Group Settings
        this.addRenderableWidget(new Button.Builder(Component.literal(""), button -> {
			ConfigContainer.getMinegasmClient().useGroupSettings = !ConfigContainer.getMinegasmClient().useGroupSettings;
            ConfigContainer.bakeClientInstance();
			EventProcessor.refreshReferenceConfig();
		}).pos(x + 24, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build());
		
		// Receive Vibrations from Others
        this.addRenderableWidget(new Button.Builder(Component.literal(""), button -> {
			ConfigContainer.getMinegasmClient().allowFromOthers = !ConfigContainer.getMinegasmClient().allowFromOthers;
            ConfigContainer.bakeClientInstance();
		}).pos(x + 24 * 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build());

		// Settings Button
        settingsButton = new Button.Builder(Component.literal(""), button -> 
            minecraft.setScreen(new GroupSettingsScreen(this, "Edit Group", new MinegasmGroup(MinegasmClient.getClientGroup())))
		).pos(xRight - 20 - 24, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build();
        this.addRenderableWidget(settingsButton);
        settingsButton.active = group.getPlayer(Minecraft.getInstance().player.getUUID()).rank == MinegasmGroupMember.PlayerRank.LEADER;
        
        // Leave Button
        this.addRenderableWidget(new Button.Builder(Component.literal(""), button -> {
            ClientPayloadDispatcher.sendRemoveGroupMemberPayload(group.uuid, player.uuid);
            MinegasmClient.setClientGroup(null);
            Minecraft.getInstance().setScreen(new JoinGroupScreen());
        }).pos(xRight - 20, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build());
    }
   
    
    public void setSelected(PlayerSelectionList.PlayerEntry entry) {
        if (entry == null) {
            this.selected = null;
            return;
        }
        
        this.selected = entry.player;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
        graphics.blit(GUI_TEXTURE, x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        
        int width = this.minecraft.font.width(name);
        graphics.drawString(this.minecraft.font, name, (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
   }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {      
        super.render(graphics, i, j, f);
		int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int xRight = (this.width + TEXTURE_WIDTH) / 2 - 8;
		
        // Vibration Icon
        graphics.pose().pushPose();
        graphics.pose().translate(x + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
        graphics.pose().scale(0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, VIBRATION_ICON);
		int offset = ConfigContainer.getMinegasmClient().vibrate ? 0 : 32;
        graphics.blit(VIBRATION_ICON, 0, 0, offset, 0, 32, 32, 64, 32);
        graphics.pose().popPose();
		
        // Group Sync Icon
        graphics.pose().pushPose();
        graphics.pose().translate(x + 24 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
        graphics.pose().scale(0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, GROUP_SYNC_ICON);
		offset = ConfigContainer.getMinegasmClient().useGroupSettings ? 0 : 32;
        graphics.blit(GROUP_SYNC_ICON, 0, 0, offset, 0, 32, 32, 64, 32);
        graphics.pose().popPose();
        
        // Vibration Other Icon
        graphics.pose().pushPose();
        graphics.pose().translate(x + (24 * 2) + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
        graphics.pose().scale(0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, VIBRATION_OTHER_ICON);
		offset = ConfigContainer.getMinegasmClient().allowFromOthers ? 0 : 32;
        graphics.blit(VIBRATION_OTHER_ICON, 0, 0, offset, 0, 32, 32, 64, 32);
        graphics.pose().popPose();

        // Settings Icon
        graphics.pose().pushPose();
        graphics.pose().translate(xRight - 20 - 24 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
        graphics.pose().scale(0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, SETTINGS_ICON);
        graphics.blit(SETTINGS_ICON, 0, 0, 0, 0, 32, 32, 32, 32);
        graphics.pose().popPose();
        
        // Leave Icon
        graphics.pose().pushPose();
        graphics.pose().translate(xRight - 20 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
        graphics.pose().scale(0.5f, 0.5f, 1);
        RenderSystem.setShaderTexture(0, LEAVE_ICON);
        graphics.blit(LEAVE_ICON, 0, 0, 0, 0, 32, 32, 32, 32);
        graphics.pose().popPose();
    }
    
}
