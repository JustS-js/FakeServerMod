package net.just_s.mixin.client;

import com.mojang.authlib.GameProfile;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "onChatMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    private void getListedPlayerListEntries(MessageHandler instance, SignedMessage message, GameProfile sender, MessageType.Parameters params) {
        // todo: serialize and send PARAMS (MessageType.Parameters) and MESSAGE (String)
        FSMClient.MC.inGameHud.getChatHud().addMessage(
                params.applyChatDecoration(
                        Text.literal(message.getSignedContent())
                )
        );
    }

    @Redirect(method = "onGameMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"))
    private void getListedPlayerListEntries(MessageHandler instance, Text message, boolean overlay) {
        // todo: serialize and send MESSAGE (Text) and OVERLAY (boolean)
        FSMClient.MC.getMessageHandler().onGameMessage(message, overlay);
    }
}
