package com.therainbowville.minegasm.core;

import com.therainbowville.minegasm.common.Minegasm;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;


import java.util.Objects;
import java.util.UUID;

public class MinegasmModifier {	
    public ModifierType type;
    public double amount;
    public int duration; // In ticks, assuming 20 ticks per second
    public final UUID origin;
    
    public MinegasmModifier(UUID origin) {
        this.type = ModifierType.SET;
        this.amount = 0;
        this.duration = -1;
        this.origin = origin;
    }
	
    public MinegasmModifier(MinegasmModifier modifier) {
        this.type = modifier.type;
        this.amount = modifier.amount;
        this.duration = modifier.duration;
        this.origin = modifier.origin;
    }
    
    public MinegasmModifier(ModifierType type, double amount, int duration, UUID origin) {
        this.type = type;
        this.amount = amount;
        this.duration = duration;
        this.origin = origin;
    }
		
    public static final StreamCodec<FriendlyByteBuf, MinegasmModifier> STREAM_CODEC = StreamCodec.ofMember(MinegasmModifier::write, MinegasmModifier::read);
    
    private void write(FriendlyByteBuf buf) {
        buf.writeEnum(type);
		buf.writeDouble(amount);
        buf.writeInt(duration);
        buf.writeUUID(origin);
    }

    private static MinegasmModifier read(FriendlyByteBuf buf) {
        return new MinegasmModifier(buf.readEnum(ModifierType.class), buf.readDouble(), buf.readInt(), buf.readUUID());
    }
    
    public enum ModifierType {
        SET("gui." + Minegasm.MOD_ID + ".modifier.set"), // Range: 0 - 100, Step: 1
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