package net.just_s.socket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.just_s.mixin.client.ClientConnectionAccessor;
import net.just_s.mixin.client.ClientPlayNetworkHandlerMixin;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.EnumSet;

public class Client extends WebSocketClient {

    public Client(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        FSM.LOGGER.info("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        FSM.LOGGER.info("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        FSM.LOGGER.info("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        try {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBytes(message.array());
            int id = buf.readVarInt();
            FSM.LOGGER.info("received ByteBuffer id | " + id);
            //FSM.LOGGER.info("buf | " + buf);
//            switch (id) {
//                case 0 -> packet = new ChatMessageS2CPacket(buf);
//                case 1 -> packet = new GameMessageS2CPacket(buf);
//                case 2 -> packet = new PlayerListHeaderS2CPacket(buf);
//                case 3 -> packet = new PlayerListC2CPacket(buf);
//                default -> {
//                    return;
//                }
//            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onError(Exception ex) {
        FSM.LOGGER.error("an error occurred:" + ex);
    }

}
