package client;

import client.Users.NormalUser;
import client.Users.User;
import client.Util.ShowAlert;
import client.Util.UserSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
public class ClientMain extends Application {
    private static final int PORT = 12345;
    private StageHandler stageHandler;
    private PrintWriter out;
    private ObjectMapper objectMapper; // JSON parser

    @Override
    public void start(Stage stage) {
        objectMapper = new ObjectMapper(); // Initialize the ObjectMapper
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
            JsonNode response = objectMapper.readTree(message);
            JsonNode dataArray = response.get("data");
            if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
                ShowAlert.error("Invalid server response: missing data array.");
                return;
            }

            JsonNode statusNode = dataArray.get(0).get("status");
            if (statusNode == null) {
                ShowAlert.error("Invalid server response: missing status.");
                return;
            }

            String status = statusNode.asText();
            if ("Error".equalsIgnoreCase(status)) {
                String userFriendlyError = dataArray.get(0).get("userFriendlyError").asText();
                ShowAlert.error(userFriendlyError);
                return;
            }

            if ("Success".equalsIgnoreCase(status)) {
                String type = response.get("type").asText();
                System.out.println("type: "+ type);
                switch (type) {
                    case "login":
                        handleLoginSuccess(response);
                        break;
                    default:
                        ShowAlert.info("Action performed successfully.");
                        break;
                }
            } else {
                ShowAlert.error("Unexpected status: " + status);
            }
        } catch (Exception e) {
            ShowAlert.error("Invalid server response: " + message);
            e.printStackTrace();
        }
    }

    private void handleLoginSuccess(JsonNode response) {
        try {
            JsonNode userData = response.get("data").get(1);
            if (userData == null) {
                ShowAlert.error("Error: Missing user data in response.");
                return;
            }

            String username = userData.get("username").asText();
            String role = userData.get("role").asText();

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



    public static void main(String[] args) {
        launch(args);
    }
}
