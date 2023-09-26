package net.just_s;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.command.CommandChangeState;
import net.just_s.command.CommandStart;
import net.just_s.command.CommandStop;
import net.just_s.socket.Client;
import net.just_s.socket.Server;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class FSMClient implements ClientModInitializer {
	public static final MinecraftClient MC = MinecraftClient.getInstance();

	private static final String host = "localhost";
	private static final int port = 8887;

	public static boolean isReceiver;
	public static boolean isRunning;

	private static Thread modThread;

	private static Server server;
	private static Client client;

	public static List<Text> fakePlayers = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register(CommandStop::register);
		ClientCommandRegistrationCallback.EVENT.register(CommandStart::register);
		ClientCommandRegistrationCallback.EVENT.register(CommandChangeState::register);
	}

	public static void startFaking() {
		modThread = new Thread(() -> {
			if (isReceiver) {

				try {
					client = new Client(new URI("ws://" + host + ":" + port));
					client.connect();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				server = new Server(new InetSocketAddress(host, port));
				server.run();
			}
		}, "fsm");
		modThread.start();
	}

	public static void stopFaking() {
		try {
			if (isReceiver) {
				client.close();
			} else {
				server.stop();
			}
			modThread.join();
		} catch (InterruptedException ignored) {}
	}

	public static void sendPacket(PacketByteBuf buf) {
		if (server == null) return;
		server.sendPacket(buf);
	}

	public enum PacketId {
		GAMEMSG, HEADERLIST, PLAYERLIST
	}
}