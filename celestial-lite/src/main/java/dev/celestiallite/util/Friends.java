package dev.celestiallite.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Middle click on a player = add/remove friend. */
public class Friends {
    private static final Set<String> NAMES = new HashSet<>();
    private static boolean wasDown;

    public static boolean isFriend(Entity e) {
        return e instanceof PlayerEntity && NAMES.contains(e.getName().getString().toLowerCase(Locale.ROOT));
    }

    public static void middleClickTick(MinecraftClient mc) {
        boolean down = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
        if (down && !wasDown && mc.currentScreen == null && mc.crosshairTarget instanceof EntityHitResult) {
            Entity e = ((EntityHitResult) mc.crosshairTarget).getEntity();
            if (e instanceof PlayerEntity && e != mc.player) {
                String n = e.getName().getString();
                String k = n.toLowerCase(Locale.ROOT);
                if (NAMES.remove(k)) {
                    mc.player.sendMessage(new LiteralText("[Lite] " + n + " removed from friends"), false);
                } else {
                    NAMES.add(k);
                    mc.player.sendMessage(new LiteralText("[Lite] " + n + " added to friends"), false);
                }
            }
        }
        wasDown = down;
    }
}
