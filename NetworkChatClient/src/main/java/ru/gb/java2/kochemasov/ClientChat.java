package ru.gb.java2.kochemasov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.gb.java2.kochemasov.controllers.AuthController;
import ru.gb.java2.kochemasov.controllers.ViewController;

import java.util.Objects;

public class ClientChat extends Application {

    public static final String NETWORK_ERROR_TITLE = "Connection error";
    public static final String NETWORK_ERROR_CONNECTION_TYPE = "Connection error";
    private Stage primaryStage;
    private Stage authStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        ViewController chatController = createChatDialog(primaryStage);
        chatController.userList.getItems().addAll(
                "username1",
                "username2",
                "username3");

        this.primaryStage.show();

        connectToServer(chatController);

        createAuthDialog(primaryStage);
        chatController.initMessageHandler();
    }

    private void createAuthDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(Objects.requireNonNull(getClass().getResource("authDialog.fxml")));
        AnchorPane authDIalogPanel = authLoader.load();

        authStage = new Stage();
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setScene(new Scene(authDIalogPanel));

        AuthController authController = authLoader.getController();
        authController.setClientChat(this);
        authController.initMessageHandler();
        authStage.showAndWait();
    }

    private ViewController createChatDialog(Stage primaryStage) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getResource("chat.fxml")));
        Parent root = loader.load();

        primaryStage.setTitle("MyChat");
        primaryStage.setScene(new Scene(root));

        ViewController viewController = loader.getController();
        return viewController;
    }

    private void connectToServer(ViewController controller) {
        boolean result = Network.getInstance().connect();
        if (!result) {
            String msgErr = "Unable to connect with server!";
            showNetworkDialog(NETWORK_ERROR_CONNECTION_TYPE, msgErr);
            return;
        }
        controller.setApplication(this);

        primaryStage.setOnCloseRequest(event -> Network.getInstance().close());
    }

    public void showErrorDialog(String title, String type, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(details);
        alert.showAndWait();
    }

    public void showNetworkDialog(String type, String details) {
        showErrorDialog(NETWORK_ERROR_TITLE, type, details);
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getChatStage() {
        return primaryStage;
    }
}