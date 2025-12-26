package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.common.Minegasm;
import com.therainbowville.minegasm.common.MinegasmClient;
import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.network.ClientPayloadDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class JoinGroupScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/group_selection_screen.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;

    private GroupSelectionList groupList;
    private MinegasmGroupInfo selected = null;
    private Button joinGroupButton;

    public JoinGroupScreen() {
        super(Component.literal("Group Selection Screen"));
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        groupList = new GroupSelectionList(this, Minecraft.getInstance(), 220, 127, y, 36);
        groupList.setRectangle(220, 127, x, y);
        
        groupList.setGroupEntries(MinegasmClient.groupInfo);

        this.addRenderableWidget(groupList);
    
        joinGroupButton = new Button.Builder(Component.literal("Join Group"), button -> {
            joinSelectedGroup();
        }).pos(this.width / 2 - 110, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(220 / 2 - 2, Button.DEFAULT_HEIGHT).build();
        
        this.addRenderableWidget(joinGroupButton);
        joinGroupButton.active = false;
        
        this.addRenderableWidget(new Button.Builder(Component.literal("Create Group"), button -> 
            minecraft.setScreen(new GroupSettingsScreen(this, "Create Group"))
            ).pos(this.width / 2 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(220 / 2 - 2, Button.DEFAULT_HEIGHT).build()
        );

    }
    
    public void joinSelectedGroup() {
        if (selected.isPrivate) {
            minecraft.setScreen(new GroupPasswordScreen(this, selected.uuid));
        } else {
            ClientPayloadDispatcher.sendJoinGroupPayload(selected.uuid);
            //minecraft.setScreen(new LoadingScreen(this));
        }
        //this.onClose();
    };
        
    public void setSelected(GroupSelectionList.GroupEntry entry) {
        selected = entry.groupInfo;
        joinGroupButton.active = selected != null;
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
        
        int width = this.minecraft.font.width("Join Group");
        graphics.drawString(this.minecraft.font, "Join Group", (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
    }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
      
        super.render(graphics, i, j, f);
   }

}
