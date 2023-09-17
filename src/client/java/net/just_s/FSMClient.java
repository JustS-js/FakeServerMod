package net.just_s;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.command.CommandChangeState;
import net.just_s.command.CommandStart;
import net.just_s.command.CommandStop;
import net.just_s.socket.Client;
import net.just_s.socket.Server;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

public class FSMClient implements ClientModInitializer {
	public static final MinecraftClient MC = MinecraftClient.getInstance();

	public static boolean isReceiver;
	public static boolean isRunning;

	private static Thread modThread;

	private static Server server;
	private static Client client;

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(CommandStop::register);
		ClientCommandRegistrationCallback.EVENT.register(CommandStart::register);
		ClientCommandRegistrationCallback.EVENT.register(CommandChangeState::register);
	}

	public static void startFaking() {
		modThread = new Thread(() -> {
			if (isReceiver) {
				client = new Client();
			} else {
				server = new Server();
			}
		}, "fsm");
		modThread.start();
	}

	public static void stopFaking() {
		boolean hasStopped = (isReceiver) ? client.stop() : server.stop();
		if (hasStopped) {
			try {
				modThread.join();
			} catch (InterruptedException ignored) {}
		}
	}

	public static <T extends PacketListener> void sendFakePacket(Packet<T> packet) {
		if (server == null) return;
		PacketByteBuf buf = PacketByteBufs.create();
		packet.write(buf);
		server.sendPacket(buf);
	}
}