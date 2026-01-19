package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupMember;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class PlayerSelectionList extends ObjectSelectionList<PlayerSelectionList.Entry> {

    private final GroupScreen screen;
    private final List<PlayerSelectionList.PlayerEntry> playerList = new ArrayList();

    public PlayerSelectionList(GroupScreen screen, Minecraft minecraft, int varWidth, int varHeight, int varX, int varY) {
        super(Minecraft.getInstance(), varWidth, varHeight, varX, varY);
        this.screen = screen;
    }
    
    private void refreshEntries() {
        this.clearEntries();
        this.playerList.forEach((entry) -> {
            this.addEntry(entry);
        });
    }
    
    public void populateFromGroup(MinegasmGroup group) {
		playerList.clear();
        group.getPlayers().forEach(p -> playerList.add(new PlayerEntry(screen, p)));
        refreshEntries();
    }
   
    public void addPlayerEntry(MinegasmGroupMember player) {
        playerList.add(new PlayerEntry(screen, player));
        refreshEntries();
    }

    public void setSelected(@Nullable PlayerSelectionList.Entry entry) {
        super.setSelected(entry);
        //this.screen.onSelectedChange();
    }

   /*public boolean keyPressed(int var1, int var2, int var3) {
      PlayerSelectionList.Entry var4 = (PlayerSelectionList.Entry)this.getSelected();
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


    public abstract static class Entry extends ObjectSelectionList.Entry<PlayerSelectionList.Entry> implements AutoCloseable {
        public Entry() {
        }

        public void close() {
        }
    }

    public static class PlayerEntry extends PlayerSelectionList.Entry {
        private final GroupScreen screen;
        public final MinegasmGroupMember player;
        protected final Minecraft minecraft;
        private long lastClickTime;


        protected PlayerEntry(GroupScreen screen, MinegasmGroupMember player) {
            this.screen = screen;
            this.minecraft = Minecraft.getInstance();
            this.player = player;
        }

        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            graphics.drawString(this.minecraft.font, player.name, left + 24, top + 1, 0xFFFFFF, false);
            
            String subtext = "";
            
            subtext += switch (player.rank) {
                case MinegasmGroupMember.PlayerRank.LEADER -> "Leader";
                case MinegasmGroupMember.PlayerRank.SUBLEADER -> "Subleader";
                case MinegasmGroupMember.PlayerRank.MEMBER -> "Member";
            };
            
            subtext += switch (player.role) {
                case MinegasmGroupMember.PlayerRole.DOM -> ", Dominate";
                case MinegasmGroupMember.PlayerRole.SWITCH -> ", Switch";
                case MinegasmGroupMember.PlayerRole.SUB -> ", Submissive";
                case MinegasmGroupMember.PlayerRole.DISABLE -> ""; 
            };
            
            graphics.drawString(this.minecraft.font, subtext, left + 24, top + 12, 0x808080, false);
            
            Player playerEntity = this.minecraft.level.getPlayerByUUID(player.uuid);
            boolean isUpsideDown = playerEntity != null && LivingEntityRenderer.isEntityUpsideDown(playerEntity);
            boolean drawHat = playerEntity != null && playerEntity.isModelPartShown(PlayerModelPart.HAT);
            PlayerInfo info = this.minecraft.player.connection.getPlayerInfo(player.uuid);
            PlayerFaceRenderer.draw(graphics, info.getSkin().texture(), left + 2, top + 2, 16, drawHat, isUpsideDown);
//            graphics.drawString(this.minecraft.font, subtext2, left + 3, top + 12 + 11, -8355712, false);
        }

        public boolean mouseClicked(double var1, double var3, int var5) {
            this.screen.setSelected(this);
            if (Util.getMillis() - this.lastClickTime < 250L) {
                this.minecraft.setScreen(new MemberScreen(this.screen, player));
            }

            this.lastClickTime = Util.getMillis();
            return super.mouseClicked(var1, var3, var5);
        }

        public Component getNarration() {
            return Component.literal("");
        }

    }
}
