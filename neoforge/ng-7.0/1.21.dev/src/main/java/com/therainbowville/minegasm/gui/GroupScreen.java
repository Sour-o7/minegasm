package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;

public class GroupScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/group_selection_screen.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;
    private final String name;
    private final MinegasmGroup group = MinegasmClient.getClientGroup();
    
    private MinegasmGroupMember selected = null;
    private Button editUserButton;
    private Button rewardUserButton;

    public GroupScreen(String name) {
        super(Component.literal(name));
        this.name = name;
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int xRight = (this.width + TEXTURE_WIDTH) / 2 - 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        PlayerSelectionList playerList = new PlayerSelectionList(this, Minecraft.getInstance(), 220, 127, y, 24);
        playerList.setRectangle(220, 127, x, y);
        playerList.populateFromGroup(group);
        this.addRenderableWidget(playerList);
    
        rewardUserButton = new Button.Builder(Component.literal("Reward/Punish"), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this))
        ).pos(x, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(100, Button.DEFAULT_HEIGHT).build();
       
        this.addRenderableWidget(rewardUserButton);
        
        editUserButton = new Button.Builder(Component.literal("Edit User"), button -> 
            minecraft.setScreen(new EditGroupMemberScreen(this, selected))
        ).pos(x + 100 + 4, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(68, Button.DEFAULT_HEIGHT).build();
        
        this.addRenderableWidget(editUserButton);
        
        Button settingsButton = new Button.Builder(Component.literal("S"), button -> 
            minecraft.setScreen(new GroupSettingsScreen(this, "Edit Group"))
            ).pos(xRight - 40 - 4, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build();
        this.addRenderableWidget(settingsButton);
        settingsButton.active = group.getPlayer(Minecraft.getInstance().player.getUUID()).rank == MinegasmGroupMember.PlayerRank.LEADER;
        
        // Leave Button
        this.addRenderableWidget(new Button.Builder(Component.literal("L"), button -> {
            ClientPayloadDispatcher.sendLeaveGroupPayload();
            MinegasmClient.setClientGroup(null);
            Minecraft.getInstance().setScreen(new JoinGroupScreen());
        }).pos(xRight - 20, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build());
        
        updateButtonStatus();
    }
    
    public void updateButtonStatus() {
        MinegasmGroupMember player = group.getPlayer(Minecraft.getInstance().player.getUUID());
        
        boolean isCorrectRank = player.rank == MinegasmGroupMember.PlayerRank.LEADER || player.rank == MinegasmGroupMember.PlayerRank.SUBLEADER;
        editUserButton.active = (isCorrectRank || !group.config.forcedRoles) && selected != null;
        
        boolean isCorrectRole = player.role == MinegasmGroupMember.PlayerRole.DOM || player.role == MinegasmGroupMember.PlayerRole.SWITCH;
        rewardUserButton.active = isCorrectRole && selected != null;
    }
    
    public void setSelected(PlayerSelectionList.PlayerEntry entry) {
        if (entry == null) {
            this.selected = null;
            return;
        }
        
        this.selected = entry.player;
        updateButtonStatus();
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
      
        //super.renderBackground(graphics, i, j, f);
   }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
      
        super.render(graphics, i, j, f);
    }
    
}
