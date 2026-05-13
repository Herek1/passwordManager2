package client.Users;

import client.ClientHandler;
import client.Util.JsonExtract;
import client.Util.UserSession;
import client.Views.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class AdminUser extends User{
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public AdminUser(String username, String password, String role, ClientHandler clientHandler, StageHandler stageHandler) {
        super(username, password, role);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    public VBox generateLayout() {
        Button addBtn = UiCreator.createButton("Add user");
        Button checkBtn = UiCreator.createButton("Manage users data");
        Button logsBtn = UiCreator.createButton("Log overview");
        Button logoutBtn = UiCreator.createButton("Log out");

        addBtn.setOnAction(e -> openAddUserView());
        checkBtn.setOnAction(e -> openManageUsersView());
        logsBtn.setOnAction(e -> openShowLogsView());
        logoutBtn.setOnAction(e -> stageHandler.setDefaultView());

        VBox root = new VBox(15, addBtn, checkBtn, logoutBtn);
        root.setPadding(new Insets(15));

        return root;
    }

    @Override
    public void handleMessage(String message) throws Exception {
        String type = JsonExtract.extract(message, "type");
        switch (type) {
            case "getUsers" -> handleGetUsers(message);
            default -> ShowAlert.info("Success");
        }
    }

    private void openAddUserView() {

        LabeledField loginField = UiCreator.createText("Username");
        LabeledSelect roleSelect = UiCreator.createSelect("Role", "user", "admin");
        Button createBtn = UiCreator.createButton("Create user");
        Button backBtn = UiCreator.createButton("Back");
        backBtn.setOnAction(e -> stageHandler.setScene(generateLayout(), "Admin panel"));

        createBtn.setOnAction(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode request = mapper.createObjectNode();

                request.put("type", "createUser");
                request.put("adminUsername", getUsername());
                request.put("username", loginField.getValue());
                request.put("role", roleSelect.getValue());

                clientHandler.sendMessage(request.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
                ShowAlert.error("Failed to create user");
            }
        });

        VBox root = new VBox(15, loginField.getRoot(), roleSelect.getRoot(), createBtn, backBtn);

        root.setPadding(new Insets(15));

        stageHandler.setScene(root, "Create user");
    }

    private void openManageUsersView() {

        LabeledField usernameField = UiCreator.createText("Username");
        LabeledSelect roleSelect = UiCreator.createSelect("Role", "", "user", "admin");

        TextArea resultArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");
        resultArea.setEditable(false);

        Button searchBtn = UiCreator.createButton("Search");
        Button viewAllBtn = UiCreator.createButton("View all");
        Button backBtn = UiCreator.createButton("Back");

        searchBtn.setOnAction(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode request = mapper.createObjectNode();

                request.put("type", "getUsers");
                request.put("username", usernameField.getValue());
                request.put("role", roleSelect.getValue());

                clientHandler.sendMessage(request.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        viewAllBtn.setOnAction(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode request = mapper.createObjectNode();

                request.put("type", "getUsers");
                request.put("username", "");
                request.put("role", "");

                clientHandler.sendMessage(request.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> stageHandler.setScene(generateLayout(), "Admin panel"));

        VBox root = new VBox(15, usernameField.getRoot(), roleSelect.getRoot(), searchBtn, viewAllBtn, backBtn, resultArea);

        root.setPadding(new Insets(15));

        stageHandler.setScene(root, "Manage users");
    }

    private void handleGetUsers(String response) {

        int size = JsonExtract.getArraySize(response, "data");

        if (size <= 1) {
            stageHandler.displayMessage("No users found.");
            return;
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        for (int i = 1; i < size; i++) {

            String id = JsonExtract.extract(response, "data", String.valueOf(i), "id");
            String username = JsonExtract.extract(response, "data", String.valueOf(i), "username");
            String role = JsonExtract.extract(response, "data", String.valueOf(i), "role");

            Label idLabel = new Label("ID: " + id);
            Label usernameLabel = new Label("Username: " + username);
            Label roleLabel = new Label("Role: " + role);

            Button viewPasswordsBtn = UiCreator.createButton("View passwords");
            Button deleteBtn = UiCreator.createButton("Delete");

            viewPasswordsBtn.setOnAction(e -> {
                // TODO:
                System.out.println("Viewing passwords for: " + username);
            });
            deleteBtn.setOnAction(e -> {
                try {
                    ObjectNode req = new ObjectMapper().createObjectNode();
                    req.put("type", "deleteUser");
                    req.put("username", username);
                    stageHandler.getClientHandler().sendMessage(req.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    ShowAlert.error("Failed to delete user");
                }
            });
            VBox buttons = new VBox(5, viewPasswordsBtn, deleteBtn);
            VBox entry = new VBox(5, idLabel, usernameLabel, roleLabel, buttons);

            entry.setStyle("-fx-border-color: gray; -fx-padding: 8;");
            layout.getChildren().add(entry);
        }

        Button backBtn = UiCreator.createButton("Back");

        backBtn.setOnAction(e -> stageHandler.setScene(UserSession.getCurrentUser().generateLayout(), "Admin panel"));
        layout.getChildren().add(backBtn);

        stageHandler.setScene(layout, "Users");
    }
    private void openShowLogsView(){}
}
