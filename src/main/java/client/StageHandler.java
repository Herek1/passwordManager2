package client;

import client.Users.User;
import client.Util.ShowAlert;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StageHandler {
    private final Stage stage;
    private final ClientHandler clientHandler;
    private final TextArea messagesArea;

    public StageHandler(Stage stage, ClientHandler clientHandler) {
        this.stage = stage;
        this.clientHandler = clientHandler;
        this.messagesArea = new TextArea();
        this.messagesArea.setEditable(false);
        this.messagesArea.setPrefHeight(200);
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public TextArea getMessagesArea() {
        return messagesArea;
    }

    public void setDefaultView() {
        VBox defaultLayout = generateDefaultLayout();
        stage.setScene(new Scene(defaultLayout, 400, 300));
        stage.setTitle("Password manager");
        stage.show();
    }

    public void switchToRoleView(User user) {
        if (user == null) {
            displayMessage("Error: User is null.");
            return;
        }
        VBox layout = user.generateLayout();
        stage.setScene(new Scene(layout, 600, 400)); // Wider scene for better layout
    }

    public void setScene(VBox root, String title) {
        Platform.runLater(() -> {
            root.setPadding(UiCreator.ROOT_PADDING);
            root.setSpacing(UiCreator.VBOX_SPACING);
            Scene scene = new Scene(root, UiCreator.WINDOW_WIDTH, UiCreator.WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        });
    }

    private VBox generateDefaultLayout() {
        displayMessage("");
        Label titleLabel = new Label("Login to password manager");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button ("Register");
        loginButton.setOnAction(event -> {
            if (!loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                sendLoginData("login",loginField, passwordField);
            } else {
                ShowAlert.error("Please enter correct data");
            }
        });

        registerButton.setOnAction(event -> {
            if (isNumeric(loginField.getText()) && !loginField.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                sendLoginData("register",loginField, passwordField);
            } else {
                ShowAlert.error("Please enter correct data");
            }
        });


        VBox layout = new VBox(10, titleLabel, loginField, passwordField, loginButton, registerButton, messagesArea);
        layout.setPadding(new Insets(15));
        layout.setSpacing(10);

        return layout;
    }

    private void sendLoginData(String action, TextField loginField, TextField passwordField) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonResponseNode = objectMapper.createObjectNode();
        jsonResponseNode.put("type", action);
        jsonResponseNode.put("login", loginField.getText());
        jsonResponseNode.put("password", passwordField.getText());

        String loginData = jsonResponseNode.toString();
        if (!loginData.isBlank()) {
            clientHandler.sendMessage(loginData);
        } else {
            displayMessage("Please enter login credentials.");
        }
    }

    public void displayMessage(String message) {
        Platform.runLater(() -> {
            messagesArea.clear();
            messagesArea.appendText(message + "\n");
            messagesArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}