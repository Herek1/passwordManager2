package client.Users;

import client.ClientHandler;
import client.Util.Encryption;
import client.Util.LabeledField;
import client.StageHandler;
import client.Util.UiCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
}