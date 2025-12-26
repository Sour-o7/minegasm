package com.therainbowville.minegasm.network;

import com.therainbowville.minegasm.common.Minegasm;

import java.util.Objects;

public enum ServerMessage {
    INCORRECT_PASSWORD("gui." + Minegasm.MOD_ID + ".message.incorrect_password"),
    PASSWORD_TIMEOUT("gui." + Minegasm.MOD_ID + ".message.password_timeout"),
    INVALID_PERMISSION("gui." + Minegasm.MOD_ID + ".message.invalid_permission");

    private final String translateKey;

    ServerMessage(String translateKey) {
        this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
    }

    public String getTranslateKey() {
        return this.translateKey;
    }
    
    public int toInt() {
        return this.ordinal();
    }
    
    public static ServerMessage fromInt(int ordinal) {
        return ServerMessage.values()[ordinal];
    }

}