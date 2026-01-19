package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.UUID;
import java.util.Optional;

public class MinegasmGroupMember {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
    
    public final UUID uuid;
    public String name;
    public PlayerRank rank;
    public PlayerRole role;
    public MinegasmModifier modifier = null;
    
    public MinegasmGroupMember(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.rank = PlayerRank.MEMBER;
        this.role = PlayerRole.SWITCH;
    }
    
    public MinegasmGroupMember(MinegasmGroupMember member) {
        this.uuid = member.uuid;
        this.name = member.name;
        this.rank = member.rank;
        this.role = member.role;
    }
    
    public MinegasmGroupMember(UUID uuid, String name, PlayerRank rank, PlayerRole role) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.role = role;
    }
	
    public MinegasmGroupMember(UUID uuid, String name, PlayerRank rank, PlayerRole role, MinegasmModifier modifier) {
		this(uuid, name, rank, role);
		this.modifier = modifier;
    }
	
	public void copyFrom(MinegasmGroupMember obj) {
		name = obj.name;
		rank = obj.rank;
		role = obj.role;
		modifier = obj.modifier;
	}
	
	public boolean equals(MinegasmGroupMember obj) {
		return name.equals(obj.name) && uuid.equals(obj.uuid) && rank == obj.rank && role == obj.role;
	}

    public static final StreamCodec<FriendlyByteBuf, MinegasmGroupMember> STREAM_CODEC = StreamCodec.ofMember(MinegasmGroupMember::write, MinegasmGroupMember::read);
    
    private void write(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeUtf(name);
        buf.writeEnum(rank);
        buf.writeEnum(role);
		//Optional<MinegasmModifier> modifier = Optional.ofNullable(this.modifier);
		//buf.writeOptional(modifier, MinegasmModifier.STREAM_CODEC::encode);
		//MinegasmModifier.STREAM_CODEC.encode(buf, modifier);
    }
    
    private static MinegasmGroupMember read(FriendlyByteBuf buf) {
		
        MinegasmGroupMember member = new MinegasmGroupMember(buf.readUUID(), buf.readUtf(), buf.readEnum(PlayerRank.class), buf.readEnum(PlayerRole.class));
		
		//Optional<MinegasmModifier> modifier = buf.readOptional(MinegasmModifier.STREAM_CODEC::decode);
		//
		//member.modifier = modifier.isPresent() ? modifier.get() : null;
		
		return member;
    }
    
    public void print() {
        LOGGER.info("uuid: " + uuid);
        LOGGER.info("name: " + name);
        LOGGER.info("rank: " + rank.getTranslateKey());
        LOGGER.info("role: " + role.getTranslateKey());
    }
    
    public enum PlayerRole {
        SWITCH("gui." + Minegasm.MOD_ID + ".config.group.role.switch"),
        DOM("gui." + Minegasm.MOD_ID + ".config.group.role.dom"),
        SUB("gui." + Minegasm.MOD_ID + ".config.group.role.sub"),
        DISABLE("gui." + Minegasm.MOD_ID + ".config.group.role.disable");

        private final String translateKey;

        PlayerRole(String translateKey) {
            this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
        }

        public String getTranslateKey() {
            return this.translateKey;
        }
    }
    
    public enum PlayerRank {
        LEADER("gui." + Minegasm.MOD_ID + ".config.group.rank.leader"),
        SUBLEADER("gui." + Minegasm.MOD_ID + ".config.group.rank.subleader"),
        MEMBER("gui." + Minegasm.MOD_ID + ".config.group.rank.member");

        private final String translateKey;

        PlayerRank(String translateKey) {
            this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
        }

        public String getTranslateKey() {
            return this.translateKey;
        }
    }
}