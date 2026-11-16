package dev.celestiallite.util;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LiteSounds {
    public static SoundEvent UWU, BONK, MARKER;
    public static SoundEvent[] MOANS = new SoundEvent[4];

    private static SoundEvent reg(String path) {
        Identifier id = new Identifier("celestiallite", path);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void register() {
        UWU = reg("hit_uwu");
        BONK = reg("hit_bonk");
        MARKER = reg("hit_marker");
        for (int i = 0; i < 4; i++) MOANS[i] = reg("hit_moan_" + (i + 1));
    }
}
