package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BowItem;

/** Releases the bow after `delay` ticks of charge (hold right click to keep shooting). */
public class BowSpam extends Module {
    public static float delay = 2.0f; // ticks, 2..5

    public BowSpam() { super("BowSpam"); }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (mc.interactionManager == null) return;
        if (p.isUsingItem() && p.getActiveItem().getItem() instanceof BowItem && p.getItemUseTime() > delay) {
            mc.interactionManager.stopUsingItem(p);
        }
    }
}
