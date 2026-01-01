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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
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

public class RewardMemberScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/ui_half_panel.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;
    private final GroupScreen lastScreen;
    
    private final MinegasmGroupMember member;
    private MinegasmModifier modifier;

    public RewardMemberScreen(GroupScreen lastScreen, MinegasmGroupMember member) {
        super(Component.literal("Edit Group Member"));
        this.lastScreen = lastScreen;
        this.member = member;
        modifier = new MinegasmModifier(Minecraft.getInstance().player.getUUID());
    }
    
    @Override
    protected void init() { 
        int middle = this.width / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        
        // Info about current modifier?
        
        ExtendedSliderWithListener amountSlider = new ExtendedSliderWithListener(middle + 8, y + 24 + 16, 100, Button.DEFAULT_HEIGHT, "Amount", "", 0, 100, 20, 1, 1, true, (value) -> {
            modifier.amount = value;
        });
        this.addRenderableWidget(amountSlider);
        
        ExtendedSliderWithListener amountSliderMulti = new ExtendedSliderWithListener(middle + 8, y + 24 + 16, 100, Button.DEFAULT_HEIGHT, "Amount", "", 0, 2, 1, 0.05, 1, true, (value) -> {
            modifier.amount = value;
        });
        amountSliderMulti.visible = false;
        this.addRenderableWidget(amountSliderMulti);
        
        this.addRenderableWidget(
            CycleButton.builder((MinegasmModifier.ModifierType type) ->
                Component.literal(switch (type) {
                    case FIXED -> "Set";
                    case BONUS -> "Bonus";
                    case OVERRIDE -> "Override";
                    case MULTIPLIER -> "Multiplier";
                }))
            .withValues(MinegasmModifier.ModifierType.FIXED, MinegasmModifier.ModifierType.BONUS, MinegasmModifier.ModifierType.OVERRIDE, MinegasmModifier.ModifierType.MULTIPLIER)
            .withInitialValue(modifier.type)
            .create(middle + 8, y + 16, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Type"), (button, value) -> {
                modifier.type = value;
                switch (value) {
                    case MULTIPLIER:
                        amountSlider.visible = false;
                        amountSliderMulti.visible = true;
                        break;
                    default:
                        amountSlider.visible = true;
                        amountSliderMulti.visible = false;
                        break;
                }
            })
        );
        
        EditBox durationBox = new EditBox(Minecraft.getInstance().font, middle + 8, y + 48 + 16, 100, Button.DEFAULT_HEIGHT, null);

        this.addRenderableWidget(durationBox);

        durationBox.setResponder(s -> {
            if (!s.equals("")) {
                modifier.duration = Integer.parseInt(s);                
            } else {
                modifier.duration = -1;
            }
        });
        
        durationBox.setFilter((s) -> {
            return s.matches("[0-9]*");
        });
        
        durationBox.setHint(Component.literal(String.format(ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Duration")));
        durationBox.setCanLoseFocus(true);
        durationBox.setMaxLength(3);
        
        Button submitButton = new Button.Builder(Component.literal("Done"), button -> {
            //ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
            this.onClose();
        }).pos((this.width - Button.DEFAULT_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build();

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
        
        Player playerEntity = this.minecraft.level.getPlayerByUUID(member.uuid);
        boolean isUpsideDown = playerEntity != null && LivingEntityRenderer.isEntityUpsideDown(playerEntity);
        boolean drawHat = playerEntity != null && playerEntity.isModelPartShown(PlayerModelPart.HAT);
        PlayerInfo info = this.minecraft.player.connection.getPlayerInfo(member.uuid);
        
        PlayerFaceRenderer.draw(graphics, info.getSkin().texture(), x + 12, y + 20, 16, drawHat, isUpsideDown);
        int width = this.minecraft.font.width(member.name);
        graphics.drawString(this.minecraft.font, member.name, x + 12 + 20, y + 20 + 4, 0xFFFFFF, false);

        graphics.drawString(this.minecraft.font, "Active Modifier: ", x + 12, y + 44, 0xFFFFFF, false); 
        
        graphics.drawString(this.minecraft.font, "Origin: Souro7", x + 16, y + 56, 0x808080, false); 
        graphics.drawString(this.minecraft.font, "Type: Multiplier", x + 16, y + 68, 0x808080, false); 
        graphics.drawString(this.minecraft.font, "Amount: 1.85", x + 16, y + 80, 0x808080, false); 
        graphics.drawString(this.minecraft.font, "Duration: 192", x + 16, y + 92, 0x808080, false); 
        //graphics.drawString(this.minecraft.font, "BowArrowLauncher", x + 24, y + 4, 0x3F3F3F, false); 
    }
   
    @Override
    public void onClose() {
        lastScreen.setSelected(null);
        this.minecraft.setScreen(lastScreen);
    }

}
