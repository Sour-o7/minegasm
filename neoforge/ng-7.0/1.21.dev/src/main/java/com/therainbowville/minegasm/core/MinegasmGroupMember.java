package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;

import java.util.Objects;
import java.util.UUID;

public class MinegasmGroupMember {
    public final UUID uuid;
    public String name;
    public PlayerRole role;
    public PlayerRank rank;
    
    public MinegasmGroupMember(UUID uuid, String name, PlayerRank rank, PlayerRole role) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.role = role;
    }
    
    public enum PlayerRole {
        DOM("gui." + Minegasm.MOD_ID + ".config.group.role.dom"),
        SWITCH("gui." + Minegasm.MOD_ID + ".config.group.role.switch"),
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