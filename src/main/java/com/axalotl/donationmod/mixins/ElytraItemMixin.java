package com.axalotl.donationmod.mixins;

import com.axalotl.donationmod.events.Values;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class ElytraItemMixin {
    @Inject(method = "canGlide", at = @At(value = "HEAD"), cancellable = true)
    private static void hookDisableFlight(CallbackInfoReturnable<Boolean> cir) {
        if (Values.disableElytra) {
            cir.setReturnValue(false);
        }
    }
}
