package ru.gb.java2.kochemasov.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    public static final String AUTH_OK_COMMAND = "/authOk";
    public static final String AUTH_COMMAND = "/auth";
    public static final String SEND_COMMAND = "/w";
    public static final String AUTH_ERR = "/authErr";

    private final MyServer server;
    private final Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String username;

    public ClientHandler(MyServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        new Thread(() -> {
            try {
                authentication();
                readMessages();
            } catch (IOException e) {
                System.err.println("Failed to process message from client");
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
                }
            }
        }).start();
    }

    private void authentication() throws IOException {
        while (true) {
            String message = inputStream.readUTF();
            if (message.startsWith(AUTH_COMMAND)) {
                String[] parts = message.split(" ");
                String login = parts[1];
                String password = parts[2];

                username = server.getAuthService().getUserNameByLoginAndPassword(login, password);
                if (username == null) {
                    sendMessage("Некорректные логин/пароль");
                } else if(!server.getClientsUsernames().contains(username)){
                    sendMessage(String.format("%s %s", AUTH_OK_COMMAND, username));
                    server.subscribe(this, username);
                    return;
                } else {
                    sendMessage(String.format("%s %s", AUTH_ERR, username));
                }
            }
        }
    }

    private void closeConnection() throws IOException {
        server.unSubscribe(this, username);
        clientSocket.close();
    }

    private void readMessages() throws IOException {
        while (true) {
            String message = inputStream.readUTF().trim();
            System.out.println("message: " + message);
            if (message.startsWith("/end")) {
                return;
            } else {
                processMessage(message);
            }
        }
    }

    private void processMessage(String message) throws IOException {
        server.broadcastMessage(message, this);
    }

    public void sendMessage(String message) throws IOException {

        outputStream.writeUTF(message);
    }
}
