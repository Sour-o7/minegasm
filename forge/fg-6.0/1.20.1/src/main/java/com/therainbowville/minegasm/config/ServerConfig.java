package com.therainbowville.minegasm.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class ServerConfig {

    ServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        builder.pop();
    }
}