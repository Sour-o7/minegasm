package com.therainbowville.minegasm.gui;

import com.therainbowville.minegasm.core.MinegasmConfig;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import net.minecraft.network.chat.Component;

interface ExtendedSliderListener {
    void applyValue(double value);
}

interface ExtendedSliderAccessor {
    double getValue();
}

class ExtendedSliderWithListener extends ExtendedSlider {

    private final ExtendedSliderListener listener;
    private final ExtendedSliderAccessor accessor;
    
    ExtendedSliderWithListener(int x, int y, int width, int height, String prefix, String suffix, double minValue, double maxValue, double currentValue, boolean drawString, ExtendedSliderListener listener, ExtendedSliderAccessor accessor) {
        super(x, y, width, height, Component.literal(prefix + ": "), Component.literal(suffix), minValue, maxValue,  currentValue, drawString);
        this.listener = listener;
        this.accessor = accessor;
    }
    
    ExtendedSliderWithListener(int x, int y, int width, int height, String prefix, String suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, ExtendedSliderListener listener) {
        super(x, y, width, height, Component.literal(prefix + ": "), Component.literal(suffix), minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.listener = listener;
        this.accessor = () -> { return getValue(); };
    }
    
    ExtendedSliderWithListener(int x, int y, int width, int height, String prefix, String suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString, ExtendedSliderListener listener, ExtendedSliderAccessor accessor) {
        super(x, y, width, height, Component.literal(prefix + ": "), Component.literal(suffix), minValue, maxValue, currentValue, stepSize, precision, drawString);
        this.listener = listener;
        this.accessor = accessor;
    }
    
    @Override
    public void applyValue() {
        listener.applyValue(getValue());
    }
    
    public void refreshValue() {
        setValue(accessor.getValue());
    }
}