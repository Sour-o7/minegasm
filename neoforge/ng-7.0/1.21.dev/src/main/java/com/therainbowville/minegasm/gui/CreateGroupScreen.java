package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.client.ToyController;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.core.MinegasmConfigGroup;
import com.therainbowville.minegasm.core.MinegasmConfigDefaults;
import com.therainbowville.minegasm.config.ConfigContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class CreateGroupScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/group_config_screen.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;
    private final Screen lastScreen;

    public CreateGroupScreen(Screen lastScreen) {
        super(Component.literal("Create Screen"));
        this.lastScreen = lastScreen;
    }
    
    static boolean temp1 = false;
    static boolean temp2 = false;
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
        
        EditBox name = new EditBox(Minecraft.getInstance().font, (this.width - 210) / 2, y + 16, 210, 16, null);
        name.setHint(Component.literal(ChatFormatting.GRAY + "Name"));
        name.setCanLoseFocus(true);
        this.addRenderableWidget(name);
        
        EditBox password = new EditBox(Minecraft.getInstance().font, (this.width - 210) / 2, y + 32 + 2, 210, 16, null);
        password.setHint(Component.literal(ChatFormatting.GRAY + "Password (Optional)"));
        password.setCanLoseFocus(true);
        this.addRenderableWidget(password);
            
        this.addRenderableWidget(CycleButton.onOffBuilder(temp1)
            .create(this.width / 2 - 100 - 5, y + 64, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Sync Config"), (button, value) -> temp1 = value));
            
        this.addRenderableWidget(CycleButton.onOffBuilder(temp2)
            .create(this.width / 2 + 5, y + 64, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Forced Roles"), (button, value) -> temp2 = value));
            
        MinegasmConfigGroup config = new MinegasmConfigGroup(false, false, false);
        config.populateFrom(MinegasmConfigDefaults.getDefaultInstance());
            
        this.addRenderableWidget(new Button.Builder(Component.literal("Edit Config..."), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this, "Minegasm Group Config", config))
            ).pos((this.width - Button.DEFAULT_WIDTH) / 2, y + 64 + 24).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build()
        );
    
        this.addRenderableWidget(new Button.Builder(Component.literal("Create Group"), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this))
        ).pos((this.width - Button.DEFAULT_WIDTH)/ 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build());

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
        int width = this.minecraft.font.width("Create Group");
        graphics.drawString(this.minecraft.font, "Create Group", (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
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
