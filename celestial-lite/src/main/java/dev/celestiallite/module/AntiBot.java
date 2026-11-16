package dev.celestiallite.module;

import dev.celestiallite.CelestialLite;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Celestial's detection modes (Matrix / ReallyWorld) live in classes that were not uploaded, so this uses the usual
 * heuristics: a "player" that is not in the tab list, or has a non-v4 (NPC) UUID, is treated as a bot.
 * Attack Aura, Trigger Bot and the like skip bots.
 */
public class AntiBot extends Module {
    public AntiBot() { super("AntiBot"); }

    public static boolean isBot(Entity e) {
        if (!(e instanceof PlayerEntity) || !CelestialLite.on(AntiBot.class)) return false;
        PlayerEntity pl = (PlayerEntity) e;
        ClientPlayNetworkHandler h = MinecraftClient.getInstance().getNetworkHandler();
        if (h == null) return false;
        if (h.getPlayerListEntry(pl.getUuid()) == null) return true;
        return pl.getUuid().version() == 2;
    }
}
