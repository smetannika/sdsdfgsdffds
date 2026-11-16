package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Knockback editor. Celestial's mode classes were not in the upload, so these are my own implementations
 * of the mode names: VANILLA = no knockback, CUSTOM = scaled, JUMP = jump while hurt, JUMP_RESET = jump on the first hurt tick.
 */
public class Velocity extends Module {
    public enum Mode { VANILLA, CUSTOM, JUMP, JUMP_RESET }

    public static Mode mode = Mode.VANILLA;
    public static float horizontal = 0f;  // percent, CUSTOM mode
    public static float vertical = 0f;    // percent, CUSTOM mode

    public Velocity() { super("Velocity"); }

    /** called from the network mixin; null = leave velocity alone */
    public static Vec3d scale(Vec3d v) {
        if (!dev.celestiallite.CelestialLite.on(Velocity.class)) return null;
        switch (mode) {
            case VANILLA: return Vec3d.ZERO;
            case CUSTOM: return new Vec3d(v.x * horizontal / 100.0, v.y * vertical / 100.0, v.z * horizontal / 100.0);
            default: return null;
        }
    }

    @Override
    public void onTick(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        if (mode == Mode.JUMP && p.hurtTime > 0 && p.isOnGround()) p.jump();
        if (mode == Mode.JUMP_RESET && p.hurtTime == 9 && p.isOnGround()) p.jump();
    }
}
