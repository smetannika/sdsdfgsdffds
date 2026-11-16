package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {
    public NoFall() { super("NoFall"); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player.fallDistance > 2.0f && !mc.player.isFallFlying() && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        }
    }
}
