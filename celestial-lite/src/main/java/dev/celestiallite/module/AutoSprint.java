package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class AutoSprint extends Module {
    public AutoSprint() { super("AutoSprint"); }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (p.forwardSpeed > 0 && !p.horizontalCollision && !p.isSneaking()
                && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
        }
    }
}
