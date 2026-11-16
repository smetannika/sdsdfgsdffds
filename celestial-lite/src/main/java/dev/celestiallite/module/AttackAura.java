package dev.celestiallite.module;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Readable port of Celestial's "Attack Aura" settings (Funtime/Matrix/Snap/SpookyTime rotation modes are not included). */
public class AttackAura extends Module {
    public enum Sort { SMART, FOV, DISTANCE, HEALTH }

    // ---- settings (edit here) ----
    public static boolean targetPlayers = true;
    public static boolean targetMobs = true;
    public static Sort sort = Sort.SMART;
    public static double hitRange = 3.0;       // "Дистанция удара"
    public static double aimExtra = 0.0;       // "Дистанция наводки": rotate toward target this much farther than hitRange
    public static boolean onlyCrits = true;    // "Только криты"
    public static boolean onlySpace = false;   // "Только с пробелом": crit-wait only while holding jump
    public static boolean stopOnUse = false;   // "Останавливать при использовании предмета"
    public static boolean throughWalls = true; // "Бить через стены"
    public static boolean breakShield = true;  // "Ломать щит" (swaps to an axe for the hit)
    public static boolean silent = true;       // "Silent"
    public static boolean targetHud = true;    // "Таргет-HUD"
    public static boolean esp = true;          // "ESP"

    private LivingEntity target;

    public AttackAura() { super("AttackAura"); }

    @Override
    public void onDisable() { target = null; }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        target = null;
        if (p == null || mc.world == null || mc.interactionManager == null || mc.getNetworkHandler() == null) return;
        if (mc.currentScreen != null || !p.isAlive() || p.isSpectator()) return;

        target = findTarget(mc, p);
        if (target == null) return;

        rotate(mc, p, target);

        if (stopOnUse && p.isUsingItem()) return;
        if (distanceTo(p, target) > hitRange) return;
        if (p.getAttackCooldownProgress(0.5f) < 0.92f) return;
        if (!critReady(mc, p)) return;

        int prevSlot = p.inventory.selectedSlot;
        if (breakShield && target.isBlocking()) {
            int axe = findAxe(p);
            if (axe != -1 && axe != prevSlot) {
                p.inventory.selectedSlot = axe;
                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(axe));
            }
        }
        mc.interactionManager.attackEntity(p, target);
        p.swingHand(Hand.MAIN_HAND);
        if (p.inventory.selectedSlot != prevSlot) {
            p.inventory.selectedSlot = prevSlot;
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(prevSlot));
        }
    }

    private boolean critReady(MinecraftClient mc, ClientPlayerEntity p) {
        if (!onlyCrits) return true;
        if (onlySpace && !mc.options.keyJump.isPressed()) return true; // not holding space -> don't wait for crit
        boolean crit = p.fallDistance > 0.0f && !p.isOnGround() && !p.isClimbing() && !p.isTouchingWater()
                && !p.hasStatusEffect(StatusEffects.BLINDNESS) && !p.hasVehicle();
        return crit;
    }

    private void rotate(MinecraftClient mc, ClientPlayerEntity p, LivingEntity t) {
        Vec3d eye = p.getCameraPosVec(1.0f);
        double dx = t.getX() - eye.x;
        double dy = t.getBodyY(0.7) - eye.y;
        double dz = t.getZ() - eye.z;
        float yaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        float pitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))), -90f, 90f);
        if (silent) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly(yaw, pitch, p.isOnGround()));
        } else {
            p.yaw = yaw;
            p.pitch = pitch;
        }
    }

    private int findAxe(ClientPlayerEntity p) {
        for (int i = 0; i < 9; i++) {
            if (p.inventory.getStack(i).getItem() instanceof AxeItem) return i;
        }
        return -1;
    }

    private double distanceTo(ClientPlayerEntity p, Entity e) {
        Vec3d eye = p.getCameraPosVec(1.0f);
        Box b = e.getBoundingBox();
        double x = MathHelper.clamp(eye.x, b.minX, b.maxX);
        double y = MathHelper.clamp(eye.y, b.minY, b.maxY);
        double z = MathHelper.clamp(eye.z, b.minZ, b.maxZ);
        return eye.distanceTo(new Vec3d(x, y, z));
    }

    private LivingEntity findTarget(MinecraftClient mc, ClientPlayerEntity p) {
        List<LivingEntity> list = new ArrayList<>();
        double max = hitRange + aimExtra;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity) || e == p || e instanceof ArmorStandEntity) continue;
            LivingEntity le = (LivingEntity) e;
            if (!le.isAlive() || le.isSpectator()) continue;
            if (dev.celestiallite.util.Friends.isFriend(le) || AntiBot.isBot(le)) continue;
            if (!((targetPlayers && le instanceof PlayerEntity) || (targetMobs && le instanceof Monster))) continue;
            if (distanceTo(p, le) > max) continue;
            if (!throughWalls && !p.canSee(le)) continue;
            list.add(le);
        }
        if (list.isEmpty()) return null;
        Comparator<LivingEntity> cmp;
        switch (sort) {
            case FOV:
                cmp = Comparator.comparingDouble(le -> fovDiff(p, le));
                break;
            case DISTANCE:
                cmp = Comparator.comparingDouble(le -> distanceTo(p, le));
                break;
            case HEALTH:
                cmp = Comparator.comparingDouble(le -> le.getHealth() + le.getAbsorptionAmount());
                break;
            default: // SMART: low health + close + near crosshair
                cmp = Comparator.comparingDouble(le -> le.getHealth() + le.getAbsorptionAmount()
                        + distanceTo(p, le) * 2.0 + fovDiff(p, le) / 30.0);
        }
        list.sort(cmp);
        return list.get(0);
    }

    private double fovDiff(ClientPlayerEntity p, LivingEntity le) {
        double dx = le.getX() - p.getX();
        double dz = le.getZ() - p.getZ();
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        return Math.abs(MathHelper.wrapDegrees(yaw - p.yaw));
    }

    // ---- Target HUD ----
    @Override
    public void onHud(MatrixStack m, MinecraftClient mc) {
        if (!targetHud || target == null || !target.isAlive()) return;
        drawTargetHud(m, mc, target);
    }

    public static void drawTargetHud(MatrixStack m, MinecraftClient mc, LivingEntity target) {
        int x = mc.getWindow().getScaledWidth() / 2 + 24;
        int y = mc.getWindow().getScaledHeight() / 2 + 24;
        int w = 110, h = 34;
        DrawableHelper.fill(m, x, y, x + w, y + h, 0xAA101018);
        mc.textRenderer.drawWithShadow(m, target.getName().getString(), x + 6, y + 5, 0xFFFFFFFF);
        float hp = target.getHealth();
        float max = Math.max(target.getMaxHealth(), 1f);
        float frac = MathHelper.clamp(hp / max, 0f, 1f);
        int bx = x + 6, by = y + 20, bw = w - 12;
        DrawableHelper.fill(m, bx, by, bx + bw, by + 6, 0xFF2A2A35);
        int col = frac > 0.5f ? 0xFF55FF55 : frac > 0.25f ? 0xFFFFAA00 : 0xFFFF5555;
        DrawableHelper.fill(m, bx, by, bx + (int) (bw * frac), by + 6, col);
        mc.textRenderer.drawWithShadow(m, String.format("%.1f", hp), x + w - 30, y + 5, 0xFFCCCCCC);
    }

    // ---- ESP: ring around the target ----
    @Override
    public void onWorld(WorldRenderContext ctx, MinecraftClient mc) {
        if (!esp || target == null || !target.isAlive()) return;
        float td = ctx.tickDelta();
        Vec3d cam = ctx.camera().getPos();
        double x = MathHelper.lerp(td, target.lastRenderX, target.getX()) - cam.x;
        double y = MathHelper.lerp(td, target.lastRenderY, target.getY()) - cam.y
                + target.getHeight() * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 400.0));
        double z = MathHelper.lerp(td, target.lastRenderZ, target.getZ()) - cam.z;
        double r = target.getWidth() * 0.8 + 0.2;

        Matrix4f mat = ctx.matrixStack().peek().getModel();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.lineWidth(2.5f);
        BufferBuilder bb = Tessellator.getInstance().getBuffer();
        bb.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 48; i++) {
            double a = Math.PI * 2 * i / 48.0;
            bb.vertex(mat, (float) (x + Math.cos(a) * r), (float) y, (float) (z + Math.sin(a) * r))
                    .color(179, 136, 255, 230).next();
        }
        Tessellator.getInstance().draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
