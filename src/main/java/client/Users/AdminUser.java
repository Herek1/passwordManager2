package client.Users;

import client.ClientHandler;
import client.Util.JsonExtract;
import client.Util.UserSession;
import client.Views.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AdminUser extends User{
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;
    private String lastPasswordRequest = "";
    private String lastUsersRequest = "";
    private String viewedUser = "";

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

        VBox root = new VBox(15, addBtn, checkBtn, logsBtn, logoutBtn);
        root.setPadding(new Insets(15));

        return root;
    }

    @Override
    public void handleMessage(String message) throws Exception {
        String type = JsonExtract.extract(message, "type");
        switch (type) {
            case "getUsers" -> handleGetUsers(message);
            case "getPasswords" -> handleGetPasswords(message);
            case "deletePassword" -> refreshPasswordsView();
            case "deleteUser" -> refreshUsersView();
            case "getAuditLogs" -> handleGetLogs(message);
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
                lastUsersRequest = request.toString();
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
                lastUsersRequest = request.toString();

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
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode jsonRequestNode = objectMapper.createObjectNode();

                    jsonRequestNode.put("type", "getPassword");
                    jsonRequestNode.put("username", username);
                    jsonRequestNode.put("url", "");
                    lastPasswordRequest = jsonRequestNode.toString();
                    viewedUser = username;

                    clientHandler.sendMessage(jsonRequestNode.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    ShowAlert.error("Failed to fetch passwords");
                }
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
            VBox buttons;
            if(role.equalsIgnoreCase("admin")){
                buttons = new VBox(5, deleteBtn);
            }else{
                buttons = new VBox(5, viewPasswordsBtn, deleteBtn);
            }

            VBox entry = new VBox(5, idLabel, usernameLabel, roleLabel, buttons);
            entry.getStyleClass().add("card");
            layout.getChildren().add(entry);
        }

        Button backBtn = UiCreator.createButton("Back");

        backBtn.setOnAction(e -> stageHandler.setScene(UserSession.getCurrentUser().generateLayout(), "Admin panel"));
        layout.getChildren().add(backBtn);

        stageHandler.setScene(layout, "Users");
    }

    private void handleGetPasswords(String message) {

        int size = JsonExtract.getArraySize(message, "data");

        if (size <= 1) {
            stageHandler.displayMessage("No passwords found.");
            return;
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        for (int i = 1; i < size; i++) {

            String domain = JsonExtract.extract(message, "data", String.valueOf(i), "domain");
            String login = JsonExtract.extract(message, "data", String.valueOf(i), "login");

            Label domainLabel = new Label("Domain: " + domain);
            Label loginLabel = new Label("Login: " + login);
            Button deleteBtn = UiCreator.createButton("Delete");

            deleteBtn.setOnAction(e -> {
                try {
                    ObjectNode req = new ObjectMapper().createObjectNode();

                    req.put("type", "deletePassword");
                    req.put("username", viewedUser);
                    req.put("domain", domain);
                    req.put("login", login);

                    stageHandler.getClientHandler().sendMessage(req.toString());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    ShowAlert.error("Failed to delete password");
                }
            });


            VBox entry = new VBox(5, domainLabel, loginLabel, deleteBtn);
            entry.getStyleClass().add("card");
            layout.getChildren().add(entry);
        }

        Button backBtn = UiCreator.createButton("Back");

        //backBtn.setOnAction(e -> stageHandler.setScene(UserSession.getCurrentUser().generateLayout(), "Admin panel"));
        backBtn.setOnAction(e ->{
            refreshUsersView();
        });
        layout.getChildren().add(backBtn);

        stageHandler.setScene(layout, "User passwords");
    }

    private void openShowLogsView() {

        LabeledField userIdField = UiCreator.createText("User ID");
        LabeledField actionField = UiCreator.createText("Action");
        LabeledField ipField = UiCreator.createText("IP");

        LabeledSelect successSelect = UiCreator.createSelect("Success", "", "true", "false");

        DatePicker fromDatePicker = new DatePicker();
        DatePicker toDatePicker = new DatePicker();

        LabeledField limitField = UiCreator.createText("Limit");
        LabeledField offsetField = UiCreator.createText("Offset");

        TextArea resultArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");
        resultArea.setEditable(false);

        Button searchBtn = UiCreator.createButton("Search");
        Button backBtn = UiCreator.createButton("Back");

        searchBtn.setOnAction(e -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode req = mapper.createObjectNode();

                req.put("type", "getAuditLogs");

                if (!userIdField.getValue().isEmpty()) {
                    req.put("userId", Integer.parseInt(userIdField.getValue()));
                }

                if (!actionField.getValue().isEmpty()) {
                    req.put("action", actionField.getValue());
                }

                if (!ipField.getValue().isEmpty()) {
                    req.put("ip", ipField.getValue());
                }

                if (!successSelect.getValue().isEmpty()) {
                    req.put("success", Boolean.parseBoolean(successSelect.getValue()));
                }

                if (fromDatePicker.getValue() != null) {
                    req.put("from", toInstant(fromDatePicker.getValue()).toString());
                }

                if (toDatePicker.getValue() != null) {
                    req.put("to", toInstant(toDatePicker.getValue()).toString());
                }

                if (!limitField.getValue().isEmpty()) {
                    req.put("limit", Integer.parseInt(limitField.getValue()));
                }

                if (!offsetField.getValue().isEmpty()) {
                    req.put("offset", Integer.parseInt(offsetField.getValue()));
                }

                clientHandler.sendMessage(req.toString());

            } catch (Exception ex) {
                ex.printStackTrace();
                ShowAlert.error("Failed to fetch logs");
            }
        });

        backBtn.setOnAction(e -> stageHandler.setScene(generateLayout(), "Admin panel"));

        VBox root = new VBox(
                10,
                userIdField.getRoot(),
                actionField.getRoot(),
                ipField.getRoot(),
                successSelect.getRoot(),
                new Label("From date"),
                fromDatePicker,
                new Label("To date"),
                toDatePicker,
                limitField.getRoot(),
                offsetField.getRoot(),
                searchBtn,
                backBtn,
                resultArea
        );

        root.setPadding(new Insets(15));

        stageHandler.setScene(root, "Audit logs");
    }

    private void handleGetLogs(String message) {

        int size = JsonExtract.getArraySize(message, "data");

        if (size <= 1) {
            stageHandler.displayMessage("No logs found.");
            return;
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        for (int i = 1; i < size; i++) {

            String id = JsonExtract.extract(message, "data", String.valueOf(i), "id");
            String timestamp = JsonExtract.extract(message, "data", String.valueOf(i), "timestamp");
            String userId = JsonExtract.extract(message, "data", String.valueOf(i), "user_id");
            String action = JsonExtract.extract(message, "data", String.valueOf(i), "action");
            String success = JsonExtract.extract(message, "data", String.valueOf(i), "success");
            String ip = JsonExtract.extract(message, "data", String.valueOf(i), "ip_address");
            String response = JsonExtract.extract(message, "data", String.valueOf(i), "response_data");

            Label idLabel = new Label("ID: " + id);
            Label timeLabel = new Label("Time: " + timestamp);
            Label userLabel = new Label("User ID: " + userId);
            Label actionLabel = new Label("Action: " + action);
            Label successLabel = new Label("Success: " + success);
            Label ipLabel = new Label("IP: " + ip);
            TextArea responseArea = new TextArea("Response: " + response);
            responseArea.setEditable(false);
            responseArea.setWrapText(true);
            responseArea.setPrefRowCount(5);
            //responseArea.setStyle("-fx-control-inner-background: #f5f5f5;");

            VBox entry = new VBox(4, idLabel, timeLabel, userLabel, actionLabel, successLabel, ipLabel, responseArea);

            //entry.setStyle("-fx-border-color: gray; -fx-padding: 8;");
            entry.getStyleClass().add("card");
            layout.getChildren().add(entry);
        }

        Button backBtn = UiCreator.createButton("Back");

        backBtn.setOnAction(e -> stageHandler.setScene(generateLayout(), "Admin panel"));

        layout.getChildren().add(backBtn);

        stageHandler.setScene(layout, "Audit logs");
    }

    private void refreshPasswordsView() {
        if (lastPasswordRequest != null) {
            clientHandler.sendMessage(lastPasswordRequest);
        }
    }

    private void refreshUsersView(){
        if(lastUsersRequest != null){
            clientHandler.sendMessage(lastUsersRequest);
        }
    }

    private Instant toInstant(LocalDate date) {
        if (date == null) return null;
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }
}
