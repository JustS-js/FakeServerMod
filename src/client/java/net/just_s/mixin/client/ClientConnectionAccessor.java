package net.just_s.mixin.client;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientConnection.class)
public interface ClientConnectionAccessor {
	@Invoker("handlePacket")
	public static <T extends PacketListener> void invokeHandlePacket(Packet<T> packet, PacketListener listener) {}

	@Accessor
	PacketListener getPacketListener();
}