package dev.celestiallite.module;

import dev.celestiallite.util.LiteSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundEvent;

import java.util.concurrent.ThreadLocalRandom;

/** Plays a sound on every hit. Sounds are Celestial's own (assets/celestiallite/sounds/hitsounds). */
public class HitSounds extends Module {
    public enum Sound { UWU, BONK, MOANS, MARKER }

    public static Sound sound = Sound.UWU;
    public static float pitch = 1.0f;

    public HitSounds() { super("HitSounds"); }

    @Override
    public void onAttack(MinecraftClient mc, Entity target) {
        if (!(target instanceof LivingEntity)) return;
        SoundEvent ev;
        switch (sound) {
            case BONK: ev = LiteSounds.BONK; break;
            case MARKER: ev = LiteSounds.MARKER; break;
            case MOANS: ev = LiteSounds.MOANS[ThreadLocalRandom.current().nextInt(4)]; break;
            default: ev = LiteSounds.UWU;
        }
        mc.getSoundManager().play(PositionedSoundInstance.master(ev, pitch, 1.0f));
    }
}
