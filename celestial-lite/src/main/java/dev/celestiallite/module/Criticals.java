package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import java.util.concurrent.ThreadLocalRandom;

/** Port of Celestial Criticals: while airborne, send a slightly lowered, slightly rotated position packet before the hit. */
public class Criticals extends Module {
    public Criticals() { super("Criticals"); }

    @Override
    public void onAttack(MinecraftClient mc, Entity target) {
        ClientPlayerEntity p = mc.player;
        if (p == null || mc.getNetworkHandler() == null || p.isOnGround()) return;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        float yaw = p.yaw + (float) r.nextDouble(-0.0015, 0.00105);
        float pitch = p.pitch + (float) r.nextDouble(-0.0015, 0.00105);
        double y = p.getY() - r.nextDouble(3.5E-5, 6.5E-5);
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Both(p.getX(), y, p.getZ(), yaw, pitch, false));
    }
}
