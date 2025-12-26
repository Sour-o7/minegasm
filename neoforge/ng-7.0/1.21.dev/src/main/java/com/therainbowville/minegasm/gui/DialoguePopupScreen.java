package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.client.ToyController;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.core.MinegasmConfigDefaults;
import com.therainbowville.minegasm.config.ConfigContainer;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;



public class DialoguePopupScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_panel_small.png");
    private static final int TEXTURE_WIDTH = 158;
    private static final int TEXTURE_HEIGHT = 88;
    
    private String header;
    private String message;

    private final onCloseInterface closeFunc;

    public DialoguePopupScreen(String header, String message, onCloseInterface closeFunc) {
        super(Component.literal("Dialogue Popup"));
        this.header = header;
        this.message = message;
        this.closeFunc = closeFunc;
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        
        
        this.addRenderableWidget(new Button.Builder(Component.literal("Close"), button -> closeFunc.onClose()
        ).pos((this.width - Button.SMALL_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.SMALL_WIDTH, Button.DEFAULT_HEIGHT).build());
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        int x = (this.width - TEXTURE_WIDTH) / 2;
        int xLeft = (this.width + TEXTURE_WIDTH) / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2;
        graphics.blit(GUI_TEXTURE, x, y, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        
        int width = this.minecraft.font.width(header);
        graphics.drawString(this.minecraft.font, Component.literal(header), (this.width - width ) / 2, y + 10, 0x3F3F3F, false);
        
        graphics.drawWordWrap(this.minecraft.font, FormattedText.of(message), x + 8, y + 26, xLeft - x - 16, 0x3F3F3F);
    }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);
   }
   
    public interface onCloseInterface {
        void onClose();
    }
}
