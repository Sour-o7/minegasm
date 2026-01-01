package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
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

public class EditMemberScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_panel_small.png");
    private static final int TEXTURE_WIDTH = 158;
    private static final int TEXTURE_HEIGHT = 88;
    private final GroupScreen lastScreen;
    private final MinegasmGroup group;
    private final MinegasmGroupMember player;
    
    private final MinegasmGroupMember member;

    public EditMemberScreen(GroupScreen lastScreen, MinegasmGroupMember member) {
        super(Component.literal("Edit Group Member"));
        this.lastScreen = lastScreen;
        this.group = lastScreen.group;
        this.player = lastScreen.player;
        
        this.member = new MinegasmGroupMember(member);
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int xRight = (this.width + TEXTURE_WIDTH) / 2 - 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        
        CycleButton playerRankButton = CycleButton.builder((MinegasmGroupMember.PlayerRank rank) ->
            Component.literal(switch (rank) {
                case LEADER -> "Leader";
                case SUBLEADER -> "SubLeader";
                case MEMBER -> "Member";
            }))
        .withValues(MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRank.SUBLEADER, MinegasmGroupMember.PlayerRank.MEMBER)
        .withInitialValue(member.rank)
        .displayOnlyValue()
        .create(this.width / 2 - 70 - 2, y + 18, 70, 18,
        Component.literal("Rank"), (button, value) -> {
            member.rank = value;
        });
        playerRankButton.active = player.rank == MinegasmGroupMember.PlayerRank.LEADER;
        this.addRenderableWidget(playerRankButton);
        
        CycleButton playerRoleButton = CycleButton.builder((MinegasmGroupMember.PlayerRole role) ->
            Component.literal(switch (role) {
                case DOM -> "Dominate";
                case SWITCH -> "Switch";
                case SUB -> "Submissive";
                case DISABLE -> "Disabled";
            }))
        .withValues(MinegasmGroupMember.PlayerRole.SWITCH, MinegasmGroupMember.PlayerRole.DOM, MinegasmGroupMember.PlayerRole.SUB, MinegasmGroupMember.PlayerRole.DISABLE)
        .withInitialValue(member.role)
        .displayOnlyValue()
        .create(this.width / 2 + 2, y + 18, 70, 18,
        Component.literal("Role"), (button, value) -> {
            member.role = value;
        });
        playerRoleButton.active = (group.config.forcedRoles && player.rank != MinegasmGroupMember.PlayerRank.MEMBER) || member.uuid.equals(player.uuid);
        this.addRenderableWidget(playerRoleButton);
        
        Button submitButton = new Button.Builder(Component.literal("Done"), button -> {
            ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
            this.onClose();
        }).pos((this.width - Button.SMALL_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build();

        this.addRenderableWidget(submitButton);
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
        
        int width = this.minecraft.font.width(member.name);
        graphics.drawString(this.minecraft.font, member.name, (this.width - width ) / 2, y + 12, 0x3F3F3F, false);
        
        //graphics.drawString(this.minecraft.font, member.name, x + 24, y + 4, 0x3F3F3F, false); 
        //graphics.drawString(this.minecraft.font, "BowArrowLauncher", x + 24, y + 4, 0x3F3F3F, false); 
        
        Player playerEntity = this.minecraft.level.getPlayerByUUID(member.uuid);
        boolean isUpsideDown = playerEntity != null && LivingEntityRenderer.isEntityUpsideDown(playerEntity);
        boolean drawHat = playerEntity != null && playerEntity.isModelPartShown(PlayerModelPart.HAT);
        PlayerInfo info = this.minecraft.player.connection.getPlayerInfo(member.uuid);
        
        PlayerFaceRenderer.draw(graphics, info.getSkin().texture(), (this.width - width ) / 2 - 16 - 4, y + 8, 16, drawHat, isUpsideDown);
    }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        lastScreen.render(graphics, i, j, f);
        super.render(graphics, i, j, f);
   }
   
    @Override
    public void onClose() {
        lastScreen.setSelected(null);
        this.minecraft.setScreen(lastScreen);
    }

}
