package dev.celestiallite.module;

import dev.celestiallite.util.Inv;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Hand;

/** Throws splash potions at your feet when strength / speed / fire resistance are missing. */
public class AutoPotion extends Module {
    public static boolean strength = true;
    public static boolean speed = true;
    public static boolean fireResistance = true;
    public static boolean autoOff = false;

    private long last;

    public AutoPotion() { super("AutoPotion"); }

    private static boolean has(ItemStack s, StatusEffect eff) {
        if (!(s.getItem() instanceof SplashPotionItem)) return false;
        for (StatusEffectInstance i : PotionUtil.getPotionEffects(s)) if (i.getEffectType() == eff) return true;
        return false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (mc.currentScreen != null || mc.interactionManager == null || mc.getNetworkHandler() == null) return;
        if (System.currentTimeMillis() - last < 600) return;

        StatusEffect[] effs = {
                strength ? StatusEffects.STRENGTH : null,
                speed ? StatusEffects.SPEED : null,
                fireResistance ? StatusEffects.FIRE_RESISTANCE : null
        };
        boolean any = false;
        for (StatusEffect eff : effs) {
            if (eff == null) continue;
            StatusEffectInstance cur = p.getStatusEffect(eff);
            if (cur != null && cur.getDuration() > 200) continue;
            int idx = Inv.any(p, s -> has(s, eff));
            if (idx == -1) continue;
            throwPotion(mc, p, idx);
            last = System.currentTimeMillis();
            any = true;
            break;
        }
        if (!any && autoOff) {
            boolean allOk = true;
            for (StatusEffect eff : effs) if (eff != null && !p.hasStatusEffect(eff)) allOk = false;
            if (allOk) toggle();
        }
    }

    private void throwPotion(MinecraftClient mc, ClientPlayerEntity p, int idx) {
        int prev = p.inventory.selectedSlot;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly(p.yaw, 90.0f, p.isOnGround()));
        if (idx < 9) {
            Inv.select(mc, idx);
            mc.interactionManager.interactItem(p, mc.world, Hand.MAIN_HAND);
            p.swingHand(Hand.MAIN_HAND);
            Inv.select(mc, prev);
        } else {
            Inv.swapHotbar(mc, idx, prev);
            mc.interactionManager.interactItem(p, mc.world, Hand.MAIN_HAND);
            p.swingHand(Hand.MAIN_HAND);
            Inv.swapHotbar(mc, idx, prev);
        }
    }
}
