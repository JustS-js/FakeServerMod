package net.just_s.socket;

import net.just_s.FSMClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.List;

public class PlayerListC2CPacket implements Packet<ClientPlayPacketListener> {
    private final List<Text> nicknames;

    public PlayerListC2CPacket(List<Text> nicknames) {
        this.nicknames = nicknames;
    }

    public PlayerListC2CPacket(PacketByteBuf buf) {
        this.nicknames = buf.readList(PacketByteBuf::readText);
    }

    @Override
    public void write(PacketByteBuf buf) {
        for (Text name : nicknames) {
            buf.writeText(name);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener listener) {
        FSMClient.fakePlayers.clear();
        FSMClient.fakePlayers = nicknames;
    }
}
