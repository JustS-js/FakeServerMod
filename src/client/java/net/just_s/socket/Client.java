package net.just_s.socket;

import io.netty.buffer.ByteBuf;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.just_s.mixin.client.ClientConnectionAccessor;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class Client {
    private static final String SOCKET_URL = "ws://127.0.0.1:80";
    private final WebSocket clientSocket;

    public Client() {
        URI uri = URI.create(SOCKET_URL);
        FSM.LOGGER.info("Created URI");
        clientSocket = HttpClient
                .newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(SOCKET_URL), new WebSocketClient())
                .join();
        FSM.LOGGER.info("created client socket!");
    }

    public boolean stop() {
        clientSocket.abort();
        return true;
    }

    private static class WebSocketClient implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            FSM.LOGGER.info("Client Connected to local server of FSM");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            FSM.LOGGER.info("FSM Client >>> " + data);

            try {
                PacketByteBuf buf = null;
                Packet<?> packet;
                switch (buf.readVarInt()) {
                    case 99 -> packet = new GameMessageS2CPacket(buf);
                    case 100 -> packet = new PlayerListHeaderS2CPacket(buf);
                    case 57 -> packet = new PlayerListS2CPacket(buf);
                    default -> {
                        return WebSocket.Listener.super.onText(webSocket, data, last);
                    }
                }
                ClientConnectionAccessor con = (ClientConnectionAccessor)FSMClient.MC.getNetworkHandler().getConnection();
                ClientConnectionAccessor.invokeHandlePacket(packet, con.getPacketListener());
            } catch (Exception e) {

            }
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }
    }
}
