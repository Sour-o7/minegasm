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
	
	CycleButton rankButton;
	Button setLeaderButton;
	Button removeMemberButton;
	
	private final MemberScreen lastScreen;

    public ManageMemberScreen(MemberScreen lastScreen, MinegasmGroupMember member) {
        super(lastScreen, new MinegasmGroupMember(member));
		super.title = "Manage User";
		this.lastScreen = lastScreen;
    }
	
	private void updateButtonPermission() {
		removeMemberButton.active = player.rank.ordinal() < member.rank.ordinal();
		setLeaderButton.active = player.rank == MinegasmGroupMember.PlayerRank.LEADER;
		rankButton.active = !player.uuid.equals(member.uuid);
		rankButton.active = rankButton.active && player.rank.ordinal() < member.rank.ordinal();		
	}
	
	@Override
	public void onGroupUpdate(MinegasmGroup group) {	
		super.onGroupUpdate(group);
		
		if (group != null) {
			updateButtonPermission();			
		}
	}
	
	@Override
	public void onPlayerUpdate(MinegasmGroupMember player) {
		super.onPlayerUpdate(player);
		
		if (player.rank == MinegasmGroupMember.PlayerRank.MEMBER) {
			this.onClose();
		}
	}
	
	@Override
	public void onMemberUpdate(MinegasmGroupMember member) {
		super.onMemberUpdate(member);
		
		if (member.rank == MinegasmGroupMember.PlayerRank.LEADER) {
			this.onClose();
		}
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
        this.addRenderableWidget(rankButton);
		
		setLeaderButton = new Button.Builder(Component.literal("Set Leader"), button -> {
			member.rank = MinegasmGroupMember.PlayerRank.LEADER;
        }).pos(middle + 8, y + 24 + 24).size(100, Button.DEFAULT_HEIGHT).build();
		
		this.addRenderableWidget(setLeaderButton);
		
		removeMemberButton = new Button.Builder(Component.literal("Remove Member"), button -> {
			ClientPayloadDispatcher.sendRemoveGroupMemberPayload(group.uuid, member.uuid);
        }).pos(middle + 8, y + 48 + 24).size(100, Button.DEFAULT_HEIGHT).build();
		
		this.addRenderableWidget(removeMemberButton);

		updateButtonPermission();
		
        this.addRenderableWidget(new Button.Builder(Component.literal("Done"), button -> {
			
            ClientPayloadDispatcher.sendUpdateGroupMemberPayload(group.uuid, member);
            this.onClose();
        }).pos((this.width - Button.DEFAULT_WIDTH ) / 2, (this.height + TEXTURE_HEIGHT) / 2 - Button.DEFAULT_HEIGHT - 8).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT).build());
    }
}