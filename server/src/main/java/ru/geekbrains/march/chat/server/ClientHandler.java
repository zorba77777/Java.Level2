package ru.geekbrains.march.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    public Server server;
    private Socket socket;
    public DataInputStream in;
    private DataOutputStream out;
    public String username;
    private ExecutorService executorService;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.executorService = Executors.newSingleThreadExecutor();
        this.executorService.execute(new ClientThreadRunner(this));
    }

    public void executeCommand(String cmd) {
        // /w Bob Hello, Bob!!!
        if (cmd.startsWith("/w ")) {
            String[] tokens = cmd.split("\\s+", 3);
            if (tokens.length != 3) {
                sendMessage("Server: Введена некорректная команда");
                return;
            }
            server.sendPrivateMessage(this, tokens[1], tokens[2]);
            return;
        }

        // /change_nick myNewNickname
        if (cmd.startsWith("/change_nick ")) {
            String[] tokens = cmd.split("\\s+");
            if (tokens.length != 2) {
                sendMessage("Server: Введена некорректная команда");
                return;
            }
            String newNickname = tokens[1];
            if (server.getAuthenticationProvider().isNickBusy(newNickname)) {
                sendMessage("Server: Такой никнейм уже занят");
                return;
            }
            server.getAuthenticationProvider().changeNickname(username, newNickname);
            username = newNickname;
            sendMessage("Server: Вы изменили никнейм на " + newNickname);
            server.broadcastClientsList();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            disconnect();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}