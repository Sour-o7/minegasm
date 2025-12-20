package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.client.ToyController;
import com.therainbowville.minegasm.core.EventProcessor;
import com.therainbowville.minegasm.core.MinegasmConfig;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import com.therainbowville.minegasm.core.MinegasmConfigDefaults;
import com.therainbowville.minegasm.core.MinegasmGroupMember;
import com.therainbowville.minegasm.config.ConfigContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class GroupScreen extends Screen {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
    
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath("minegasm", "textures/group_selection_screen.png");
    private static final int TEXTURE_WIDTH = 236;
    private static final int TEXTURE_HEIGHT = 176;
    private static String name;

    public GroupScreen(String name) {
        super(Component.literal(name));
        this.name = name;
    }
    
    @Override
    protected void init() { 
        int x = (this.width - TEXTURE_WIDTH) / 2 + 8;
        int xRight = (this.width + TEXTURE_WIDTH) / 2 - 8;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
        PlayerSelectionList playerList = new PlayerSelectionList(this, Minecraft.getInstance(), 220, 127, y, 24);
        playerList.setRectangle(220, 127, x, y);

        MinegasmGroupMember p1 = new MinegasmGroupMember(Minecraft.getInstance().player.getGameProfile().getId(), "Player 1", MinegasmGroupMember.PlayerRank.LEADER, MinegasmGroupMember.PlayerRole.SUB);
        MinegasmGroupMember p2 = new MinegasmGroupMember(Minecraft.getInstance().player.getGameProfile().getId(), "Player 2", MinegasmGroupMember.PlayerRank.SUBLEADER, MinegasmGroupMember.PlayerRole.DOM);
        MinegasmGroupMember p3 = new MinegasmGroupMember(Minecraft.getInstance().player.getGameProfile().getId(), "Player 3", MinegasmGroupMember.PlayerRank.MEMBER, MinegasmGroupMember.PlayerRole.SWITCH);
        MinegasmGroupMember p4 = new MinegasmGroupMember(Minecraft.getInstance().player.getGameProfile().getId(), "Player 4", MinegasmGroupMember.PlayerRank.MEMBER, MinegasmGroupMember.PlayerRole.DISABLE);

        playerList.addPlayerEntry(p1);
        playerList.addPlayerEntry(p2);
        playerList.addPlayerEntry(p3);
        playerList.addPlayerEntry(p4);

        this.addRenderableWidget(playerList);
    
        /*this.addRenderableWidget(new Button.Builder(Component.literal("Reward Player"), button -> 
            minecraft.setScreen(new VibrationConfigScreen(this))
            //).pos((this.width - Button.DEFAULT_WIDTH)/ 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build()
            ).pos(this.width / 2 - 110, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(220 / 2 - 2, Button.DEFAULT_HEIGHT).build()
        );*/
        
        // Settings Button
        this.addRenderableWidget(new Button.Builder(Component.literal("S"), button -> 
            minecraft.setScreen(new CreateGroupScreen(this))
            ).pos(xRight - 20, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build()
        );
        
        // Leave Button
        this.addRenderableWidget(new Button.Builder(Component.literal("L"), button -> 
            minecraft.setScreen(new CreateGroupScreen(this))
            ).pos(xRight - 40 - 4, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_HEIGHT, Button.DEFAULT_HEIGHT).build()
        );
        


    }
    
    public void onSelectedChange() {
        
    }
    
    public void setSelected(PlayerSelectionList.Entry entry) {
        
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
        
        int width = this.minecraft.font.width(name);
        graphics.drawString(this.minecraft.font, name, (this.width - width ) / 2, y + 5, 0x3F3F3F, false);
      
        //super.renderBackground(graphics, i, j, f);
   }
   
    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
      
        super.render(graphics, i, j, f);
   }

   /*public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      MerchantOffers var5 = ((MerchantMenu)this.menu).getOffers();
      if (!var5.isEmpty()) {
         int var6 = (this.width - this.imageWidth) / 2;
         int var7 = (this.height - this.imageHeight) / 2;
         int var8 = var7 + 16 + 1;
         int var9 = var6 + 5 + 5;
         this.renderScroller(var1, var6, var7, var5);
         int var10 = 0;
         Iterator var11 = var5.iterator();

         while(true) {
            MerchantOffer var12;
            while(var11.hasNext()) {
               var12 = (MerchantOffer)var11.next();
               if (this.canScroll(var5.size()) && (var10 < this.scrollOff || var10 >= 7 + this.scrollOff)) {
                  ++var10;
               } else {
                  ItemStack var13 = var12.getBaseCostA();
                  ItemStack var14 = var12.getCostA();
                  ItemStack var15 = var12.getCostB();
                  ItemStack var16 = var12.getResult();
                  var1.pose().pushPose();
                  var1.pose().translate(0.0F, 0.0F, 100.0F);
                  int var17 = var8 + 2;
                  this.renderAndDecorateCostA(var1, var14, var13, var9, var17);
                  if (!var15.isEmpty()) {
                     var1.renderFakeItem(var15, var6 + 5 + 35, var17);
                     var1.renderItemDecorations(this.font, var15, var6 + 5 + 35, var17);
                  }

                  this.renderButtonArrows(var1, var12, var6, var17);
                  var1.renderFakeItem(var16, var6 + 5 + 68, var17);
                  var1.renderItemDecorations(this.font, var16, var6 + 5 + 68, var17);
                  var1.pose().popPose();
                  var8 += 20;
                  ++var10;
               }
            }

            int var18 = this.shopItem;
            var12 = (MerchantOffer)var5.get(var18);
            if (((MerchantMenu)this.menu).showProgressBar()) {
               this.renderProgressBar(var1, var6, var7, var12);
            }

            if (var12.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var2, (double)var3) && ((MerchantMenu)this.menu).canRestock()) {
               var1.renderTooltip(this.font, DEPRECATED_TOOLTIP, var2, var3);
            }

            MerchantScreen.TradeOfferButton[] var19 = this.tradeOfferButtons;
            int var20 = var19.length;

            for(int var21 = 0; var21 < var20; ++var21) {
               MerchantScreen.TradeOfferButton var22 = var19[var21];
               if (var22.isHoveredOrFocused()) {
                  var22.renderToolTip(var1, var2, var3);
               }

               var22.visible = var22.index < ((MerchantMenu)this.menu).getOffers().size();
            }

            RenderSystem.enableDepthTest();
            break;
         }
      }

      this.renderTooltip(var1, var2, var3);
   }*/
}
