package net.just_s.socket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public class Server extends WebSocketServer {

    public Server(InetSocketAddress address) {
        super(address);
    }

    public void sendPacket(PacketByteBuf buf) {
        this.broadcast(buf.array());
    }

    public void sendPacket(Packet<?> packet) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(packetToId(packet));
        packet.write(buf);
        sendPacket(buf);
    }

    private int packetToId(Packet<?> packet) {
        if (packet instanceof ChatMessageS2CPacket) return 0;
        if (packet instanceof GameMessageS2CPacket) return 1;
        if (packet instanceof PlayerListHeaderS2CPacket) return 2;
        if (packet instanceof PlayerListS2CPacket) return 3;
        return -1;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        FSM.LOGGER.info("new connection to " + conn.getRemoteSocketAddress());
        sendPacket(new PlayerListS2CPacket(
                PlayerListS2CPacket.Action.UPDATE_LISTED,
                (ServerPlayerEntity) FSMClient.MC.player.networkHandler.getListedPlayerListEntries()
        ));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        FSM.LOGGER.info("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        FSM.LOGGER.info("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        FSM.LOGGER.info("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + ex);
    }

    @Override
    public void onStart() {
        FSM.LOGGER.info("server started successfully");
    }
}
