package net.just_s.mixin.client;

import net.just_s.FSMClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static <T extends PacketListener> void inject(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (!FSMClient.isRunning || FSMClient.isReceiver) return;
        if (    packet instanceof GameMessageS2CPacket ||
                packet instanceof PlayerListHeaderS2CPacket ||
                packet instanceof PlayerListS2CPacket
        ) {
            FSMClient.sendFakePacket(packet);
        }
    }
}
