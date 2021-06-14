package ru.gb.java2.kochemasov.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ru.gb.java2.kochemasov.ClientChat;
import ru.gb.java2.kochemasov.Network;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ViewController {
    @FXML
    public ListView<String> userList;
    @FXML
    private TextField textInp;
    @FXML
    private Button btnSend;
    @FXML
    private TextArea chatArea;

    private ClientChat application;

    @FXML
    private void sendAction(ActionEvent actionEvent) {
        sendMessage();
    }

    @FXML
    private void onKeyPressedInInputInTextField(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            chatArea.appendText(System.lineSeparator());
            sendMessage();
        }
    }

    private void sendMessage() {
        String message = textInp.getText().trim();
        if (message.isEmpty()) {
            textInp.clear();
            return;
        }

        String sender = null;
        if (!userList.getSelectionModel().isEmpty()) {
            sender = userList.getSelectionModel().getSelectedItem();
        }

        try {
            message = sender != null ? String.format("%s %s", sender, message) : message;
            Network.getInstance().sendMessage(message);
        } catch (IOException e) {
            application.showNetworkDialog("Ошибка передачи данных по сети", "Не удалось отправить сообщение");
        }
        appendMessageToChat("Me", message);
    }

    private void appendMessageToChat(String sender, String message) {
        chatArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
        chatArea.appendText(System.lineSeparator());
        if (sender != null) {
            chatArea.appendText(sender + ":");
            chatArea.appendText(System.lineSeparator());
        }
        chatArea.appendText(message);
        chatArea.appendText(System.lineSeparator());
        chatArea.appendText(System.lineSeparator());
        textInp.clear();
    }

    public void setApplication(ClientChat application) {
        this.application = application;
    }

    public void initMessageHandler() {
        Network.getInstance().waitMessages(message -> Platform.runLater(() -> {
            ViewController.this.appendMessageToChat("Server", message);
        }));
    }
}