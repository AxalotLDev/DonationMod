package com.axalotl.donationmod.mixins;

import com.axalotl.donationmod.events.Values;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)

public class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    private float lastFovMultiplier;
    @Shadow
    private float fovMultiplier;

    @Shadow
    @Final
    private Camera camera;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
    private void rollCamera(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        if (Values.cameraRotate != 0f) {
            this.camera.getRotation().rotateZ((float) Math.toRadians(Values.cameraRotate));
        }
    }

    @Inject(
            method = "renderWorld",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"
            )
    )
    private void rollCamera(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack matrixStack) {
        if (Values.cameraRotate != 0f) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Values.cameraRotate));
        }
    }



    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    private void cancelRotate(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && Values.casinoActive) {
            CameraAccessor cameraAccessor = (CameraAccessor) MinecraftClient.getInstance().gameRenderer.getCamera();
            cameraAccessor.setPitch(0);
            cameraAccessor.setYaw(0);
            MinecraftClient.getInstance().player.setBodyYaw(0);
            MinecraftClient.getInstance().player.setMovementSpeed(0);
            MinecraftClient.getInstance().player.setVelocity(0, 0, 0);
        }
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    public void changeFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Float> cir) {
        if (Values.forceFov) {
            cir.setReturnValue(updateFov(camera, tickDelta, changingFov, Values.fov));
        }
    }

    @Unique
    private float updateFov(Camera camera, float tickDelta, boolean changingFov, double fovValue) {
        float fov = 70.0F;
        if (changingFov) {
            fov = (float) fovValue;
            fov *= MathHelper.lerp(tickDelta, this.lastFovMultiplier, this.fovMultiplier);
        }
        if (camera.getFocusedEntity() instanceof LivingEntity living && living.isDead()) {
            float f = Math.min(living.deathTime + tickDelta, 20.0F);
            fov /= ((1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F);
        }
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.LAVA || cameraSubmersionType == CameraSubmersionType.WATER) {
            fov *= (float) MathHelper.lerp(this.client.options.getFovEffectScale().getValue(), 1.0F, 0.85714287F);
        }
        return fov;
    }
}