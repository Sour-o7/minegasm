package com.therainbowville.minegasm.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;

public class GroupSelectionList extends ObjectSelectionList<GroupSelectionList.Entry> {

    private final JoinGroupScreen screen;
    private final List<GroupSelectionList.GroupSelectionEntry> groupList = new ArrayList();

    public GroupSelectionList(JoinGroupScreen screen, Minecraft minecraft, int varWidth, int varHeight, int varX, int varY) {
        super(Minecraft.getInstance(), varWidth, varHeight, varX, varY);
        this.screen = screen;
    }
    
    private void refreshEntries() {
        this.clearEntries();
        this.groupList.forEach((entry) -> {
            this.addEntry(entry);
        });
    }
   
    public void addGroupEntry(String name, String subtext1, String subtext2) {
        groupList.add(new GroupSelectionEntry(screen, name, subtext1, subtext2));
        refreshEntries();
    }

    public void setSelected(@Nullable GroupSelectionList.Entry entry) {
        super.setSelected(entry);
        this.screen.onSelectedChange();
    }

   /*public boolean keyPressed(int var1, int var2, int var3) {
      GroupSelectionList.Entry var4 = (GroupSelectionList.Entry)this.getSelected();
      return var4 != null && var4.keyPressed(var1, var2, var3) || super.keyPressed(var1, var2, var3);
   }*/

    public int getRowWidth() {
        return 220 - 3;
    }
    
    @Override
    protected boolean scrollbarVisible() {
        return false;
    }

    public void removed() {
    }


    public abstract static class Entry extends ObjectSelectionList.Entry<GroupSelectionList.Entry> implements AutoCloseable {
        public Entry() {
        }

        public void close() {
        }
    }

    public static class GroupSelectionEntry extends GroupSelectionList.Entry {
        private final Component name;
        private final Component subtext1;
        private final Component subtext2;
        private final JoinGroupScreen screen;
        protected final Minecraft minecraft;
        private long lastClickTime;
//        private final Button joinButton;

        protected GroupSelectionEntry(JoinGroupScreen screen, String name, String subtext1, String subtext2) {
            this.screen = screen;
            this.name = Component.literal(name);
            this.subtext1 = Component.literal(subtext1);
            this.subtext2 = Component.literal(subtext2);
            this.minecraft = Minecraft.getInstance();
            //joinButton = new Button.Builder(Component.literal("Join"), button -> {
            //    this.screen.setSelected(this);
            //    //this.screen.joinSelectedServer();
            //}).pos(0, 0).size(50, Button.DEFAULT_HEIGHT).build();
        }

        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            graphics.drawString(this.minecraft.font, name, left + 3, top + 1, 16777215, false);
            graphics.drawString(this.minecraft.font, subtext1, left + 3, top + 12, -8355712, false);
            graphics.drawString(this.minecraft.font, subtext2, left + 3, top + 12 + 11, -8355712, false);
            /*if (isMouseOver) {
                joinButton.setX(left + width - joinButton.getWidth() - 7);
                joinButton.setY(top + (height - joinButton.getHeight()) / 2);
                joinButton.render(graphics, mouseX, mouseY, partialTick);                
            }*/
        }

        public boolean mouseClicked(double var1, double var3, int var5) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                //this.screen.joinSelectedServer();
            }

            this.lastClickTime = Util.getMillis();
            return super.mouseClicked(var1, var3, var5);
        }

        public Component getNarration() {
            return Component.literal("");
        }

    }
}
