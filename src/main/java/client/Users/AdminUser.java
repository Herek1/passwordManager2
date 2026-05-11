package client.Users;

import client.ClientHandler;
import client.Util.JsonExtract;
import client.Views.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
        Button logoutBtn = UiCreator.createButton("Log out");

        addBtn.setOnAction(e -> openAddUserView());
        checkBtn.setOnAction(e -> openManageUsersView());
        logoutBtn.setOnAction(e -> stageHandler.setDefaultView());

        VBox root = new VBox(15, addBtn, checkBtn, logoutBtn);
        root.setPadding(new Insets(15));

        return root;
    }

    @Override
    public void handleMessage(String message) throws Exception {
        String type = JsonExtract.extract(message, "type");
        switch (type) {
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

                clientHandler.sendMessage(
                        request.toString()
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                ShowAlert.error("Failed to create user");
            }
        });

        VBox root = new VBox(
                15,
                loginField.getRoot(),
                roleSelect.getRoot(),
                createBtn,
                backBtn
        );

        root.setPadding(new Insets(15));

        stageHandler.setScene(
                root,
                "Create user"
        );
    }

    private void openManageUsersView(){}
}
