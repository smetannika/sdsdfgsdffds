package dev.celestiallite.mixin;

import dev.celestiallite.module.SuperBow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "stopUsingItem", at = @At("HEAD"))
    private void lite$release(PlayerEntity player, CallbackInfo ci) {
        SuperBow.onRelease(MinecraftClient.getInstance(), player);
    }
}
