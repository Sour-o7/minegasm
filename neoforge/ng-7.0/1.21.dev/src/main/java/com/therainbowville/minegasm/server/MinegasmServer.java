package com.therainbowville.minegasm.common;

import com.therainbowville.minegasm.core.MinegasmGroup;
import com.therainbowville.minegasm.gui.ClientConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Minegasm.MOD_ID, dist = Dist.DEDICATED_SERVER) 
public class MinegasmServer {
    ArrayList<MinegasmGroup> minegasmGroups;
    
    public MinegasmServer(IEventBus modEventBus, ModContainer container) {
        
    }
    

}