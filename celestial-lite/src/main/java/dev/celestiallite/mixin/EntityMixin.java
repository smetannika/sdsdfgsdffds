package dev.celestiallite.mixin;

import dev.celestiallite.module.HitBoxes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getTargetingMargin", at = @At("HEAD"), cancellable = true)
    private void lite$hitbox(CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        float v = HitBoxes.margin(self, MinecraftClient.getInstance().player);
        if (v > 0f) cir.setReturnValue(v);
    }
}
