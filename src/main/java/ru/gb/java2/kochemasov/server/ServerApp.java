package ru.gb.java2.kochemasov.server;

public class ServerApp {

    public static void main(String[] args) {
        int port = ConnectionSettings.SERVER_PORT;

        if(args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        new MyServer().start(port);
    }
}
