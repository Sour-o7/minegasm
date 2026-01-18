package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class GroupSettingsScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_panel_large.png");
	private static final ResourceLocation DELETE_ICON = ResourceLocation.fromNamespaceAndPath(Minegasm.MOD_ID, "textures/delete_icon.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;
    private final Screen lastScreen;
    private final String title;
    private final MinegasmGroup group;
    private final boolean isCreateGroupScreen;
    
    MinegasmGroup.Builder builder;

    public GroupSettingsScreen(Screen lastScreen, String title) {
        super(Component.literal(title));
        this.lastScreen = lastScreen;
        this.title = title;
        this.isCreateGroupScreen = MinegasmClient.getClientGroup() == null;
        
        if (isCreateGroupScreen) {
            this.group = new MinegasmGroup();
            group.config.mode = MinegasmConfig.GameplayMode.CUSTOM;
        } else {
            this.group = new MinegasmGroup(MinegasmClient.getClientGroup());
        }
        builder = group.new Builder();
    }
    
    private boolean validator() {
        boolean isNameValid = group.name != null && !group.name.equals("");
        boolean isPrivateGroup = group.password != null && !group.password.equals("") || (!isCreateGroupScreen && group.isPrivate);
        boolean isPasswordValid = isCreateGroupScreen || !group.isPrivate || !group.password.equals("");
        boolean isForcedRolesValid = !builder.forcedRoles || isPrivateGroup;
        return isNameValid && isForcedRolesValid && isPasswordValid;
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
        
        Button submitButton = new Button.Builder(Component.literal(isCreateGroupScreen ? "Create Group" : "Done"), button -> 
            saveSettings()
        ).pos((this.width - Button.DEFAULT_WIDTH)/ 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build();
        
        submitButton.active = validator();
        this.addRenderableWidget(submitButton);
        
        EditBox name = new EditBox(Minecraft.getInstance().font, (this.width - 210) / 2, y + 24 + 2, 210, 16, null);
        name.setCanLoseFocus(true);        
        name.setResponder(s -> {
            group.name = s;
            submitButton.active = validator();
        });
        name.setValue(group.name);
        this.addRenderableWidget(name);
        
        EditBox password = new EditBox(Minecraft.getInstance().font, (this.width - 210) / 2, y + 56, 210, 16, null);
        password.setCanLoseFocus(true);
        password.setResponder(s -> {
            group.password = s;
            submitButton.active = validator();
        });
        password.setValue(group.password);
        this.addRenderableWidget(password);

        if (!isCreateGroupScreen) {
            password.active = group.isPrivate;
        }
            
        this.addRenderableWidget(CycleButton.onOffBuilder(group.config.syncConfig)
            .create(this.width / 2 - 100 - 5, y + 76, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Sync Config"), (button, value) -> group.config.syncConfig = value));
            
        CycleButton forcedRolesButton = CycleButton.onOffBuilder(group.config.forcedRoles)
            .create(this.width / 2 + 5, y + 76, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Forced Roles"), (button, value) -> {
                builder.forcedRoles = value;
                submitButton.active = validator();
            });
        forcedRolesButton.active = isCreateGroupScreen;
        this.addRenderableWidget(forcedRolesButton);


        this.addRenderableWidget(
            CycleButton.builder((MinegasmConfig.GameplayMode mode) ->
                Component.literal(switch (mode) {
                    case CUSTOM -> "Normal";
                    case ACCUMULATION -> "Accumulation";
                    case GLOBAL_ACCUMULATION -> "Global Accumulation";
                    default -> "";
                }))
            .withValues(MinegasmConfig.GameplayMode.CUSTOM, MinegasmConfig.GameplayMode.ACCUMULATION, MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION)
            .withInitialValue(group.config.mode)
            .create((this.width - Button.DEFAULT_WIDTH) / 2, y + 76 + 24, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Mode"), (button, value) -> {
                group.config.mode = value;
            })
        );

		// Delete Group Button
        Button deleteButton = new Button.Builder(Component.literal(""), button -> 
            minecraft.setScreen(new DeleteGroupScreen(this, group))
		).pos(x + 8, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build();
		deleteButton.visible = !isCreateGroupScreen;
        this.addRenderableWidget(deleteButton);
        
        this.addRenderableWidget(new Button.Builder(Component.literal("Edit Config..."), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this, "Minegasm Group Config", group.config))
            ).pos((this.width - Button.DEFAULT_WIDTH) / 2, y + 76 + 48).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build()
        );
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
      
        //super.renderBackground(graphics, i, j, f);
        int width = this.minecraft.font.width(this.title);
        graphics.drawString(this.minecraft.font, this.title, (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
        
        graphics.drawString(this.minecraft.font, "Group Name", x + 14, y + 16, 0x3F3F3F, false);
        graphics.drawString(this.minecraft.font, "Password" + (!isCreateGroupScreen && group.isPrivate ? "" :  " (Optional)"), x + 14, y + 46, 0x3F3F3F, false);
   }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);
		
		int x = (this.width - TEXTURE_WIDTH) / 2;
		
        // Delete Icon
		if (!isCreateGroupScreen) {
			graphics.pose().pushPose();
			graphics.pose().translate(x + 8 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8 + 2, 0);
			graphics.pose().scale(0.5f, 0.5f, 1);
			RenderSystem.setShaderTexture(0, DELETE_ICON);
			graphics.blit(DELETE_ICON, 0, 0, 0, 0, 32, 32, 32, 32);
			graphics.pose().popPose();
		}
   }
   
    private void saveSettings() {
        if (isCreateGroupScreen) {
            ClientPayloadDispatcher.sendCreateGroupPayload(builder.build());
        } else {
            ClientPayloadDispatcher.sendUpdateGroupPayload(group);
        }
        //this.onClose();
    }
   
    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }
}
