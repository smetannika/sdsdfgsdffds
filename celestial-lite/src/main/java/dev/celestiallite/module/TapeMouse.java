package dev.celestiallite.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;

/** Clicks left or right mouse button every N seconds. */
public class TapeMouse extends Module {
    public static boolean leftClick = true; // false = right click
    public static float delaySeconds = 0.5f;

    private long last;

    public TapeMouse() { super("TapeMouse"); }

    @Override
    public void onEnable() { last = System.currentTimeMillis(); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.currentScreen != null) return;
        if (System.currentTimeMillis() - last < delaySeconds * 1000f) return;
        KeyBinding kb = leftClick ? mc.options.keyAttack : mc.options.keyUse;
        KeyBinding.onKeyPressed(InputUtil.fromTranslationKey(kb.getBoundKeyTranslationKey()));
        last = System.currentTimeMillis();
    }
}
