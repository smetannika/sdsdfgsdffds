package dev.celestiallite.module;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.lwjgl.glfw.GLFW;

public abstract class Module {
    public final String name;
    private boolean enabled;
    private KeyBinding key;

    protected Module(String name) {
        this.name = name;
    }

    public void registerKey() {
        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.celestiallite." + name.toLowerCase(),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.celestiallite"));
    }

    public void pollKey() {
        while (key != null && key.wasPressed()) toggle();
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable(); else onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onAttack(MinecraftClient mc, Entity target) {}
    public boolean cancelAttack(Entity target) { return false; }
    public void onHud(MatrixStack matrices, MinecraftClient mc) {}
    public void onWorld(WorldRenderContext ctx, MinecraftClient mc) {}
}
