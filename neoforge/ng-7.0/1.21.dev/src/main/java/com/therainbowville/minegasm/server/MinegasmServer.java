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
    
    public static void updateGroup(MinegasmGroup group) {
        minegasmGroups.put(group.uuid, group);
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
    }
    
    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer && !event.getEntity().level().isClientSide) {
            ServerPayloadDispatcher.sendGroupInfoPayload((ServerPlayer) event.getEntity(), getGroupInfoList());
        }
    }
}