package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;

import java.util.Objects;
import java.util.UUID;

public class MinegasmModifier {
    public ModifierType type;
    public double amount;
    public int duration; // In ticks, assuming 20 ticks per second
    public final UUID origin;
    
    public MinegasmModifier(UUID origin) {
        this.type = ModifierType.FIXED;
        this.amount = 0;
        this.duration = -1;
        this.origin = origin;
    }
    
    public MinegasmModifier(ModifierType type, double amount, int duration, UUID origin) {
        this.type = type;
        this.amount = amount;
        this.duration = duration;
        this.origin = origin;
    }
    
    public enum ModifierType {
        FIXED("gui." + Minegasm.MOD_ID + ".modifier.fixed"), // Range: 0 - 100, Step: 1
        BONUS("gui." + Minegasm.MOD_ID + ".modifier.bonus"), // Range: 0 - 100, Step: 1
        OVERRIDE("gui." + Minegasm.MOD_ID + ".modifier.override"), // Range: 0 - 100, Step: 1
        MULTIPLIER("gui." + Minegasm.MOD_ID + ".modifier.multiplier"); // Range: 0 - 2, Step: 0.05

        private final String translateKey;

        ModifierType(String translateKey) {
            this.translateKey = Objects.requireNonNull(translateKey, "translateKey");
        }

        public String getTranslateKey() {
            return this.translateKey;
        }
    }
}