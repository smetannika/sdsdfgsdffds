package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;

public class AutoRespawn extends Module {
    public AutoRespawn() { super("AutoRespawn"); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player.isDead()) mc.player.requestRespawn();
    }
}
