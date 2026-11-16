package dev.celestiallite.module;

import dev.celestiallite.util.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;

/** Attacks whatever is under the crosshair. */
public class TriggerBot extends Module {
    public static boolean players = true;
    public static boolean mobs = true;
    public static boolean onlyCrits = true;
    public static boolean onlySpace = false;
    public static boolean stopOnUse = false;
    public static boolean stopOnContainer = false;
    public static boolean targetHud = true;

    private LivingEntity target;

    public TriggerBot() { super("TriggerBot"); }

    @Override
    public void onDisable() { target = null; }

    @Override
    public void onTick(MinecraftClient mc) {
        target = null;
        ClientPlayerEntity p = mc.player;
        if (mc.interactionManager == null || !(mc.crosshairTarget instanceof EntityHitResult)) return;
        if (stopOnContainer && mc.currentScreen instanceof HandledScreen) return;
        if (mc.currentScreen != null) return;
        Entity e = ((EntityHitResult) mc.crosshairTarget).getEntity();
        if (!Combat.validTarget(p, e, players, mobs)) return;
        target = (LivingEntity) e;
        if (stopOnUse && p.isUsingItem()) return;
        if (p.getAttackCooldownProgress(0.5f) < 0.92f) return;
        if (!Combat.critReady(mc, p, onlyCrits, onlySpace)) return;
        mc.interactionManager.attackEntity(p, e);
        p.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onHud(MatrixStack m, MinecraftClient mc) {
        if (targetHud && target != null && target.isAlive()) AttackAura.drawTargetHud(m, mc, target);
    }
}
