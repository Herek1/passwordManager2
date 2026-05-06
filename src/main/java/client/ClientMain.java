package client;

import client.Users.NormalUser;
import client.Users.User;
import client.Util.Encryption;
import client.Util.JsonExtract;
import client.Util.ShowAlert;
import client.Util.UserSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
public class ClientMain extends Application {
    private static final int PORT = 12345;
    private StageHandler stageHandler;
    private PrintWriter out;

    @Override
    public void start(Stage stage) {
        connectToServer(stage);
    }

    private void connectToServer(Stage stage) {
        try {
            Socket socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            ClientHandler clientHandler = new ClientHandler(out);
            this.stageHandler = new StageHandler(stage, clientHandler);
            Platform.runLater(stageHandler::setDefaultView);

            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String message;
                    while ((message = in.readLine()) != null) {
                        handleServerResponse(message);
                    }
                } catch (IOException e) {
                    ShowAlert.error("Connection lost");
                }
            }).start();

        } catch (IOException e) {
            ShowAlert.error("Unable to connect to the server.");
        }
    }

    private void handleServerResponse(String message) {
        System.out.println("Received: " + message);
        try {
            String status = JsonExtract.extract(message, "data", "0", "status");
            if ("Error".equalsIgnoreCase(status)) {
                String err = JsonExtract.extract(message, "data", "0", "userFriendlyError");
                ShowAlert.error(err);
                return;
            }
            String type = JsonExtract.extract(message, "type");
            switch (type) {
                case "login":
                    handleLoginSuccess(message);
                    break;
                case "getPasswords":
                    handleGetPasswords(message);
                    break;
                case "deletePassword":
                    UserSession.getCurrentUser().openCheckPasswordView();
                    break;
                default:
                    ShowAlert.info("Success");
            }
        } catch (Exception e) {
            ShowAlert.error("Invalid server response: " + message);
            e.printStackTrace();
        }
    }

    private void handleLoginSuccess(String response) {
        try {
            String username = JsonExtract.extract(response, "data", "1", "username");
            String role = JsonExtract.extract(response, "data", "1", "role");

            final User user;
            switch (role.toLowerCase()) {
                case "user":
                    user = new NormalUser(username, UserSession.clearPendingPassword(), role, stageHandler.getClientHandler(), stageHandler);
                    break;
                default:
                    Platform.runLater(() -> stageHandler.displayMessage("Error: Unsupported role."));
                    return;
            }

            UserSession.setCurrentUser(user);
            Platform.runLater(() -> stageHandler.switchToRoleView(user));

        } catch (Exception e) {
            ShowAlert.error("Error processing login response.");
            e.printStackTrace();
        }
    }

    private void handleGetPasswords(String response) throws Exception {

        int size = JsonExtract.getArraySize(response, "data");

        if (size <= 1) {
            stageHandler.displayMessage("No passwords found.");
            return;
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        for (int i = 1; i < size; i++) {

            String domain = JsonExtract.extract(response, "data", String.valueOf(i), "domain");
            String login  = JsonExtract.extract(response, "data", String.valueOf(i), "login");
            String encPwd = JsonExtract.extract(response, "data", String.valueOf(i), "password");

            String password = Encryption.decryptPassword(
                    UserSession.getCurrentUser().getMaster_password(),
                    encPwd
            );

            Label domainLabel = new Label("Domain: " + domain);
            Label loginLabel  = new Label("Login: " + login);
            Label passLabel   = new Label("Password: " + password);

            Button deleteBtn = new Button("Delete");
            deleteBtn.setOnAction(e -> {
                ObjectNode req = new ObjectMapper().createObjectNode();
                req.put("type", "deletePassword");
                req.put("username", UserSession.getCurrentUser().getUsername());
                req.put("domain", domain);
                req.put("login", login);

                stageHandler.getClientHandler().sendMessage(req.toString());
            });

            VBox entry = new VBox(5, domainLabel, loginLabel, passLabel, deleteBtn);
            entry.setStyle("-fx-border-color: gray; -fx-padding: 8;");

            layout.getChildren().add(entry);
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e ->
                stageHandler.setScene(
                        UserSession.getCurrentUser().generateLayout(),
                        "Password manager"
                )
        );

        layout.getChildren().add(backBtn);
        stageHandler.setScene(layout, "Your passwords");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
