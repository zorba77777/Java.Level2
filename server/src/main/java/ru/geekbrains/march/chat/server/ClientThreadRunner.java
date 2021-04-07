package ru.geekbrains.march.chat.server;

import java.io.IOException;

class ClientThreadRunner implements Runnable {
    final ClientHandler clientHandler;

    ClientThreadRunner(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public void run() {
        try {
            while (true) { // Цикл авторизации
                String msg = clientHandler.in.readUTF();
                if (msg.startsWith("/login ")) {
                    // /login Bob 100xyz
                    String[] tokens = msg.split("\\s+");
                    if (tokens.length != 3) {
                        clientHandler.sendMessage("/login_failed Введите имя пользователя и пароль");
                        continue;
                    }
                    String login = tokens[1];
                    String password = tokens[2];

                    String userNickname = this.clientHandler.server.getAuthenticationProvider().getNicknameByLoginAndPassword(login, password);
                    if (userNickname == null) {
                        this.clientHandler.sendMessage("/login_failed Введен некорретный логин/пароль");
                        continue;
                    }
                    if (this.clientHandler.server.isUserOnline(userNickname)) {
                        this.clientHandler.sendMessage("/login_failed Учетная запись уже используется");
                        continue;
                    }
                    this.clientHandler.username = userNickname;
                    this.clientHandler.sendMessage("/login_ok " + this.clientHandler.username);
                    this.clientHandler.server.subscribe(this.clientHandler);
                    break;
                }
            }

            while (true) { // Цикл общения с клиентом
                String msg = this.clientHandler.in.readUTF();
                if (msg.startsWith("/")) {
                    this.clientHandler.executeCommand(msg);
                    continue;
                }
                this.clientHandler.server.broadcastMessage(this.clientHandler.username + ": " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.clientHandler.disconnect();
        }
    }
}
