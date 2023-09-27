package net.just_s.socket;

import net.just_s.FSM;
import net.minecraft.network.PacketByteBuf;
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

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        FSM.LOGGER.info("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        FSM.LOGGER.info("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //FSM.LOGGER.info("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        //FSM.LOGGER.info("received ByteBuffer from "	+ conn.getRemoteSocketAddress());
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
