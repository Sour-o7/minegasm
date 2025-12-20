package com.therainbowville.minegasm.config;

import net.neoforged.neoforge.common.ModConfigSpec;

final class ServerConfig {

    ServerConfig(final ModConfigSpec.Builder builder) {
        builder.push("general");
        builder.pop();
    }
}