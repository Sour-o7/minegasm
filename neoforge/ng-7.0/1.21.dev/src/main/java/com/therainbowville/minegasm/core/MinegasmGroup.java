package com.therainbowville.minegasm.core;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinegasmGroup {
    public final static int MAX_GROUP_MEMBERS = 20;
    private static final Logger LOGGER = LogManager.getLogger();

    public String name;
    public String password;
    public boolean isPrivate;
    public UUID uuid;
    public MinegasmConfigGroup config;

    private LinkedHashMap<UUID, MinegasmGroupMember> players = new LinkedHashMap<UUID, MinegasmGroupMember>();
    
    public static final StreamCodec<FriendlyByteBuf, MinegasmGroup> STREAM_CODEC = StreamCodec.ofMember(MinegasmGroup::write, MinegasmGroup::read);
    
    public MinegasmGroup () {
        this("", null, new MinegasmConfigGroup(MinegasmConfigDefaults.getDefaultInstance(), false, false, false));
    }
    
    public MinegasmGroup (MinegasmGroup group) {
        this.name = group.name;
        this.password = group.password;
        this.isPrivate = group.isPrivate;
        this.uuid = group.uuid;
        this.config = group.config;
    }
    
    public MinegasmGroup (String name) {
        this(name, null, new MinegasmConfigGroup(MinegasmConfigDefaults.getDefaultInstance(), false, false, false));
    }
    
    public MinegasmGroup (String name, String password) {
        this(name, password, new MinegasmConfigGroup(MinegasmConfigDefaults.getDefaultInstance(), false, false, false));
    }
    
    public MinegasmGroup(String name, String password, MinegasmConfigGroup config) {
        this(name, password, UUID.randomUUID(), config);
    }
    
    private MinegasmGroup(String name, String password, UUID uuid, MinegasmConfigGroup config) {
        this.name = name;
        this.password = password;
        this.uuid = uuid;
        this.config = config;
        if (password == null || password == "") {
            this.isPrivate = false;
            this.password = "";
        } else {
            this.isPrivate = true;
        }
    }
    
    private void write(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeUtf(password);
        buf.writeUUID(uuid);
        MinegasmConfigGroup.STREAM_CODEC.encode(buf, config);
        buf.writeInt(players.size());
        for (MinegasmGroupMember member : players.values()) {
            MinegasmGroupMember.STREAM_CODEC.encode(buf, member);
        }
    }
        
    private static MinegasmGroup read(FriendlyByteBuf buf) {
        MinegasmGroup receivedGroup = new MinegasmGroup(buf.readUtf(), buf.readUtf(), buf.readUUID(), MinegasmConfigGroup.STREAM_CODEC.decode(buf));
        int playerCount = buf.readInt();
        for (int i = 0; i < playerCount; i++) {
            MinegasmGroupMember member = MinegasmGroupMember.STREAM_CODEC.decode(buf);
            receivedGroup.players.put(member.uuid, member);
        }
        return receivedGroup;
    }
    
    public MinegasmGroupMember getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    
    public ArrayList<MinegasmGroupMember> getPlayers() {
        ArrayList<MinegasmGroupMember> playerList = new ArrayList<MinegasmGroupMember>(players.values());
        Collections.sort(playerList, (a, b) -> a.rank.ordinal() - b.rank.ordinal());
        return playerList;
    }
    
    public ArrayList<UUID> getPlayerUUIDs() {
        return new ArrayList<UUID>(players.keySet());
    }

    public int getPlayerCount() {
        return players.size();
    }
    
    public void updatePlayer(MinegasmGroupMember player) {
        players.put(player.uuid, player);
    }
    
    public void removePlayer(UUID player) {
        players.remove(player);
    }
    
    public void print() {
        LOGGER.info("Group Info");
        LOGGER.info("Name: " + name);
        LOGGER.info("Password: " + password);
        LOGGER.info("UUID: " + uuid);
        LOGGER.info("Config: ");
        config.print();
        //LOGGER.info("Players: ");
        //players.forEach((p) -> p.print());
    }
    
    public class Builder {
        
        public boolean forcedRoles = MinegasmGroup.this.config.forcedRoles;
        //public boolean syncConfig = MinegasmGroup.this.config.syncConfig;
        
        
        public MinegasmGroup build() {
            return new MinegasmGroup(MinegasmGroup.this.name, MinegasmGroup.this.password, new MinegasmConfigGroup(MinegasmGroup.this.config, !MinegasmGroup.this.password.equals(""), forcedRoles, MinegasmGroup.this.config.syncConfig));
        }
        
    }
}