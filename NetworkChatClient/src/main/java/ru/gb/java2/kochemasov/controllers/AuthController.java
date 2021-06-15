package ru.gb.java2.kochemasov.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.gb.java2.kochemasov.ClientChat;
import ru.gb.java2.kochemasov.Network;

import java.io.IOException;
import java.util.function.Consumer;

public class AuthController {
    public static final String AUTH_COMMAND = "/auth";
    public static final String AUTH_OK_COMMAND = "/authOk";
    public static final String AUTH_ERR = "/authErr";
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button authButton;

    private ClientChat clientChat;

    private static String username;

    public static String getUsername() {
        return username;
    }

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
                    username = parts[1];
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> {
                                clientChat.getChatStage().setTitle(username);
                                clientChat.getAuthStage().close();
                            }
                    );
                } else if (message.startsWith(AUTH_ERR)){
                    Platform.runLater(() -> {
                        clientChat.showErrorDialog("Аутентификация",
                                "Пользователь уже залогинен",
                                "Открыта активная сессия данного пользователя!");
                    });
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

    public void onAuthPressEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            executeAuthAction(null);
        }
    }
}
