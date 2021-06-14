package ru.gb.java2.kochemasov.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.gb.java2.kochemasov.ClientChat;
import ru.gb.java2.kochemasov.Network;

import java.io.IOException;
import java.util.function.Consumer;

public class AuthController {
    public static final String AUTH_COMMAND = "/auth";
    public static final String AUTH_OK_COMMAND = "/authOk";
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button authButton;

    private ClientChat clientChat;

    @FXML
    public void executeAuthAction(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login == null || login.isBlank() ||
                password == null || password.isBlank()) {
            clientChat.showErrorDialog("Аутентификация",
                    "Некорректные данные!",
                    "Логин и пароль должны быть заполнены!");
            return;
        }

        String authCommandMessage = String.format(
                "%s %s %s", AUTH_COMMAND, login, password);
        try {
            Network.getInstance().sendMessage(authCommandMessage);
        } catch (IOException e) {
            clientChat.showNetworkDialog("Ошибка передачи данных по сети",
                    "Не удалось отправить сообщение!");
            e.printStackTrace();
        }
    }

    public void setClientChat(ClientChat clientChat) {
        this.clientChat = clientChat;
    }

    public void initMessageHandler() {
        Network.getInstance().waitMessages(new Consumer<String>() {
            @Override
            public void accept(String message) {
                if (message.startsWith(AUTH_OK_COMMAND)) {
                    String[] parts = message.split(" ");
                    String username = parts[1];
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> {
                                clientChat.getChatStage().setTitle(username);
                                clientChat.getAuthStage().close();
                            }
                    );
                } else {
                    Platform.runLater(() -> {
                        clientChat.showErrorDialog("Аутентификация",
                                "Некорректные данные!",
                                "Пользователя с такими логином/паролем не существует!");
                    });
                }
            }
        });
    }
}
