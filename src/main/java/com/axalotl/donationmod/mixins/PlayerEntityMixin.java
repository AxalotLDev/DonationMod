package com.axalotl.donationmod.mixins;

import com.axalotl.donationmod.events.Values;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void jump() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getWorld().isClient && Values.enableNoJump) {
            return;
        }
        super.jump();
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void onMove(CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.getWorld().isClient && player.isOnGround() && Values.disablePlayerMovements || Values.casinoActive) {
            if (player != null) {
                player.setVelocity(0, 0, 0);
            }
            if (player != null) {
                player.setMovementSpeed(0);
            }
            ci.cancel();
        }
    }
}
