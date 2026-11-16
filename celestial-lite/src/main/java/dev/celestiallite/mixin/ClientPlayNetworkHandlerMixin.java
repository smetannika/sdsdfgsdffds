package dev.celestiallite.mixin;

import dev.celestiallite.module.Velocity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow private MinecraftClient client;
    @Unique private Vec3d lite$before;

    @Inject(method = "onEntityVelocityUpdate", at = @At("TAIL"))
    private void lite$knockback(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        if (client.player == null || packet.getId() != client.player.getEntityId()) return;
        Vec3d res = Velocity.scale(client.player.getVelocity());
        if (res != null) client.player.setVelocity(res);
    }

    @Inject(method = "onExplosion", at = @At("HEAD"))
    private void lite$explosionHead(ExplosionS2CPacket packet, CallbackInfo ci) {
        lite$before = (client.isOnThread() && client.player != null) ? client.player.getVelocity() : null;
    }

    @Inject(method = "onExplosion", at = @At("TAIL"))
    private void lite$explosionTail(ExplosionS2CPacket packet, CallbackInfo ci) {
        if (lite$before == null || client.player == null) return;
        Vec3d added = client.player.getVelocity().subtract(lite$before);
        Vec3d scaled = Velocity.scale(added);
        if (scaled != null) client.player.setVelocity(lite$before.add(scaled));
        lite$before = null;
    }
}
