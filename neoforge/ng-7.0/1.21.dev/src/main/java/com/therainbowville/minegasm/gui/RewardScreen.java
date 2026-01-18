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

public class RewardScreen extends MemberScreenBase {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private MinegasmModifier modifier;

    public RewardScreen(MemberScreen lastScreen, MinegasmGroupMember member) {
        super(lastScreen, lastScreen.group, member);
		super.title = "Reward/Punish Member";
		modifier = new MinegasmModifier(Minecraft.getInstance().player.getUUID());

		if (member.modifier != null) {
			modifier.amount = member.modifier.amount;
			modifier.type = member.modifier.type;
			if (member.modifier.duration != -1) {
				modifier.duration = member.modifier.duration / 20;				
			}
		}
    }
    
    @Override
    protected void init() { 
        int middle = this.width / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;

        ExtendedSliderWithListener amountSlider = new ExtendedSliderWithListener(middle + 8, y + 24, 100, Button.DEFAULT_HEIGHT, "Amount", "", 0, 100, member.modifier != null && member.modifier.type != MinegasmModifier.ModifierType.MULTIPLIER ? member.modifier.amount : 20, 1, 1, true, (value) -> {
            modifier.amount = value;
        });
		modifier.amount = amountSlider.getValue();
		amountSlider.visible = member.modifier == null || member.modifier.type != MinegasmModifier.ModifierType.MULTIPLIER;
        this.addRenderableWidget(amountSlider);
        
        ExtendedSliderWithListener amountSliderMulti = new ExtendedSliderWithListener(middle + 8, y + 24, 100, Button.DEFAULT_HEIGHT, "Amount", "", 0, 2, member.modifier != null && member.modifier.type == MinegasmModifier.ModifierType.MULTIPLIER ? member.modifier.amount : 1, 0.05, 1, true, (value) -> {
            modifier.amount = value;
        });
        amountSliderMulti.visible = member.modifier != null && member.modifier.type == MinegasmModifier.ModifierType.MULTIPLIER;
        this.addRenderableWidget(amountSliderMulti);
        
        this.addRenderableWidget(
            CycleButton.builder((MinegasmModifier.ModifierType type) ->
                Component.literal(switch (type) {
                    case SET -> "Set";
                    case BONUS -> "Bonus";
                    case OVERRIDE -> "Override";
                    case MULTIPLIER -> "Multiplier";
                }))
            .withValues(MinegasmModifier.ModifierType.SET, MinegasmModifier.ModifierType.BONUS, MinegasmModifier.ModifierType.OVERRIDE, MinegasmModifier.ModifierType.MULTIPLIER)
            .withInitialValue(modifier.type)
            .create(middle + 8, y, 100, Button.DEFAULT_HEIGHT,
            Component.literal("Type"), (button, value) -> {
                modifier.type = value;
                switch (value) {
                    case MULTIPLIER:
                        amountSlider.visible = false;
                        amountSliderMulti.visible = true;
						modifier.amount = amountSliderMulti.getValue();
                        break;
                    default:
                        amountSlider.visible = true;
                        amountSliderMulti.visible = false;
						modifier.amount = amountSlider.getValue();
                        break;
                }
            })
        );
        
        EditBox durationBox = new EditBox(Minecraft.getInstance().font, middle + 8, y + 48, 100, Button.DEFAULT_HEIGHT, null);
		if (modifier.duration != -1) {
			durationBox.setValue(String.valueOf(modifier.duration));
			modifier.duration = modifier.duration * 20;
		}

        this.addRenderableWidget(durationBox);

        durationBox.setResponder(s -> {
            if (!s.equals("")) {
                modifier.duration = Integer.parseInt(s) * 20;
            } else {
                modifier.duration = -1;
            }
        });
        
        durationBox.setFilter((s) -> {
            return s.matches("[0-9]*");
        });
        
        durationBox.setHint(Component.literal(String.format(ChatFormatting.GRAY.toString() + /*ChatFormatting.ITALIC.toString() +*/ "Duration")));
        durationBox.setCanLoseFocus(true);
        durationBox.setMaxLength(3);
		
		int buttonWidth = TEXTURE_WIDTH / 2 - 8 - 4;
        
        this.addRenderableWidget(new Button.Builder(Component.literal("Cancel"), button -> {
            this.onClose();
        }).pos(this.width / 2 - buttonWidth - 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(buttonWidth, Button.DEFAULT_HEIGHT).build());
		
        this.addRenderableWidget(new Button.Builder(Component.literal("Done"), button -> {
			member.modifier = modifier;
            ClientPayloadDispatcher.sendUpdateModifierPayload(group.uuid, member.uuid, member.modifier);
            this.onClose();
        }).pos(this.width / 2 + 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(buttonWidth, Button.DEFAULT_HEIGHT).build());
    }

}
