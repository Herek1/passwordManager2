package client.Users;

import client.ClientHandler;
import client.LabeledField;
import client.StageHandler;
import client.UiCreator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class NormalUser extends User {
    private final ClientHandler clientHandler;
    private final StageHandler stageHandler;

    public NormalUser(int id, String name, String surname, ClientHandler clientHandler, StageHandler stageHandler) {
        super(id, name, surname);
        this.clientHandler = clientHandler;
        this.stageHandler = stageHandler;
    }

    @Override
    public VBox generateLayout() {
        // Tworzenie przycisków
        Button addBtn = UiCreator.createButton("Add password");
        Button checkBtn = UiCreator.createButton("Check password");
        Button deleteBtn = UiCreator.createButton("Delete password");
        Button logoutBtn = UiCreator.createButton("Log out");

        // Obsługa kliknięć
        addBtn.setOnAction(e -> openAddPasswordView());
        checkBtn.setOnAction(e -> openCheckPasswordView());
        deleteBtn.setOnAction(e -> openDeletePasswordView());
        logoutBtn.setOnAction(e -> stageHandler.setDefaultView());

        // Ustawienie layoutu
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