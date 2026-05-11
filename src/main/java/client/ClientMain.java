package client;

import client.Users.AdminUser;
import client.Users.NormalUser;
import client.Users.User;
import client.Util.JsonExtract;
import client.Views.ShowAlert;
import client.Util.UserSession;
import client.Views.StageHandler;
import client.Views.TimeOutHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
public class ClientMain extends Application {
    private static final int PORT = 12345;
    private StageHandler stageHandler;
    private TimeOutHandler timeOutHandler;
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

            this.timeOutHandler = new TimeOutHandler(stageHandler);

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
                //stageHandler.displayMessage(err);
                ShowAlert.error(err);
                return;
            }
            String type = JsonExtract.extract(message, "type");
            switch (type) {
                case "login" -> handleLoginSuccess(message);
                case "timeout" -> timeOutHandler.handleTimeout(message);
                default -> {
                    User currentUser = UserSession.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.handleMessage(message);
                    } else {
                        ShowAlert.info("Success");
                    }
                }
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
                case "admin":
                    user = new AdminUser(username, UserSession.clearPendingPassword(), role, stageHandler.getClientHandler(), stageHandler);
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
