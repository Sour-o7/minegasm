package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.core.MinegasmGroupInfo;
import com.therainbowville.minegasm.network.ServerPayloadDispatcher;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Minegasm.MOD_ID/*, dist = Dist.DEDICATED_SERVER*/) 
public class MinegasmServer {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
    
    static Map<UUID, MinegasmGroup> minegasmGroups = new LinkedHashMap<UUID, MinegasmGroup>();
	
	public static MinegasmGroup getGroup(UUID uuid) {
		return minegasmGroups.get(uuid);
	}
    
    public static void updateGroup(MinegasmGroup group) {
        minegasmGroups.put(group.uuid, group);
    }
	
	public static void removeGroup(UUID uuid) {
		minegasmGroups.remove(uuid);
	}
    
    public static List<MinegasmGroupInfo> getGroupInfoList() {
        ArrayList<MinegasmGroupInfo> list = new ArrayList<MinegasmGroupInfo>();
        for (MinegasmGroup group : minegasmGroups.values()) {
            list.add(new MinegasmGroupInfo(group));
        }
        //list.sort((a, b) -> a.name.compareTo(b.name));
        return list;
    }
    
    public static Map<UUID, MinegasmGroup> getGroupMap() {
        return minegasmGroups;
    }
    
    public MinegasmServer(IEventBus modEventBus, ModContainer container) {
        NeoForge.EVENT_BUS.addListener(MinegasmServer::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(MinegasmServer::onServerTick);
    }
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer && !event.getEntity().level().isClientSide) {
			ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerPayloadDispatcher.sendGroupInfoPayload(player, getGroupInfoList());
			for (MinegasmGroup group : minegasmGroups.values()) {
				if (group.getPlayer(player.getUUID()) != null) {
					ServerPayloadDispatcher.sendGroupPayload(player, group);
					break;
				}
			}
        }
    }
	
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		for (MinegasmGroup group : minegasmGroups.values()) {
			group.process();
		}
	}
}