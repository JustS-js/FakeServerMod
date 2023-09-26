package net.just_s.mixin.client;

import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener) {}

//    @Inject(method = "handlePacket", at = @At("HEAD"))
//    private static <T extends PacketListener> void inject(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
//        if (!FSMClient.isRunning || FSMClient.isReceiver) return;
//        if (    packet instanceof ChatMessageS2CPacket ||
//                packet instanceof GameMessageS2CPacket ||
//                packet instanceof PlayerListHeaderS2CPacket ||
//                packet instanceof PlayerListS2CPacket
//        ) {
//            int id = (packet instanceof ChatMessageS2CPacket) ? 0 :
//                    (packet instanceof GameMessageS2CPacket) ? 1 :
//                    (packet instanceof PlayerListHeaderS2CPacket) ? 2 : 3;
//            FSM.LOGGER.info("sending " + id);
//            FSMClient.sendFakePacket(packet, id);
//        }
//    }

//    @Redirect(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
//            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V"))
//    private <T extends PacketListener> void reader(Packet<T> packet, PacketListener listener) {
//        if (FSMClient.isRunning && FSMClient.isReceiver) {
//            if (
//                    packet instanceof ChatMessageS2CPacket ||
//                    packet instanceof GameMessageS2CPacket ||
//                    packet instanceof PlayerListHeaderS2CPacket ||
//                    packet instanceof PlayerListS2CPacket
//            ) {
//                return;
//            }
//        }
//        handlePacket(packet, listener);
//    }
}
