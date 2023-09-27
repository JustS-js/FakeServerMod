package net.just_s.mixin.client;

import net.just_s.FSMClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class MixinInGameHud {
    @Redirect(method = "render",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;isInSingleplayer()Z"))
    private boolean renderPlayerList(MinecraftClient instance) {
        if (!FSMClient.isRunning || !FSMClient.isReceiver) return instance.isInSingleplayer();
        return false;
    }
}
