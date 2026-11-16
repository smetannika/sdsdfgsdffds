package dev.celestiallite.module;

import dev.celestiallite.CelestialLite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

public class HitBoxes extends Module {
    public static float size = 0.2f;
    public static boolean players = true;
    public static boolean mobs = false;

    public HitBoxes() { super("Hitboxes"); }

    /** called from EntityMixin: extra targeting margin, or 0 */
    public static float margin(Entity e, Entity self) {
        if (e == self || !CelestialLite.on(HitBoxes.class)) return 0f;
        if ((players && e instanceof PlayerEntity) || (mobs && e instanceof Monster)) return size;
        return 0f;
    }
}
