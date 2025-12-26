package com.therainbowville.minegasm.core;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

public class MinegasmGroupInfo {
    public final String name;
    public final UUID uuid;
    public final int playerCount;
    public final boolean isPrivate;
    public final boolean syncConfig;
    public final boolean forcedRoles;

    public static final StreamCodec<FriendlyByteBuf, MinegasmGroupInfo> STREAM_CODEC = StreamCodec.ofMember(MinegasmGroupInfo::write, MinegasmGroupInfo::read);
    
    public MinegasmGroupInfo (String name, UUID uuid, int playerCount, boolean isPrivate, boolean syncConfig, boolean forcedRoles) {
        this.name = name;
        this.uuid = uuid;
        this.playerCount = playerCount;
        this.isPrivate = isPrivate;
        this.syncConfig = syncConfig;
        this.forcedRoles = forcedRoles;
    }
    
    public MinegasmGroupInfo (MinegasmGroup group) {
        this.name = group.name;
        this.uuid = group.uuid;
        this.isPrivate = group.isPrivate;
        this.playerCount = group.getPlayerCount();
        this.syncConfig = group.config.syncConfig;
        this.forcedRoles = group.config.forcedRoles;
    }
    
    private void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUUID(uuid);
        buf.writeShort(playerCount);
        buf.writeBoolean(isPrivate);
        buf.writeBoolean(syncConfig);
        buf.writeBoolean(forcedRoles);
    }
    
    private static MinegasmGroupInfo read(FriendlyByteBuf buf) {
        return new MinegasmGroupInfo(buf.readUtf(), buf.readUUID(), buf.readShort(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }

}