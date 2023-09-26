package net.just_s.mixin.client;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.FilterMask;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Redirect(method = "onChatMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    private void onChatMessage(MessageHandler instance, SignedMessage message, GameProfile sender, MessageType.Parameters params) {

        if (FSMClient.isRunning && !FSMClient.isReceiver) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(FSMClient.PacketId.GAMEMSG.ordinal());

            Text text = params.applyChatDecoration(Text.literal(message.getSignedContent()));
            buf.writeString(Text.Serializer.toJson(text));
            buf.writeBoolean(false);

            FSMClient.sendPacket(buf);
        }

        instance.onChatMessage(message, sender, params);
    }

    @Redirect(method = "onGameMessage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"))
    private void onGameMessage(MessageHandler instance, Text message, boolean overlay) {
        if (FSMClient.isRunning && !FSMClient.isReceiver) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeByte(FSMClient.PacketId.GAMEMSG.ordinal());

            buf.writeString(Text.Serializer.toJson(message));
            buf.writeBoolean(overlay);

            FSMClient.sendPacket(buf);
        }

        instance.onGameMessage(message, overlay);
    }

    @Inject(method = "onPlayerListHeader", at = @At("TAIL"), cancellable = true)
    private void onPlayerListHeader(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
        if (!FSMClient.isRunning) return;
        if (FSMClient.isReceiver) {ci.cancel();return;}

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(FSMClient.PacketId.HEADERLIST.ordinal());

        buf.writeBoolean(packet.getHeader().getString().isEmpty());
        buf.writeBoolean(packet.getFooter().getString().isEmpty());
        buf.writeString(
                packet.getHeader().getString().isEmpty() ? "" : Text.Serializer.toJson(packet.getHeader())
        );
        buf.writeString(
                packet.getFooter().getString().isEmpty() ? "" : Text.Serializer.toJson(packet.getFooter())
        );

        FSMClient.sendPacket(buf);

    }
}
