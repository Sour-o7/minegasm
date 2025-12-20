package com.therainbowville.minegasm.config;

import com.therainbowville.minegasm.common.Minegasm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import com.therainbowville.minegasm.core.MinegasmConfigClient;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(modid = Minegasm.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ConfigContainer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec SERVER_SPEC;
    static final ClientConfig CLIENT;
    static final ServerConfig SERVER;

    static final MinegasmConfigClient MINEGASM_CLIENT_CONFIG = new MinegasmConfigClient();

    static {
        {
            final Pair<ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            final Pair<ServerConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ServerConfig::new);
            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
    
    public static void bakeClientInstance() {
        CLIENT.fromMinegasmConfig(MINEGASM_CLIENT_CONFIG);
        CLIENT_SPEC.save();
    }
    
    public static void bakeMinegasmClient() {
        MINEGASM_CLIENT_CONFIG.copyFrom(CLIENT.toMinegasmConfig());
    }
    
    public static MinegasmConfigClient getMinegasmClient () {
        return MINEGASM_CLIENT_CONFIG;
    }

    public static ClientConfig getClientInstance() {
        return CLIENT;
    }

    public static ServerConfig getServerInstance() {
        return SERVER;
    }
    
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        if (config.getSpec() == ConfigContainer.CLIENT_SPEC) {
            if (event instanceof ModConfigEvent.Loading)  {
                MINEGASM_CLIENT_CONFIG.populateFrom(CLIENT.toMinegasmConfig());
            }
            ConfigContainer.bakeMinegasmClient();
        }
    }

}