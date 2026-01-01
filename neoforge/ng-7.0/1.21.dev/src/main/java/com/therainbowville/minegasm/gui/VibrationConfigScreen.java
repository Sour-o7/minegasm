package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigGroup;
import com.therainbowville.minegasm.core.MinegasmConfigDefaults;
import com.therainbowville.minegasm.config.ConfigContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents
;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

public class VibrationConfigScreen extends OptionsSubScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinegasmConfig config;
    
    private List<AbstractWidget> optionList;
    private final boolean isConfigGroup;
    
    public VibrationConfigScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options, Component.literal("Minegasm Vibration Config"));
        this.config = ConfigContainer.getMinegasmClient();
        this.isConfigGroup = false;
    }    
    
    public VibrationConfigScreen(Screen lastScreen, String title, MinegasmConfig config) {
        super(lastScreen, Minecraft.getInstance().options, Component.literal(title));
        this.config = config;
        this.isConfigGroup = config instanceof MinegasmConfigGroup;
    }
    
    private List<AbstractWidget> combineColumns(List<AbstractWidget> columnLeft, List<AbstractWidget> columnRight) {
        List<AbstractWidget> out = new ArrayList<AbstractWidget>();
        
        while (columnLeft.size() > 0 && columnRight.size() > 0) {
            out.add(columnLeft.get(0));
            columnLeft.remove(0);
            out.add(columnRight.get(0));
            columnRight.remove(0);
        }
        
        while (columnLeft.size() > 0 && columnRight.size() < 1) {
            out.add(columnLeft.get(0));
            columnLeft.remove(0);
            out.add(null);
        }
        
        while (columnLeft.size() < 1 && columnRight.size() > 0) {
            out.add(null);
            out.add(columnRight.get(0));
            columnRight.remove(0);
        }
        
        return out;
    }
    
    private void addSlider(List<AbstractWidget> list, String name, String tooltip, double min, double max, double currentValue, double stepSize, ExtendedSliderListener listener, ExtendedSliderAccessor accessor) {
        ExtendedSliderWithListener slider = new ExtendedSliderWithListener(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
        name, "", min, max, currentValue, stepSize, 1, true, listener, accessor);
        
        if (tooltip != null) {
            slider.setTooltip(Tooltip.create(Component.literal(tooltip)));
        }
        
        list.add(slider);
    }
    
    private void addLabel(String name, String tooltip, List<AbstractWidget> list) {
        StringWidget label = new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.literal(name), font);
        label.setTooltip(Tooltip.create(Component.literal(tooltip)));
        list.add(label);
    }
    
    private void addEventConfigSliders(MinegasmConfig.EventConfig config, List<AbstractWidget> list) {
        addSlider(list, "Intensity", null, 0, 100, config.intensity, 1, (double value) -> {
            config.intensity = (int) Math.round(value);
        }, () -> config.intensity );
        
        addSlider(list, "Duration", null, 0, 10, config.duration, 0.1, (double value) -> {
            config.duration = (int) Math.round(value);
        }, () -> config.duration );
        
        addSlider(list, "Feedback Bonus", "How much intensity to add for instant feedback", 0, 20, config.feedbackBonus, 1, (double value) -> {
            config.feedbackBonus = (int) Math.round(value);
        }, () -> config.feedbackBonus );
        
        addSlider(list, "Feedback Duration", "How long feedback should last", 0, 10, config.feedbackDuration, 0.1, (double value) -> {
            config.feedbackDuration = (int) Math.round(value);
        }, () -> config.feedbackDuration );
        
        addSlider(list, "Streak Extender", "How much this extends your streak during both accumulation modes", 0, 10, config.streakExtender, 0.1, (double value) -> {
            config.streakExtender = (int) Math.round(value);
        }, () -> config.streakExtender );
    }
    
    private void addEventConfigGroupButtons(MinegasmConfig.EventConfig config, List<AbstractWidget> list) {
        if (config instanceof MinegasmConfigGroup.EventConfig && isConfigGroup) {
            MinegasmConfigGroup.EventConfig eventConfig = (MinegasmConfigGroup.EventConfig) config;
            
            CycleButton proximityButton = CycleButton.onOffBuilder(eventConfig.proximityEnabled)
                .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                Component.literal("Proximety Mode"), (button, value) -> eventConfig.proximityEnabled = value );
                
            CycleButton broadcastButton = CycleButton.onOffBuilder(eventConfig.broadcastOnly)
                .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
                Component.literal("Broadcast Mode"), (button, value) -> eventConfig.broadcastOnly = value );
            
            list.add(CycleButton.builder((MinegasmConfig.TriggerType type) ->
                Component.literal(switch (type) {
                    case SEPARATE -> "Separate";
                    case SHARED -> "Shared";
                    case DISABLED -> "Disabled";
                    case USER_PREFERENCE -> "User Preference";
                }))
            .withValues(MinegasmConfig.TriggerType.SEPARATE, MinegasmConfig.TriggerType.SHARED, MinegasmConfig.TriggerType.DISABLED, MinegasmConfig.TriggerType.USER_PREFERENCE)
            .withInitialValue(eventConfig.type)
            .create(0, 0, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Type"), (button, value) -> {
                eventConfig.type = value;
                proximityButton.active = value == MinegasmConfig.TriggerType.SHARED;
                broadcastButton.active = value == MinegasmConfig.TriggerType.SHARED;
            }));
            
            proximityButton.active = eventConfig.type == MinegasmConfig.TriggerType.SHARED;
            broadcastButton.active = eventConfig.type == MinegasmConfig.TriggerType.SHARED;
           
            list.add(proximityButton);
                
            list.add(broadcastButton);
        }
    }

    @Override
    protected void addOptions() {
        
        List<AbstractWidget> columnLeft = new ArrayList<AbstractWidget>();
        List<AbstractWidget> columnRight = new ArrayList<AbstractWidget>();

        addLabel("Attack", "Activates when you attack another player or mob. Critical hits boost by another 20 points.", columnLeft);
        addEventConfigGroupButtons(config.attackConfig, columnLeft);
        addEventConfigSliders(config.attackConfig, columnLeft);
        
        addLabel("Hurt", "Activates when you receive damage.", columnRight);
        addEventConfigGroupButtons(config.hurtConfig, columnRight);
        addEventConfigSliders(config.hurtConfig, columnRight);
        
        addLabel("Mine", "Activates when you finish mining a block. Mining ores will add a small boost.", columnLeft);
        addEventConfigGroupButtons(config.mineConfig, columnLeft);
        addEventConfigSliders(config.mineConfig, columnLeft);

        addLabel("Place", "Activates when you place a block.", columnRight);
        addEventConfigGroupButtons(config.placeConfig, columnRight);
        addEventConfigSliders(config.placeConfig, columnRight);
        
        addLabel("Harvest", "Activates while you are actively mining a block.", columnLeft);
        addEventConfigGroupButtons(config.harvestConfig, columnLeft);
        addEventConfigSliders(config.harvestConfig, columnLeft);
        
        addLabel("Vitality", "Will activate while you have full health/saturation. Will give a small boost if you heal to full health. While on Masochist Mode, it will flip to activate while you have half a heart.", columnRight);
        addEventConfigGroupButtons(config.vitalityConfig, columnRight);
        addEventConfigSliders(config.vitalityConfig, columnRight);
        
        addLabel("Experience", "Activates when you receive XP. A boost to the duration will be added based on how much XP you received.", columnLeft);
        addEventConfigGroupButtons(config.xpChangeConfig, columnLeft);
        addEventConfigSliders(config.xpChangeConfig, columnLeft);

        addLabel("Advancement", "Triggers upon receiving an advancement. The harder the advancement, the longer it will last.", columnRight);        
        addEventConfigGroupButtons(config.advancementConfig, columnRight);
        addEventConfigSliders(config.advancementConfig, columnRight);

        addLabel("Fishing", "Activates when you successfully catch a fish.", columnLeft);        
        addEventConfigGroupButtons(config.fishingConfig, columnLeft);
        addEventConfigSliders(config.fishingConfig, columnLeft);

        optionList = combineColumns(columnLeft, columnRight);        
        list.addSmall(optionList);
    }
    
    @Override
    protected void addFooter() {
        this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> this.onClose()).pos(this.width / 2 + 5, this.height - 26).size(150, 20).build());
        this.addRenderableWidget(new Button.Builder(Component.literal("Reset Values"), button -> {
            config.attackConfig.copyFrom(MinegasmConfigDefaults.attackConfig);
            config.hurtConfig.copyFrom(MinegasmConfigDefaults.hurtConfig);
            config.mineConfig.copyFrom(MinegasmConfigDefaults.mineConfig);
            config.placeConfig.copyFrom(MinegasmConfigDefaults.placeConfig);
            config.xpChangeConfig.copyFrom(MinegasmConfigDefaults.xpChangeConfig);
            config.harvestConfig.copyFrom(MinegasmConfigDefaults.harvestConfig);
            config.fishingConfig.copyFrom(MinegasmConfigDefaults.fishingConfig);
            config.vitalityConfig.copyFrom(MinegasmConfigDefaults.vitalityConfig);
            config.advancementConfig.copyFrom(MinegasmConfigDefaults.advancementConfig);
            
            optionList.forEach((obj) -> {
                if (obj instanceof ExtendedSliderWithListener) {
                    ((ExtendedSliderWithListener) obj).refreshValue();
                }
            });
        }).pos(this.width / 2 - 155, this.height - 27).size(150, 20).build());
    }
    
}