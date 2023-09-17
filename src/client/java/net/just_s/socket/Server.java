package net.just_s.socket;

import net.just_s.FSM;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
    private ServerSocket serverSocket;
    private Socket client;

    public Server() {
        try {
            FSM.LOGGER.info("Server()");
            serverSocket = new ServerSocket(80);
            FSM.LOGGER.info("Server created...");
            client = serverSocket.accept();
            FSM.LOGGER.info("Client connected!");

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner s = new Scanner(in, StandardCharsets.UTF_8);

            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);

            FSM.LOGGER.info("got GET input");

            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8)))
                        + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
                out.write(response, 0, response.length);
            }
            FSM.LOGGER.info("changed to wss type");
        } catch (IOException | NoSuchAlgorithmException e) {
            FSM.LOGGER.error("Server() | " + e.getMessage());
        }
    }

    public boolean stop() {
        try {
            client.close();
            serverSocket.close();
            return true;
        } catch (IOException e) {
            FSM.LOGGER.error("stop | " + e.getMessage());
        }
        return false;
    }

    public boolean sendPacket(PacketByteBuf buf) {
        if (serverSocket == null) return false;
        if (client == null) return false;
        try {
            OutputStream out =  client.getOutputStream();
            out.write(buf.array(), 0, buf.capacity());
            return true;
        } catch (IOException e) {
            FSM.LOGGER.error("sendPacket | " + e);
        }
        return false;
    }
}
