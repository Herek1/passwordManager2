package client.Users;

import client.ClientHandler;
import client.Util.*;
import client.Views.LabeledField;
import client.Views.StageHandler;
import client.Views.UiCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class NormalUser extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public NormalUser(String username, String password, String role, ClientHandler clientHandler, StageHandler stageHandler) {
        super(username, password, role);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        Button addBtn = UiCreator.createButton("Add password");
        Button checkBtn = UiCreator.createButton("Check password");
        Button logoutBtn = UiCreator.createButton("Log out");

        addBtn.setOnAction(e -> openAddPasswordView());
        checkBtn.setOnAction(e -> openCheckPasswordView());
        logoutBtn.setOnAction(e -> stageHandler.setDefaultView());

        VBox root = new VBox(15, addBtn, checkBtn, logoutBtn);
        root.setPadding(new Insets(15));

        return root;
    }

    private void openAddPasswordView() {
        LabeledField loginField = UiCreator.createText("Login");
        LabeledField passField = UiCreator.createPassword("Password");
        LabeledField urlField = UiCreator.createText("Domain URL");

        Button save = UiCreator.createButton("Save login");
        Button back = UiCreator.createButton("Back");

        back.setOnAction(e -> stageHandler.setScene(generateLayout(),"Password manager"));
        save.setOnAction(e ->{
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonRequestNode = objectMapper.createObjectNode();
                jsonRequestNode.put("type", "addPassword");
                jsonRequestNode.put("username", getUsername());
                jsonRequestNode.put("login", loginField.getValue());
                jsonRequestNode.put("password", Encryption.encryptPassword(getMaster_password(), passField.getValue()));
                jsonRequestNode.put("domain", urlField.getValue());
                clientHandler.sendMessage(jsonRequestNode.toString());
            }catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        VBox root = new VBox(
                loginField.getRoot(),
                passField.getRoot(),
                urlField.getRoot(),
                save,
                back
        );
        stageHandler.setScene(root, "Add password");
    }

    @Override
    public void openCheckPasswordView() {
        LabeledField urlField = UiCreator.createText("Domain URL");
        TextArea resultArea = stageHandler.getMessagesArea();
        stageHandler.displayMessage("");
        resultArea.setEditable(false);

        Button searchBtn = UiCreator.createButton("Search");
        Button backBtn = UiCreator.createButton("Back");
        Button viewAllBtn = UiCreator.createButton("View all");

        searchBtn.setOnAction(e -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "getPassword");
            jsonRequestNode.put("username", this.getUsername());
            jsonRequestNode.put("url", urlField.getValue());
            clientHandler.sendMessage(jsonRequestNode.toString());
        });
        viewAllBtn.setOnAction(e -> {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "getPassword");
            jsonRequestNode.put("username", this.getUsername());
            jsonRequestNode.put("url", "");
            clientHandler.sendMessage(jsonRequestNode.toString());
        });

        backBtn.setOnAction(e -> stageHandler.setScene(generateLayout(),"Password manager"));

        VBox root = new VBox(
                urlField.getRoot(),
                searchBtn,
                viewAllBtn,
                backBtn,
                resultArea
        );

        stageHandler.setScene(root, "Check Password");
    }

    @Override
    public void handleGetPasswords(String response) throws Exception {

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
}