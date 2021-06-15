package ru.gb.java2.kochemasov.server;

import ru.gb.java2.kochemasov.server.auth.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MyServer {

    private final List<ClientHandler> clients = new ArrayList<>();
    private final List<String> clientsUsernames = new ArrayList<>();

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
                client.sendMessage(message);
            }
        }
    }

    public void subscribe(ClientHandler clientHandler, String username){
        clients.add(clientHandler);
        clientsUsernames.add(username);
    }
    public void unSubscribe(ClientHandler clientHandler, String username){
        clients.remove(clientHandler);
        clientsUsernames.remove(username);
    }

    public List<String> getClientsUsernames() {
        return clientsUsernames;
    }
}