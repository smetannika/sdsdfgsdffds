package dev.celestiallite.module;

import dev.celestiallite.util.Inv;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

/**
 * Swaps the off-hand item. Bind mode: press the swap key (Options > Controls) to flip between FROM and TO.
 * Smart mode: swaps FROM -> TO when health is low (or an end crystal is close).
 * Celestial's RUNE / SPHERE items are server-specific and not included; BALL = player head.
 */
public class AutoSwap extends Module {
    public enum Kind { SHIELD, GAPPLE, BALL, TOTEM }

    public static boolean smart = true;
    public static Kind from = Kind.SHIELD;
    public static Kind to = Kind.GAPPLE;
    public static float health = 16.5f;
    public static boolean checkCrystal = true;

    private KeyBinding swapKey;
    private int cooldown;

    public AutoSwap() { super("AutoSwap"); }

    @Override
    public void registerKey() {
        super.registerKey();
        swapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.celestiallite.autoswap.swap", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.celestiallite"));
    }

    private static boolean matches(Kind k, ItemStack s) {
        switch (k) {
            case SHIELD: return s.getItem() == Items.SHIELD;
            case GAPPLE: return s.getItem() == Items.GOLDEN_APPLE || s.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
            case BALL: return s.getItem() == Items.PLAYER_HEAD;
            default: return s.getItem() == Items.TOTEM_OF_UNDYING;
        }
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (cooldown > 0) cooldown--;
        ClientPlayerEntity p = mc.player;
        boolean pressed = false;
        while (swapKey != null && swapKey.wasPressed()) pressed = true;
        if (mc.currentScreen != null || mc.interactionManager == null) return;

        if (!smart) {
            if (pressed) flip(mc, p);
            return;
        }
        if (cooldown > 0 || !matches(from, p.getOffHandStack())) return;
        boolean crystalNear = checkCrystal
                && !mc.world.getEntitiesByClass(EndCrystalEntity.class, p.getBoundingBox().expand(6.0), e -> true).isEmpty();
        if (p.getHealth() + p.getAbsorptionAmount() <= health || crystalNear) {
            int idx = Inv.any(p, s -> matches(to, s));
            if (idx != -1) {
                Inv.swapOffhand(mc, idx);
                cooldown = 10;
            }
        }
    }

    private void flip(MinecraftClient mc, ClientPlayerEntity p) {
        Kind want = matches(to, p.getOffHandStack()) ? from : to;
        int idx = Inv.any(p, s -> matches(want, s));
        if (idx != -1) Inv.swapOffhand(mc, idx);
    }
}
