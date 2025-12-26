package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.core.MinegasmGroupInfo;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GroupSelectionList extends ObjectSelectionList<GroupSelectionList.Entry> {

    private final JoinGroupScreen screen;
    private final List<GroupSelectionList.GroupEntry> groupList = new ArrayList();

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
   
    public void setGroupEntries(List<MinegasmGroupInfo> groupInfoList) {
        groupList.clear();
        for (MinegasmGroupInfo info : groupInfoList) {
            groupList.add(new GroupEntry(screen, info));
        }
        refreshEntries();
    }

    public void setSelected(@Nullable GroupSelectionList.GroupEntry entry) {
        super.setSelected(entry);
        this.screen.setSelected(entry);
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

    public static class GroupEntry extends Entry {
        private final JoinGroupScreen screen;
        protected final Minecraft minecraft;
        public final MinegasmGroupInfo groupInfo;
        private long lastClickTime;

        protected GroupEntry(JoinGroupScreen screen, MinegasmGroupInfo groupInfo) {
            this.screen = screen;
            this.minecraft = Minecraft.getInstance();
            this.groupInfo = groupInfo;
        }

        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            graphics.drawString(this.minecraft.font, groupInfo.name, left + 3, top + 1, 16777215, false);
            graphics.drawString(this.minecraft.font, (groupInfo.isPrivate ? "Private" : "Public") + " : " + groupInfo.playerCount + " Players", left + 3, top + 12, -8355712, false);
            graphics.drawString(this.minecraft.font, (groupInfo.syncConfig ? "Synced Config" : "") + (groupInfo.syncConfig && groupInfo.forcedRoles ? ", " : "") + (groupInfo.forcedRoles ? "Forced Roles" : ""), left + 3, top + 12 + 11, -8355712, false);
        }

        public boolean mouseClicked(double var1, double var3, int var5) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.screen.joinSelectedGroup();
            }

            this.lastClickTime = Util.getMillis();
            return super.mouseClicked(var1, var3, var5);
        }

        public Component getNarration() {
            return Component.literal("");
        }

    }
}
