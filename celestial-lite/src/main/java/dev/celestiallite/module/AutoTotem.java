package dev.celestiallite.module;

import dev.celestiallite.util.Inv;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Puts a totem in the off hand when health is low or a threat is near. Funtime/Advanced modes were not uploaded. */
public class AutoTotem extends Module {
    public static float health = 5.0f;
    public static boolean crystal = true;
    public static float crystalRadius = 6.0f;
    public static boolean tnt = false;
    public static float tntRadius = 5.0f;
    public static boolean fall = true;
    public static boolean counter = true;

    private int cooldown;

    public AutoTotem() { super("AutoTotem"); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (cooldown > 0) cooldown--;
        ClientPlayerEntity p = mc.player;
        if (mc.currentScreen != null || mc.interactionManager == null) return;
        if (p.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
        if (cooldown > 0 || !threat(mc, p)) return;
        int slot = Inv.any(p, s -> s.getItem() == Items.TOTEM_OF_UNDYING);
        if (slot == -1) return;
        Inv.swapOffhand(mc, slot);
        cooldown = 3;
    }

    private boolean threat(MinecraftClient mc, ClientPlayerEntity p) {
        float hp = p.getHealth() + p.getAbsorptionAmount();
        if (hp <= health) return true;
        if (fall && !p.isFallFlying() && p.fallDistance - 3.0f >= hp) return true;
        if (crystal && !mc.world.getEntitiesByClass(EndCrystalEntity.class, p.getBoundingBox().expand(crystalRadius), e -> true).isEmpty()) return true;
        if (tnt && !mc.world.getEntitiesByClass(TntEntity.class, p.getBoundingBox().expand(tntRadius), e -> true).isEmpty()) return true;
        return false;
    }

    @Override
    public void onHud(MatrixStack m, MinecraftClient mc) {
        if (!counter) return;
        int n = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStack(i);
            if (s.getItem() == Items.TOTEM_OF_UNDYING) n += s.getCount();
        }
        if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) n += mc.player.getOffHandStack().getCount();
        int x = mc.getWindow().getScaledWidth() / 2 + 98;
        int y = mc.getWindow().getScaledHeight() - 20;
        mc.textRenderer.drawWithShadow(m, "Totems: " + n, x, y, n == 0 ? 0xFFFF5555 : 0xFFFFFFFF);
    }
}
