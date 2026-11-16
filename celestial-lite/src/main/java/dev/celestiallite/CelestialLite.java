package dev.celestiallite;

import dev.celestiallite.module.*;
import dev.celestiallite.util.Friends;
import dev.celestiallite.util.LiteSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.ActionResult;

import java.util.ArrayList;
import java.util.List;

public class CelestialLite implements ClientModInitializer {
    public static final List<Module> MODULES = new ArrayList<>();

    public static <T extends Module> T get(Class<T> c) {
        for (Module m : MODULES) if (c.isInstance(m)) return c.cast(m);
        return null;
    }

    public static boolean on(Class<? extends Module> c) {
        Module m = get(c);
        return m != null && m.isEnabled();
    }

    @Override
    public void onInitializeClient() {
        MODULES.add(new AutoSprint());
        MODULES.add(new Fullbright());
        MODULES.add(new NoFall());
        MODULES.add(new AttackAura());
        MODULES.add(new TriggerBot());
        MODULES.add(new Criticals());
        MODULES.add(new Velocity());
        MODULES.add(new HitBoxes());
        MODULES.add(new AntiBot());
        MODULES.add(new FriendProtect());
        MODULES.add(new AutoTotem());
        MODULES.add(new AutoGApple());
        MODULES.add(new AutoPotion());
        MODULES.add(new AutoSwap());
        MODULES.add(new AutoExplosion());
        MODULES.add(new BowSpam());
        MODULES.add(new SuperBow());
        MODULES.add(new TapeMouse());
        MODULES.add(new HitSounds());
        MODULES.add(new AutoRespawn());

        LiteSounds.register();
        AutoExplosion.registerCallbacks();
        for (Module m : MODULES) m.registerKey();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            for (Module m : MODULES) m.pollKey();
            if (mc.player == null || mc.world == null) return;
            Friends.middleClickTick(mc);
            for (Module m : MODULES) if (m.isEnabled()) m.onTick(mc);
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient) {
                for (Module m : MODULES) if (m.isEnabled() && m.cancelAttack(entity)) return ActionResult.FAIL;
                for (Module m : MODULES) if (m.isEnabled()) m.onAttack(MinecraftClient.getInstance(), entity);
            }
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register(Hud::render);

        WorldRenderEvents.LAST.register(ctx -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            for (Module m : MODULES) if (m.isEnabled()) m.onWorld(ctx, mc);
        });
    }
}
