package com.therainbowville.minegasm.gui;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;

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

//      RenderSystem.setShaderColor(1, 1, 1, 1);
//      Minecraft.getInstance().font.draw(poseStack, text.getString(), x, y, 0xFFFFFF);
        graphics.drawCenteredString(Minecraft.getInstance().font, text.getString(), Minecraft.getInstance().screen.width / 2, this.y + this.height / 4, 0xFFFFFF);
    }
}