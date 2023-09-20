package net.just_s.mixin.client;

import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "getListedPlayerListEntries", at = @At("HEAD"), cancellable = true)
    private void getListedPlayerListEntries(CallbackInfoReturnable<Collection<PlayerListEntry>> cir) {
        if (!FSMClient.isRunning || !FSMClient.isReceiver) return;
        for (PlayerListEntry entry : FSMClient.fakePlayers) {
            FSM.LOGGER.info("got player entry: " + entry.getProfile().getName());
        }
        cir.setReturnValue(FSMClient.fakePlayers);
    }
}
