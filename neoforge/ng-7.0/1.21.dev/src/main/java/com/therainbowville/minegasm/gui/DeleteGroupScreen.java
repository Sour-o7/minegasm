package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;

public class DeleteGroupScreen extends Screen {
	private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

	private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_panel_small.png");
	private static final int TEXTURE_WIDTH = 158;
	private static final int TEXTURE_HEIGHT = 88;
	private final Screen lastScreen;
	private final MinegasmGroup group;

	public DeleteGroupScreen(Screen lastScreen, MinegasmGroup group) {
		super(Component.literal("Delete Group?"));
		this.lastScreen = lastScreen;
		this.group = group;
	}
	
	@Override
	protected void init() { 
		int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
		int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
		
		this.addRenderableWidget(new Button.Builder(Component.literal("Confirm"), button -> {
			ClientPayloadDispatcher.sendRemoveGroupPayload(group.uuid);
			this.onClose();
		}).pos((this.width - Button.SMALL_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT  * 2 - 2 - 8).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build());
			
		this.addRenderableWidget(new Button.Builder(Component.literal("Cancel"), button -> {
			this.onClose();
		}).pos((this.width - Button.SMALL_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build());
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
		
		int width = this.minecraft.font.width("Are you sure");
		graphics.drawString(this.minecraft.font, "Are you sure", (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
		
		width = this.minecraft.font.width("you want to delete");
		graphics.drawString(this.minecraft.font, "you want to delete", (this.width - width ) / 2, y + 5 + 10, 0x3F3F3F, false);
		
		width = this.minecraft.font.width(group.name);
		graphics.drawString(this.minecraft.font, group.name, (this.width - width ) / 2, y + 5 + 10 * 2, 0x3F3F3F, false);
	}
   
	@Override
	public void render(GuiGraphics graphics, int i, int j, float f) {
		super.render(graphics, i, j, f);
   }
   
	@Override
	public void onClose() {
		this.minecraft.setScreen(lastScreen);
	}

}
