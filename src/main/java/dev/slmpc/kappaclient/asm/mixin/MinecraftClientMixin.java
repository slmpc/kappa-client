package dev.slmpc.kappaclient.asm.mixin;

import dev.slmpc.kappaclient.event.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;startTick()V"))
    public void run(CallbackInfo ci) {
        GameLoopEvent.Tick.INSTANCE.post();
    }

    @Inject(method = "run", at = @At(value = "TAIL"))
    public void onQuit(CallbackInfo ci) {
        new CloseGameEvent().post();
    }

    @Inject(method = "joinWorld", at = @At("RETURN"))
    public void joinWorld(ClientWorld world, CallbackInfo ci) {
        new JoinWorldEvent().post();
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    public void disconnect(Screen screen, CallbackInfo ci) {
        new DisconnectEvent().post();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTickPre(CallbackInfo ci) {
        TickEvent.Pre.INSTANCE.post();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTickPost(CallbackInfo ci) {
        TickEvent.Post.INSTANCE.post();
    }

}
