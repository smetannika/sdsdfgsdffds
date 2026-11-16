package dev.celestiallite.module;

import dev.celestiallite.CelestialLite;
import dev.celestiallite.util.Inv;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/** After you place obsidian: places an end crystal on it and hits it. */
public class AutoExplosion extends Module {
    public static boolean lookAtCrystal = true;

    private static BlockPos pending;
    private static int age;
    private int placeCooldown;

    public AutoExplosion() { super("AutoExplosion"); }

    public static void registerCallbacks() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient && CelestialLite.on(AutoExplosion.class)
                    && player.getStackInHand(hand).getItem() == Items.OBSIDIAN) {
                pending = hit.getBlockPos().offset(hit.getSide());
                age = 0;
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public void onDisable() { pending = null; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (placeCooldown > 0) placeCooldown--;
        if (pending == null) return;
        ClientPlayerEntity p = mc.player;
        if (++age > 20 || p.squaredDistanceTo(Vec3d.ofCenter(pending)) > 36.0 || mc.interactionManager == null) {
            pending = null;
            return;
        }
        BlockState st = mc.world.getBlockState(pending);
        if (!st.isOf(Blocks.OBSIDIAN) && !st.isOf(Blocks.BEDROCK)) return; // wait until the server confirms the block

        BlockPos up = pending.up();
        Box box = new Box(up.getX(), up.getY(), up.getZ(), up.getX() + 1, up.getY() + 2, up.getZ() + 1);
        List<EndCrystalEntity> crystals = mc.world.getEntitiesByClass(EndCrystalEntity.class, box, e -> true);

        if (crystals.isEmpty()) {
            if (!mc.world.getBlockState(up).isAir() || placeCooldown > 0) return;
            Hand hand;
            if (p.getOffHandStack().getItem() == Items.END_CRYSTAL) {
                hand = Hand.OFF_HAND;
            } else {
                int slot = Inv.hotbar(p, s -> s.getItem() == Items.END_CRYSTAL);
                if (slot == -1) { pending = null; return; }
                Inv.select(mc, slot);
                hand = Hand.MAIN_HAND;
            }
            Vec3d hitVec = new Vec3d(pending.getX() + 0.5, pending.getY() + 1.0, pending.getZ() + 0.5);
            if (lookAtCrystal) look(mc, p, hitVec);
            mc.interactionManager.interactBlock(p, mc.world, hand, new BlockHitResult(hitVec, Direction.UP, pending, false));
            p.swingHand(hand);
            placeCooldown = 3;
        } else {
            EndCrystalEntity c = crystals.get(0);
            if (lookAtCrystal) look(mc, p, new Vec3d(c.getX(), c.getY() + 0.5, c.getZ()));
            mc.interactionManager.attackEntity(p, c);
            p.swingHand(Hand.MAIN_HAND);
            pending = null;
        }
    }

    private void look(MinecraftClient mc, ClientPlayerEntity p, Vec3d to) {
        Vec3d eye = p.getCameraPosVec(1.0f);
        double dx = to.x - eye.x, dy = to.y - eye.y, dz = to.z - eye.z;
        float yaw = MathHelper.wrapDegrees((float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0));
        float pitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz))), -90f, 90f);
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookOnly(yaw, pitch, p.isOnGround()));
    }
}
