package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    private double oldGamma = 1.0;

    public Fullbright() { super("Fullbright"); }

    @Override
    public void onEnable() {
        oldGamma = MinecraftClient.getInstance().options.gamma;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        mc.options.gamma = 16.0;
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.gamma = oldGamma;
    }
}
