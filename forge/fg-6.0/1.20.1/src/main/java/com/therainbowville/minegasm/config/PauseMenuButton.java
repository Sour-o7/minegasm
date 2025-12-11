package com.therainbowville.minegasm.config;

import com.mojang.blaze3d.systems.RenderSystem;
import com.therainbowville.minegasm.common.Minegasm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Adapted from https://github.com/Creators-of-Create/Create/
public class PauseMenuButton extends Button {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ResourceLocation LOGO = new ResourceLocation(Minegasm.MOD_ID, "textures/logo.png");

    int xPos;
    int yPos;

    public PauseMenuButton(int x, int y) {
        super(new Button.Builder(Component.literal(""), PauseMenuButton::clicked).pos(x, y).size(20, 20));
        xPos = x;
        yPos = y;
    }

    public static void clicked(Button button) {
        Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen, true));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float k) {
        super.renderWidget(graphics, i, j, k);
        graphics.pose().pushPose();
        graphics.pose().translate(xPos + width / 2 - (64 * 0.25f) / 2, yPos + height / 2 - (64 * 0.25f) / 2, 0);
        graphics.pose().scale(0.25f, 0.25f, 1);
        RenderSystem.setShaderTexture(0, LOGO);
        graphics.blit(LOGO, 0, 0, 0, 0, 64, 64, 64, 64);
        graphics.pose().popPose();
    }

    public static class SingleMenuRow {
        public final String left, right;

        public SingleMenuRow(String left, String right) {
            this.left = I18n.get(left);
            this.right = I18n.get(right);
        }

        public SingleMenuRow(String center) {
            this(center, center);
        }
    }

    public static class MenuRows {
        public static final MenuRows MAIN_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.singleplayer"),
                new SingleMenuRow("menu.multiplayer"),
                new SingleMenuRow("fml.menu.mods", "menu.online"),
                new SingleMenuRow("narrator.button.language", "narrator.button.accessibility")
        ));

        public static final MenuRows INGAME_MENU = new MenuRows(Arrays.asList(
                new SingleMenuRow("menu.returnToGame"),
                new SingleMenuRow("gui.advancements", "gui.stats"),
                new SingleMenuRow("menu.sendFeedback", "menu.reportBugs"),
                new SingleMenuRow("menu.options", "menu.shareToLan"),
                new SingleMenuRow("menu.returnToMenu")
        ));

        protected final List<String> leftButtons, rightButtons;

        public MenuRows(List<SingleMenuRow> variants) {
            leftButtons = variants.stream().map(r -> r.left).collect(Collectors.toList());
            rightButtons = variants.stream().map(r -> r.right).collect(Collectors.toList());
        }
    }

    @Mod.EventBusSubscriber(modid = Minegasm.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class PauseMenuButtonScreen {

        @SubscribeEvent
        public static void onGuiInit(ScreenEvent.Init event) {
            if (event.getScreen() instanceof PauseScreen) { // Make sure GUI is Escape menu
                MenuRows menu = MenuRows.INGAME_MENU;
                int rowIdx = 3;
                int offsetX = 4;
                boolean onLeft = offsetX < 0;
                String target = (onLeft ? menu.leftButtons : menu.rightButtons).get(rowIdx - 1);

                int offsetX_ = offsetX;
                MutableObject<GuiEventListener> toAdd = new MutableObject<>(null);
                event.getListenersList()
                        .stream()
                        .filter(w -> w instanceof AbstractWidget)
                        .map(w -> (AbstractWidget) w)
                        .filter(w -> w.getMessage()
                                .getString()
                                .equals(target))
                        .findFirst()
                        .ifPresent(w -> toAdd
                                .setValue(new PauseMenuButton(w.getX() + offsetX_ + (onLeft ? -20 : w.getWidth()), w.getY())));
                if (toAdd.getValue() != null)
                    event.addListener(toAdd.getValue());
            }
        }
    }
}
