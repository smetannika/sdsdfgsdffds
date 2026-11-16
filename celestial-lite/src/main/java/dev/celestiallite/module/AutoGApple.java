package dev.celestiallite.module;

import dev.celestiallite.util.Inv;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Eats a golden apple when health drops under the threshold. */
public class AutoGApple extends Module {
    public static float health = 15.0f;

    private boolean eating;
    private int prevSlot = -1;

    public AutoGApple() { super("AutoGApple"); }

    private static boolean isGapple(ItemStack s) {
        return s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
    }

    @Override
    public void onDisable() {
        stop(MinecraftClient.getInstance());
    }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (mc.currentScreen != null) { stop(mc); return; }
        boolean cooling = p.getItemCooldownManager().isCoolingDown(Items.GOLDEN_APPLE)
                || p.getItemCooldownManager().isCoolingDown(Items.ENCHANTED_GOLDEN_APPLE);
        int slot = Inv.hotbar(p, AutoGApple::isGapple);
        boolean off = isGapple(p.getOffHandStack());
        boolean want = p.getHealth() <= health && !cooling && (slot != -1 || off);
        if (want) {
            if (!eating) {
                prevSlot = p.inventory.selectedSlot;
                eating = true;
            }
            if (!off && slot != -1) Inv.select(mc, slot);
            mc.options.keyUse.setPressed(true);
        } else if (eating) {
            stop(mc);
        }
    }

    private void stop(MinecraftClient mc) {
        if (!eating) return;
        mc.options.keyUse.setPressed(false);
        if (prevSlot != -1 && mc.player != null) Inv.select(mc, prevSlot);
        prevSlot = -1;
        eating = false;
    }
}
