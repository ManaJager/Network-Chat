package ru.gb.java2.kochemasov;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ru.gb.java2.kochemasov.controllers.AuthController;
import ru.gb.java2.kochemasov.controllers.ChatController;
import ru.gb.java2.kochemasov.model.Network;

import java.io.IOException;
import java.util.Objects;

public class ClientChat extends Application {
    public static ClientChat INSTANCE;

    public static final String AUTH_DIALOG_FXML = "authDialog.fxml";
    public static final String CHAT_FXML = "chat.fxml";

    public static final String NETWORK_ERROR_TITLE = "Connection error";
    public static final String NETWORK_ERROR_CONNECTION_TYPE = "Connection error";
    private Stage primaryStage;
    private Stage authStage;
    private FXMLLoader chatWindowLoader;
    private FXMLLoader authLoader;

    @Override
    public void init() throws Exception {
        INSTANCE = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initViews();
        getChatStage().show();
        getAuthStage().show();
        getAuthController().initMessageHandler();
    }

    public ChatController getChatController() {
        return chatWindowLoader.getController();
    }

    private AuthController getAuthController() {
        return authLoader.getController();
    }

    private void initViews() throws IOException {
        initChatWindow();
        initAuthDialog();
    }

    private void initChatWindow() throws IOException {
        chatWindowLoader = new FXMLLoader();
        chatWindowLoader.setLocation(ClientChat.class.getResource(CHAT_FXML));

        Parent root = chatWindowLoader.load();
        this.primaryStage.setScene(new Scene(root));

        setStageForSecondScreen(primaryStage);
    }

    private void initAuthDialog() throws java.io.IOException {
        authLoader = new FXMLLoader();
        authLoader.setLocation(Objects.requireNonNull(getClass().getResource(AUTH_DIALOG_FXML)));
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

    private Screen getSecondScreen() {
        for (Screen screen : Screen.getScreens()) {
            if (!screen.equals(Screen.getPrimary())) {
                return screen;
            }
        }
        return Screen.getPrimary();
    }

    private void setStageForSecondScreen(Stage primaryStage) {
        Screen secondScreen = getSecondScreen();
        Rectangle2D bounds = secondScreen.getBounds();
        primaryStage.setX(bounds.getMinX() + (bounds.getWidth() - 300) / 2);
        primaryStage.setY(bounds.getMinY() + (bounds.getHeight() - 200) / 2);
    }

    private ChatController setStageForSecondaryScreen(Stage primaryStage) throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getResource(CHAT_FXML)));
        Parent root = loader.load();

        primaryStage.setTitle("MyChat");
        primaryStage.setScene(new Scene(root));

        return loader.getController();
    }

    private void connectToServer(ChatController controller) {
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

    public void switchToMainChatWindow(String username) {
        getChatStage().setTitle(username);
        getChatController().initMessageHandler();
        getAuthController().close();
        getAuthStage().close();
    }
}