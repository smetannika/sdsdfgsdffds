package dev.celestiallite.util;

import dev.celestiallite.module.AntiBot;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;

public class Combat {
    public static boolean validTarget(ClientPlayerEntity p, Entity e, boolean players, boolean mobs) {
        if (!(e instanceof LivingEntity) || e == p || e instanceof ArmorStandEntity) return false;
        LivingEntity le = (LivingEntity) e;
        if (!le.isAlive() || le.isSpectator()) return false;
        if (Friends.isFriend(le) || AntiBot.isBot(le)) return false;
        return (players && le instanceof PlayerEntity) || (mobs && le instanceof Monster);
    }

    public static boolean critReady(MinecraftClient mc, ClientPlayerEntity p, boolean onlyCrits, boolean onlySpace) {
        if (!onlyCrits) return true;
        if (onlySpace && !mc.options.keyJump.isPressed()) return true;
        return p.fallDistance > 0.0f && !p.isOnGround() && !p.isClimbing() && !p.isTouchingWater()
                && !p.hasStatusEffect(StatusEffects.BLINDNESS) && !p.hasVehicle();
    }
}
