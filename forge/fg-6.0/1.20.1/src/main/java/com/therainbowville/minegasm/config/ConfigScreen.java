package com.therainbowville.minegasm.config;

import com.therainbowville.minegasm.client.ClientEventHandler;
import com.therainbowville.minegasm.client.ToyController;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ConfigScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen previous;
    EditBox wsHost = null;
    private boolean pauseMenu;

    public ConfigScreen(Screen previous) {
        super(Component.literal("Minegasm Config"));
        this.previous = previous;
        pauseMenu = false;
    }

    public ConfigScreen(Screen previous, boolean pause) {
        super(Component.literal("Minegasm Config"));
        this.previous = previous;
        pauseMenu = pause;
    }

    @Override
    protected void init() {
        wsHost = new EditBox(Minecraft.getInstance().font, this.width / 2 - 100, this.height / 6, 200, 20, null);
        wsHost.setValue(MinegasmConfig.serverUrl);
        this.addRenderableWidget(wsHost);

        wsHost.setResponder(s -> {
            LOGGER.info(s);
            MinegasmConfig.serverUrl = s;
        });

        this.addRenderableWidget(new Button.Builder(Component.literal("Reset Server Url"), button -> {
                    ConfigHolder.getClientInstance().resetConfigUrl();
                    MinegasmConfig.serverUrl = ConfigHolder.getClientInstance().serverUrl.get();
                    wsHost.setValue(MinegasmConfig.serverUrl);
                }).pos(this.width / 2 - 155, this.height / 6 + 25)
                        .size(150, 20)
                        .build()
        );

        PlainTextLabel connectResponse = new PlainTextLabel(this.width / 2 - 155, this.height / 6 + 50, 310, 15, Component.literal("" + ChatFormatting.GREEN));

        this.addRenderableWidget(connectResponse);

        Button reconnectButton = new Button.Builder(Component.literal("Reconnect"), button -> {
            button.active = false;
            connectResponse.setValue("Connecting");
            new Thread(() -> {
                if (ToyController.connectDevice()) {
                    ClientEventHandler.afterConnect();
                    button.active = true;
                    connectResponse.setValue(String.format("Connected to " + ChatFormatting.GREEN + "%s" + ChatFormatting.RESET + " [%d]", ToyController.getDeviceName(), ToyController.getDeviceId()));
                } else {
                    button.active = true;
                    connectResponse.setValue(String.format(ChatFormatting.YELLOW + "Minegasm " + ChatFormatting.RESET + "failed to start: %s", ToyController.getLastErrorMessage()));
                }

            }).start();
        })
                .pos(this.width / 2 + 5, this.height / 6 + 25)
                .size(150, 20)
                .build();

        this.addRenderableWidget(reconnectButton);

        reconnectButton.active = pauseMenu;

        this.addRenderableWidget(CycleButton.onOffBuilder(MinegasmConfig.vibrate)
                .create(this.width / 2 - 155, this.height / 6 + 25 * 3, 150, 20,
                        Component.literal("Vibration"), (button, value) -> MinegasmConfig.vibrate = value));

        this.addRenderableWidget(CycleButton.onOffBuilder(MinegasmConfig.stealth)
                .create(this.width / 2 + 5, this.height / 6 + 25 * 3, 150, 20,
                        Component.literal("Stealth"), (button, value) -> MinegasmConfig.stealth = value));

        this.addRenderableWidget(
                CycleButton.builder((ClientConfig.GameplayMode mode) ->
                                Component.literal(switch (mode) {
                                    case NORMAL -> "Normal";
                                    case MASOCHIST -> "Masochist";
                                    case HEDONIST -> "Hedonist";
                                    case ACCUMULATION -> "Accumulation";
                                    case CUSTOM -> "Custom";
                                }))
                        .withValues(ClientConfig.GameplayMode.NORMAL, ClientConfig.GameplayMode.MASOCHIST, ClientConfig.GameplayMode.HEDONIST, ClientConfig.GameplayMode.ACCUMULATION, ClientConfig.GameplayMode.CUSTOM)
                        .withInitialValue(MinegasmConfig.mode)
                        .create(this.width / 2 - 155, this.height / 6 + 25 * 4, 150, 20,
                                Component.literal("Mode"), (button, value) -> {
                                    MinegasmConfig.mode = value;
                                })
        );

        this.addRenderableWidget(new Button.Builder(Component.literal("Edit Custom Settings"), button -> minecraft.setScreen(new CustomModeConfigScreen(this, pauseMenu)))
                .pos(this.width / 2 + 5, this.height / 6 + 25 * 4)
                .size(150, 20)
                .build()
        );

        this.addRenderableWidget(
                CycleButton.builder((Integer tickFrequency) ->
                                Component.literal(switch (tickFrequency) {
                                    case 1 -> "Every Tick";
                                    case 2 -> "Every Other Tick";
                                    case 5 -> "Every 5 Ticks";
                                    case 10 -> "Every 10 Ticks";
                                    case 20 -> "Every Second";
                                    default -> "Every " + Float.toString(tickFrequency / 20f) + " Seconds";
                                }))
                        .withValues(1, 2, 5, 10, 20, 30, 40, 50)
                        .withInitialValue(MinegasmConfig.tickFrequency)
                        .create(this.width / 2 - 100, this.height / 6 + 25 * 5, 200, 20,
                                Component.literal("Tick Frequency"), (button, value) -> {
                                    MinegasmConfig.tickFrequency = value;
                                    MinegasmConfig.ticksPerSecond = Math.max(0, 20f / MinegasmConfig.tickFrequency);
                                    LOGGER.info("TPS: " + MinegasmConfig.ticksPerSecond);
                                }
                        )
        );

        this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .pos(this.width / 2 - 100, this.height - 27)
                .size(200, 20)
                .build()
        );


    }

    @Override
    public void tick() {
        super.tick();

        // Add ticking logic for EditBox in editBox
//        if (this.wsHost != null)
//            this.wsHost.tick();
    }

    @Override
    public void onClose() {
        PlainTextLabel.setValue("");
        this.minecraft.setScreen(previous);
        MinegasmConfig.save();
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        if (pauseMenu)
            this.renderBackground(graphics);
        else
            this.renderDirtBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(graphics, i, j, f);
    }

    class PlainTextLabel extends AbstractWidget {

        private static Component text = Component.literal("");
        private int x;
        private int y;

        public PlainTextLabel(int x, int y, int width, int height, Component text) {
            super(x, y, width, height, text);
            this.x = x;
            this.y = y;
        }

        public static void setValue(String value) {
            text = Component.literal(value);
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
            if (text == null || text.getString().isEmpty())
                return;

//            RenderSystem.setShaderColor(1, 1, 1, 1);
//            Minecraft.getInstance().font.draw(poseStack, text.getString(), x, y, 0xFFFFFF);
            graphics.drawCenteredString(Minecraft.getInstance().font, text.getString(), Minecraft.getInstance().screen.width / 2, this.y + this.height / 4, 0xFFFFFF);
        }
    }

}

class CustomModeConfigScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen previous;
    private boolean pauseMenu;

    public CustomModeConfigScreen(Screen previous) {
        super(Component.literal("Minegasm Custom Config"));
        this.previous = previous;
        pauseMenu = false;
    }

    public CustomModeConfigScreen(Screen previous, boolean pause) {
        super(Component.literal("Minegasm Custom Config"));
        this.previous = previous;
        pauseMenu = pause;
    }

    @Override
    protected void init() {
        try {
            this.addRenderableWidget(new Button.Builder(CommonComponents.GUI_DONE, button -> this.onClose())
                    .pos(this.width / 2 + 5, this.height - 27)
                    .size(150, 20)
                    .build()
            );

            IntensitiySliderBar.sliders.clear();

            // Attack
            this.addRenderableWidget(new IntensitiySliderBar(this, "Attack: ", MinegasmConfig.class.getField("attackIntensity")));

            // Hurt
            this.addRenderableWidget(new IntensitiySliderBar(this, "Hurt: ", MinegasmConfig.class.getField("hurtIntensity")));

            // Mine
            this.addRenderableWidget(new IntensitiySliderBar(this, "Mine: ", MinegasmConfig.class.getField("mineIntensity")));

            // Place
            this.addRenderableWidget(new IntensitiySliderBar(this, "Place: ", MinegasmConfig.class.getField("placeIntensity")));

            // XP Change
            this.addRenderableWidget(new IntensitiySliderBar(this, "XP Change: ", MinegasmConfig.class.getField("xpChangeIntensity")));

            // Fishing
            this.addRenderableWidget(new IntensitiySliderBar(this, "Fishing: ", MinegasmConfig.class.getField("fishingIntensity")));

            // Harvest
            this.addRenderableWidget(new IntensitiySliderBar(this, "Harvest: ", MinegasmConfig.class.getField("harvestIntensity")));

            // Vitality
            this.addRenderableWidget(new IntensitiySliderBar(this, "Vitality: ", MinegasmConfig.class.getField("vitalityIntensity")));

            // Advancement
            this.addRenderableWidget(new IntensitiySliderBar(this, "Advancement: ", MinegasmConfig.class.getField("advancementIntensity")));

            this.addRenderableWidget(new Button.Builder(Component.literal("Reset Values"), button -> {
                        ConfigHolder.getClientInstance().resetConfigCustom();
                        IntensitiySliderBar.refreshAllValues();
                    })
                            .pos(this.width / 2 - 155, this.height - 27)
                            .size(150, 20)
                            .build()
            );


        } catch (Throwable e) {
            LOGGER.throwing(e);
        }
    }

    @Override
    public void onClose() {
        IntensitiySliderBar.sliders.clear();
        this.minecraft.setScreen(previous);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        if (pauseMenu)
            this.renderBackground(graphics);
        else
            this.renderDirtBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(graphics, i, j, f);
    }

    private class IntensitiySliderBar extends ForgeSlider {
        public static ArrayList<IntensitiySliderBar> sliders = new ArrayList<IntensitiySliderBar>();
        private Field fieldReference;

        IntensitiySliderBar(CustomModeConfigScreen parent, String prefix, Field field) throws Exception {
            super(parent.width / 2 + (sliders.size() % 2 == 1 ? 5 : -155), // x pos
                    parent.height / 6 + 25 * (int) Math.floor(sliders.size() / 2), // y pos
                    150, 20, // Width, height
                    Component.literal(prefix), // Prefix
                    Component.literal(""), // Suffix
                    0, 100, field.getInt(null), 1, 1, true); // Min, Max, Default value, stepsize, percision, drawstring
            fieldReference = field;
//            LOGGER.info("S: " + sliders.size() + "   X: " + parent.width / 2 + (sliders.size() % 2 == 1 ? 5 : -155) + "   Y: " + parent.height / 6 + 25 * (int)Math.floor(sliders.size() / 2));
            sliders.add(this);
        }

        public static void refreshAllValues() {
            for (IntensitiySliderBar slider : sliders) {
                slider.refreshValue();
            }
        }

        @Override
        public void applyValue() {
//            LOGGER.info("applyValue");
            //responder.accept(this.getValueInt());
            try {
                fieldReference.set(null, this.getValueInt());
            } catch (Throwable e) {
                LOGGER.throwing(e);
            }
        }

        private void refreshValue() {
            try {
                this.setValue(fieldReference.getInt(null));
            } catch (Throwable e) {
                LOGGER.throwing(e);
            }
        }

    }
}