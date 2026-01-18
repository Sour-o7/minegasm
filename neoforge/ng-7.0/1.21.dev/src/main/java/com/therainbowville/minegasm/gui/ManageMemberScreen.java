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

public class ManageMemberScreen extends MemberScreenBase {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();
	
	private final MemberScreen lastScreen;

    public ManageMemberScreen(MemberScreen lastScreen, MinegasmGroupMember member) {
        super(lastScreen, lastScreen.group, new MinegasmGroupMember(member));
		super.title = "Manage User";
		this.lastScreen = lastScreen;
    }
    
    @Override
    protected void init() { 
        int middle = this.width / 2;
        int y = (this.height - TEXTURE_HEIGHT) / 2 + 16;
		
        CycleButton rankButton = CycleButton.builder((MinegasmGroupMember.PlayerRank rank) ->
            Component.literal(switch (rank) {
				case LEADER -> "Leader";
                case SUBLEADER -> "Sub-Leader";
                case MEMBER -> "Member";
            }))
        .withValues(MinegasmGroupMember.PlayerRank.SUBLEADER, MinegasmGroupMember.PlayerRank.MEMBER)
        .withInitialValue(member.rank)
        .create(middle + 8, y, 100, Button.DEFAULT_HEIGHT,
        Component.literal("Rank"), (button, value) -> {
            member.rank = value;
        });
        // Commented out for testing, remove for release
		//rankButton.active = !lastScreen.player.uuid.equals(member.uuid);
        this.addRenderableWidget(rankButton);
		
		Button setLeaderButton = new Button.Builder(Component.literal("Set Leader"), button -> {
			member.rank = MinegasmGroupMember.PlayerRank.LEADER;
        }).pos(middle + 8, y + 24 + 24).size(100, Button.DEFAULT_HEIGHT).build();
		//setLeaderButton.active = lastScreen.player.rank == MinegasmGroupMember.PlayerRank.LEADER;
		
		this.addRenderableWidget(setLeaderButton);
		
		Button removeMemberButton = new Button.Builder(Component.literal("Remove Member"), button -> {
			ClientPayloadDispatcher.sendRemoveGroupMemberPayload(group.uuid, member.uuid);
        }).pos(middle + 8, y + 48 + 24).size(100, Button.DEFAULT_HEIGHT).build();
		//removeMemberButton.active = lastScreen.player.rank.ordinal() < member.rank.ordinal();
		
		this.addRenderableWidget(removeMemberButton);
		
        this.addRenderableWidget(new Button.Builder(Component.literal("Done"), button -> {
			
            ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
            this.onClose();
        }).pos((this.width - Button.DEFAULT_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build());
    }
}