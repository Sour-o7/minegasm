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

public class MemberScreenBase extends MinegasmScreenListener {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_half_panel.png");
    static final int TEXTURE_WIDTH = 236;
    static final int TEXTURE_HEIGHT = 176;
    final Screen lastScreen;
	String title;

    public MemberScreenBase(Screen lastScreen, MinegasmGroupMember member) {
        super(Component.literal("Member Screen"), member);
		this.lastScreen = lastScreen;
    }
	
	@Override
	public void onMemberUpdate(MinegasmGroupMember newMember) {	
		super.onMemberUpdate(newMember);
		
		if (newMember == null) {
			Minecraft.getInstance().setScreen(new GroupScreen(MinegasmClient.getClientGroup().name));
		}
	}
	
	public void updateMemberModifier(UUID memberUUID, MinegasmModifier modifier) {
		if (member.uuid.equals(memberUUID)) {
			if (modifier == null) {
				member.modifier = null;
			} else {
				member.modifier = new MinegasmModifier(modifier);				
			}
		}
	}
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
	
	@Override
	public void tick() {
		super.tick();
		if (member.modifier != null && member.modifier.duration != -1) {
			member.modifier.duration -= 1;
			if (member.modifier.duration == 0) {
				member.modifier = null;
			}
		}
	}

    @Override
    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
        graphics.blit(GUI_TEXTURE, x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		
        int width = this.minecraft.font.width(title);
        graphics.drawString(this.minecraft.font, title, (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
    }
	
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
		super.render(graphics, i, j, f);
		
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
		
        Player playerEntity = this.minecraft.level.getPlayerByUUID(member.uuid);
        boolean isUpsideDown = playerEntity != null && LivingEntityRenderer.isEntityUpsideDown(playerEntity);
        boolean drawHat = playerEntity != null && playerEntity.isModelPartShown(PlayerModelPart.HAT);
        PlayerInfo info = this.minecraft.player.connection.getPlayerInfo(member.uuid);
        PlayerFaceRenderer.draw(graphics, info.getSkin().texture(), x + 12, y + 20, 16, drawHat, isUpsideDown);

        int width = this.minecraft.font.width(member.name);		
        graphics.drawString(this.minecraft.font, member.name, x + 12 + 20, y + 19, 0xFFFFFF, false);
		
		String subtext = switch (member.rank) {
			case MinegasmGroupMember.PlayerRank.LEADER -> "Leader";
			case MinegasmGroupMember.PlayerRank.SUBLEADER -> "Sublead";
			case MinegasmGroupMember.PlayerRank.MEMBER -> "Member";
		};
		
		subtext += switch (member.role) {
			case MinegasmGroupMember.PlayerRole.DOM -> ", Dom";
			case MinegasmGroupMember.PlayerRole.SWITCH -> ", Switch";
			case MinegasmGroupMember.PlayerRole.SUB -> ", Sub";
			case MinegasmGroupMember.PlayerRole.DISABLE -> ""; 
		};
		
		graphics.drawString(this.minecraft.font, subtext, x + 12 + 20, y + 30, 0x808080, false);
		
		int textYPos = y + 36;
		
		if (member.modifier == null) {
			graphics.drawString(this.minecraft.font, "No Active Modifier", x + 14, textYPos += 12, 0xFFFFFF, false); 
		} else {
			Player originPlayer = this.minecraft.level.getPlayerByUUID(member.modifier.origin);
			String originName = originPlayer.getName().getString();
			
			String modifierType = switch (member.modifier.type) {
				case MinegasmModifier.ModifierType.GRANT -> "Grant";
				case MinegasmModifier.ModifierType.BONUS -> "Bonus";
				case MinegasmModifier.ModifierType.OVERRIDE -> "Override";
				case MinegasmModifier.ModifierType.MULTIPLIER -> "Multiplier";
			};
			
			graphics.drawString(this.minecraft.font, "Active Modifer: ", x + 14, textYPos += 12, 0xFFFFFF, false); 
			graphics.drawString(this.minecraft.font, "Origin: " + originName, x + 14, textYPos += 12, 0x808080, false); 
			graphics.drawString(this.minecraft.font, "Type: " + modifierType, x + 14, textYPos += 12, 0x808080, false);
			
			if (member.modifier.type == MinegasmModifier.ModifierType.MULTIPLIER) {
				graphics.drawString(this.minecraft.font, String.format("Amount: %.2f", member.modifier.amount), x + 14, textYPos += 12, 0x808080, false);
			} else {
				graphics.drawString(this.minecraft.font, "Amount: " + (int) member.modifier.amount, x + 14, textYPos += 12, 0x808080, false); 
			}
			
			if (member.modifier.duration != -1) {
				int durationSeconds = (int) Math.ceil(member.modifier.duration / 20.0);
				String formattedTime = String.format("%dm %02ds", durationSeconds / 60, durationSeconds % 60);
				
				graphics.drawString(this.minecraft.font, "Duration: " + formattedTime, x + 14, textYPos += 12, 0x808080, false);
			}
		}
    }
   
    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}
