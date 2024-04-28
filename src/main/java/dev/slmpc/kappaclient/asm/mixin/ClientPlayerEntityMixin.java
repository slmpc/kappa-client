package dev.slmpc.kappaclient.asm.mixin;

import com.mojang.authlib.GameProfile;
import dev.slmpc.kappaclient.event.impl.PlayerMotionEvent;
import dev.slmpc.kappaclient.manager.impl.EventAccessManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class, priority = 800)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Unique
    public PlayerMotionEvent motionEvent;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onTickMovementHead(CallbackInfo callbackInfo) {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
            motionEvent = new PlayerMotionEvent(PlayerMotionEvent.StageType.START, this.getX(), getY(), this.getZ(), this.yaw, this.pitch, this.onGround);
            motionEvent.post();
            EventAccessManager.INSTANCE.setData(motionEvent);
            if (motionEvent.isCancelled()) {
                callbackInfo.cancel();
            }
        }
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getX()D"))
    private double posXHook(ClientPlayerEntity instance) {
        return motionEvent.getX();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getY()D"))
    private double posYHook(ClientPlayerEntity instance) {
        return motionEvent.getY();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getZ()D"))
    private double posZHook(ClientPlayerEntity instance) {
        return motionEvent.getZ();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float yawHook(ClientPlayerEntity instance) {
        return motionEvent.getYaw();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float pitchHook(ClientPlayerEntity instance) {
        return motionEvent.getPitch();
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isOnGround()Z"))
    private boolean groundHook(ClientPlayerEntity instance) {
        return motionEvent.isOnGround();
    }

    @Inject(method = "sendMovementPackets", at = @At(value = "RETURN"))
    private void sendMovementPackets_Return(CallbackInfo callbackInfo) {
        PlayerMotionEvent oldEvent = new PlayerMotionEvent(PlayerMotionEvent.StageType.END, motionEvent);
        oldEvent.post();
        EventAccessManager.INSTANCE.setData(oldEvent);
    }
}