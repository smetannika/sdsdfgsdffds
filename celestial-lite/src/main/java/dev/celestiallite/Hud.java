package dev.celestiallite;

import dev.celestiallite.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class Hud {
    public static void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.player == null) return;
        TextRenderer tr = mc.textRenderer;

        tr.drawWithShadow(matrices, "Celestial Lite", 4, 4, 0xFFB388FF);

        List<String> names = new ArrayList<>();
        for (Module m : CelestialLite.MODULES) if (m.isEnabled()) names.add(m.name);
        names.sort((a, b) -> Integer.compare(tr.getWidth(b), tr.getWidth(a)));

        for (Module m : CelestialLite.MODULES) if (m.isEnabled()) m.onHud(matrices, mc);

        int w = mc.getWindow().getScaledWidth();
        int y = 4;
        for (String n : names) {
            tr.drawWithShadow(matrices, n, w - tr.getWidth(n) - 4, y, 0xFFFFFFFF);
            y += tr.fontHeight + 2;
        }
    }
}
