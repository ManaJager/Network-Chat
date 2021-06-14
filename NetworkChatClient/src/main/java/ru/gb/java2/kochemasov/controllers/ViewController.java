package ru.gb.java2.kochemasov.controllers;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        String destinator;
        if (!userList.getSelectionModel().isEmpty()) {
            destinator = userList.getSelectionModel().getSelectedItem();
        } else destinator = "broadcast";

        try {

            message = String.format("/w %s %s %s", destinator, message, AuthController.getUsername());
            if(!destinator.equals(AuthController.getUsername())) Network.getInstance().sendMessage(message);
        } catch (IOException e) {
            application.showNetworkDialog("Ошибка передачи данных по сети", "Не удалось отправить сообщение");
        }
        if(!destinator.equals(AuthController.getUsername())) appendMessageToChat("Me", message);
    }

    private void appendMessageToChat(String sender, String message) {
        String[] parsed = message.split(" ");
        String from = parsed[3];
        if (sender != null && parsed[0].equals("/w")
                && (parsed[1].equals(AuthController.getUsername()) ||
                parsed[1].equals("broadcast") || sender.equals("Me"))) {
            chatArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
            chatArea.appendText(System.lineSeparator());
            if(!sender.equals("Me")) from = "Me";
            chatArea.appendText(from + ":");
            chatArea.appendText(System.lineSeparator());
            chatArea.appendText(parsed[2]);
            chatArea.appendText(System.lineSeparator());
            chatArea.appendText(System.lineSeparator());
        }
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