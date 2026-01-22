package client.Users;

import client.ClientHandler;
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
        Button deleteBtn = UiCreator.createButton("Delete password");
        Button logoutBtn = UiCreator.createButton("Log out");

        addBtn.setOnAction(e -> openAddPasswordView());
        checkBtn.setOnAction(e -> openCheckPasswordView());
        deleteBtn.setOnAction(e -> openDeletePasswordView());
        logoutBtn.setOnAction(e -> stageHandler.setDefaultView());

        VBox root = new VBox(15, addBtn, checkBtn, deleteBtn, logoutBtn);
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
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonRequestNode = objectMapper.createObjectNode();
            jsonRequestNode.put("type", "addPassword");
            jsonRequestNode.put("username", loginField.getValue());
            jsonRequestNode.put("password", passField.getValue());
            jsonRequestNode.put("domain", urlField.getValue());
            clientHandler.sendMessage(jsonRequestNode.toString());
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
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);

        Button searchBtn = UiCreator.createButton("Search");
        Button backBtn = UiCreator.createButton("Back");
        Button viewAllBtn = UiCreator.createButton("View all");

        searchBtn.setOnAction(e -> {});
        viewAllBtn.setOnAction(e -> {});

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

    private void openDeletePasswordView() {
        LabeledField loginField = UiCreator.createText("Login");
        LabeledField urlField = UiCreator.createText("Domain URL");

        Button delete = UiCreator.createButton("Delete login");
        Button back = UiCreator.createButton("Back");

        delete.setOnAction(e -> {});

        back.setOnAction(e -> stageHandler.setScene(generateLayout(),"Password manager"));

        VBox root = new VBox(
                loginField.getRoot(),
                urlField.getRoot(),
                delete,
                back
        );

        stageHandler.setScene(root, "Delete password");
    }
}