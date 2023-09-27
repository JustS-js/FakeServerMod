package net.just_s.socket;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.just_s.FSM;
import net.just_s.FSMClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;

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
        //FSM.LOGGER.info("received message: " + message);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        try {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBytes(message.array());
            FSMClient.PacketId id = FSMClient.PacketId.values()[buf.readVarInt()];
            //FSM.LOGGER.info("buf | " + buf);
            switch (id) {
                case GAMEMSG -> onChatPacket(buf);
                case HEADERLIST -> onHeaderPacket(buf);
//                case 2 -> packet = new PlayerListHeaderS2CPacket(buf);
//                case 3 -> packet = new PlayerListC2CPacket(buf);
                default -> {
                    return;
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onError(Exception ex) {
        FSM.LOGGER.error("an error occurred:" + ex);
    }

    private void onChatPacket(PacketByteBuf buf) {
        String json = buf.readString();
        boolean overlay = buf.readBoolean();
        Text message = Text.Serializer.fromJson(json);

        FSMClient.MC.getMessageHandler().onGameMessage(message, overlay);
    }

    private void onHeaderPacket(PacketByteBuf buf) {
        boolean isHeaderEmpty = buf.readBoolean();
        boolean isFooterEmpty = buf.readBoolean();
        Text header = Text.Serializer.fromJson(buf.readString());
        Text footer = Text.Serializer.fromJson(buf.readString());

        FSMClient.MC.inGameHud.getPlayerListHud().setHeader(isHeaderEmpty ? null : header);
        FSMClient.MC.inGameHud.getPlayerListHud().setFooter(isFooterEmpty ? null : footer);

        int k = buf.readInt();
        FSMClient.fakePlayers.clear();
        while (0 < k--) {
            Text name = Text.Serializer.fromJson(buf.readString());
            FSMClient.fakePlayers.add(name);
        }
    }

}
