package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.client.ToyController;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ClientConfigScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinegasmConfigClient minegasmConfig = ConfigContainer.getMinegasmClient();
    private final Screen lastScreen;

    public ClientConfigScreen(Screen lastScreen) {
        super(Component.literal("Minegasm Config"));
        this.lastScreen = lastScreen;
    }
    
    private int calculateYPos(int row) {
        final int heightFactor = 24;
        final int heightOffset = 30;

        return heightOffset + (row * heightFactor);
    }
    
    private int calculateXPos(int width, int col) {
        return this.width / 2 - 100;
    }

    @Override
    protected void init() {
        // Intiface Url
        EditBox wsHost = new EditBox(Minecraft.getInstance().font, this.width / 2 - 100, calculateYPos(0), 200, Button.DEFAULT_HEIGHT, null);
        wsHost.setValue(minegasmConfig.serverUrl);
        this.addRenderableWidget(wsHost);

        wsHost.setResponder(s -> {
            minegasmConfig.serverUrl = s;
        });

        // Reset Server URL
        this.addRenderableWidget(
            new Button.Builder(Component.literal("Reset Server Url"), button -> {
            minegasmConfig.serverUrl = MinegasmConfigDefaults.ClientConfig.serverUrl;
            wsHost.setValue(minegasmConfig.serverUrl);
        }).pos(this.width / 2 - 155, calculateYPos(1)).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build());
        
        // Connect Response Message
        PlainTextLabel connectResponse = new PlainTextLabel(this.width / 2 - 155, calculateYPos(2), 310, 15, Component.literal("" + ChatFormatting.GREEN));

        this.addRenderableWidget(connectResponse);

        // Reconnect Button
        Button reconnectButton = new Button.Builder(Component.literal("Reconnect"), button -> {
            button.active = false;
            connectResponse.setValue("Connecting");
            new Thread(() -> {
                if (ToyController.connectDevice()) {
                    EventProcessor.startEvent("deviceConnected", 5, Math.round(1 * minegasmConfig.ticksPerSecond));
                    button.active = true;
                    connectResponse.setValue(String.format("Connected to " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + " [%d]", ToyController.getDeviceName(), ToyController.getDeviceId()));
                } else {
                    button.active = true;
                    connectResponse.setValue(String.format(ChatFormatting.YELLOW + "Minegasm " + ChatFormatting.RESET + "failed to start: %s", ToyController.getLastErrorMessage()));
                }
            }).start();
        }).pos(this.width / 2 + 5, calculateYPos(1)).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build();

        this.addRenderableWidget(reconnectButton);

        reconnectButton.active = Minecraft.getInstance().isPaused();

        this.addRenderableWidget(CycleButton.onOffBuilder(minegasmConfig.vibrate)
            //.withTooltip((value) -> Tooltip.create(Component.literal("Toggle vibration on/off")))
            .create(this.width / 2 - 155, calculateYPos(3), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Vibration"), (button, value) -> minegasmConfig.vibrate = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(minegasmConfig.showChatMessages)
            .create(this.width / 2 + 5, calculateYPos(3), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Show Chat Messages"), (button, value) -> minegasmConfig.showChatMessages = value));
            
        this.addRenderableWidget(CycleButton.onOffBuilder(minegasmConfig.allowFromOthers)
            .create(this.width / 2 - 155, calculateYPos(4), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Allow From Others"), (button, value) -> minegasmConfig.allowFromOthers = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(minegasmConfig.useGroupSettings)
            .create(this.width / 2 + 5, calculateYPos(4), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Use Group Settings"), (button, value) -> minegasmConfig.useGroupSettings = value));

        this.addRenderableWidget(
            CycleButton.builder((MinegasmConfig.GameplayMode mode) ->
                Component.literal(switch (mode) {
                    case NORMAL -> "Normal";
                    case MASOCHIST -> "Masochist";
                    case HEDONIST -> "Hedonist";
                    case ACCUMULATION -> "Accumulation";
                    case GLOBAL_ACCUMULATION -> "Global Accumulation";
                    case CUSTOM -> "Custom";
                }))
            .withValues(MinegasmConfig.GameplayMode.NORMAL, MinegasmConfig.GameplayMode.MASOCHIST, MinegasmConfig.GameplayMode.HEDONIST, MinegasmConfig.GameplayMode.ACCUMULATION, MinegasmConfig.GameplayMode.GLOBAL_ACCUMULATION, MinegasmConfig.GameplayMode.CUSTOM)
            .withInitialValue(minegasmConfig.mode)
            .create(this.width / 2 - 155, calculateYPos(5), Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT,
            Component.literal("Mode"), (button, value) -> {
                minegasmConfig.mode = value;
            })
        );

        this.addRenderableWidget(new Button.Builder(Component.literal("Edit Custom Settings..."), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this))
            ).pos(this.width / 2 + 5, calculateYPos(5)).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build()
        );

        this.addRenderableWidget(CycleButton.builder((Integer tickFrequency) ->
            Component.literal(switch (tickFrequency) {
                case 1 -> "Every Tick";
                case 2 -> "Every Other Tick";
                case 5 -> "Every 5 Ticks";
                case 10 -> "Every 10 Ticks";
                case 20 -> "Every Second";
                default -> "error";
            }))
            .withValues(1, 2, 5, 10, 20)
            .withInitialValue(minegasmConfig.tickFrequency.getInt())
            .create(this.width / 2 - 100, calculateYPos(6), 200, Button.DEFAULT_HEIGHT,
            Component.literal("Tick Frequency"), (button, value) -> {
                minegasmConfig.tickFrequency = MinegasmConfig.TickFrequencyOptions.fromInt(value);
            })
        );

        this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> 
            this.onClose()
        ).pos(this.width / 2 - 100, this.height - 26).size(200, Button.DEFAULT_HEIGHT).build());

    }

    @Override
    public void onClose() {
        ConfigContainer.bakeClientInstance();
        EventProcessor.refreshReferenceConfig();
        this.minecraft.setScreen(lastScreen);
//        super.onClose();
        //clientConfig.save();
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        super.render(graphics, i, j, f);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
    }
}