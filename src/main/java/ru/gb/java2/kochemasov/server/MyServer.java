package ru.gb.java2.kochemasov.server;

import ru.gb.java2.kochemasov.server.auth.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final List<ClientHandler> clients = new ArrayList<>();

    public AuthService getAuthService() {
        return authService;
    }

    private AuthService authService;

    public void start(int port) {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server has been started");
            authService = new AuthService();
            while (true) {
                waitAndProcessNewClientConnection(serverSocket);
            }
        } catch (IOException e) {
            System.err.println("Failed to bind port " + port);
        }
    }

    private void waitAndProcessNewClientConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Waiting for client connection...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client just connected.");
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public void broadcastMessage(String message, ClientHandler sender) throws IOException {

        for (ClientHandler client : clients) {

            if (client != sender) {
//                String[] rawMessage = message.split(" ");
//                String destinator = rawMessage[1];
//                if(destinator == clients.get().)
                client.sendMessage(message);
            }
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }
    public void unSubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }
}