package dev.celestiallite.module;

import dev.celestiallite.CelestialLite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/** Extra damage from bows: spams tiny position packets right before the arrow is released. */
public class SuperBow extends Module {
    public static float power = 50.0f; // 10..100 packets pairs

    public SuperBow() { super("SuperBow"); }

    /** called from ClientPlayerInteractionManagerMixin right before the release packet is sent */
    public static void onRelease(MinecraftClient mc, PlayerEntity player) {
        if (!CelestialLite.on(SuperBow.class) || mc.player == null || player != mc.player || mc.getNetworkHandler() == null) return;
        if (!(player.isUsingItem() && player.getActiveItem().getItem() instanceof BowItem)) return;
        mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        for (int i = 0; i < (int) power; i++) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(player.getX(), player.getY() + 1.0E-10, player.getZ(), false));
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionOnly(player.getX(), player.getY() - 1.0E-10, player.getZ(), true));
        }
    }
}
