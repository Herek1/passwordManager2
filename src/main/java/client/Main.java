//package client;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//import java.util.ArrayList;
//
//public class Main extends Application {
//    private final String fileName = "password.csv";
//    private String masterPassword = "test123";
//    private String userName = "";
//    private MessageHandler fileHandler;
//    private Stage primaryStage;
//
//    @Override
//    public void start(Stage stage) {
//        this.primaryStage = stage;
//        openLoginView();
//        stage.setTitle("Password Manager");
//        stage.show();
//    }
//
//    public void openLoginView() {
//        LabeledField loginField = UiCreator.createText("Login");
//        LabeledField passField = UiCreator.createPassword("Master password");
//
//        Button loginBtn = UiCreator.createButton("Login");
//
//        loginBtn.setOnAction(e -> {
//            if (!loginField.getValue().isBlank() && !passField.getValue().isBlank()) {
//                userName = loginField.getValue();
//                masterPassword = passField.getValue();
//                fileHandler = new MessageHandler(fileName, masterPassword, userName);
//                openMainMenuView();
//            }
//        });
//
//        VBox root = new VBox(loginField.getRoot(), passField.getRoot(), loginBtn);
//        setView(root, "Login");
//    }
//
//
//    public void openMainMenuView() {
//        Button addBtn = UiCreator.createButton("Add password");
//        Button checkBtn = UiCreator.createButton("Check password");
//        Button deleteBtn = UiCreator.createButton("Delete passowrd");
//        Button logout = UiCreator.createButton("Log out");
//
//        addBtn.setOnAction(e -> openAddPasswordView());
//        checkBtn.setOnAction(e -> openCheckPasswordView());
//        deleteBtn.setOnAction(e -> openDeletePasswordView());
//        logout.setOnAction(e -> openLoginView());
//
//        VBox root = new VBox(addBtn, checkBtn, deleteBtn, logout);
//        setView(root, "Main menu");
//    }
//
//    private void openAddPasswordView() {
//        LabeledField loginField = UiCreator.createText("Login");
//        LabeledField passField = UiCreator.createPassword("Password");
//        LabeledField urlField = UiCreator.createText("Domain URL");
//
//        Button save = UiCreator.createButton("Save login");
//        Button back = UiCreator.createButton("Cancel");
//
//        save.setOnAction(e -> {
//            if (!loginField.getValue().isBlank()
//                    && !passField.getValue().isBlank()
//                    && !urlField.getValue().isBlank()) {
//
//                fileHandler.checkAndSaveEntry(
//                        urlField.getValue(),
//                        loginField.getValue(),
//                        passField.getValue()
//                );
//                openMainMenuView();
//            }
//        });
//
//        back.setOnAction(e -> openMainMenuView());
//
//        VBox root = new VBox(
//                loginField.getRoot(),
//                passField.getRoot(),
//                urlField.getRoot(),
//                save,
//                back
//        );
//
//        setView(root, "Add password");
//    }
//
//    public void openCheckPasswordView() {
//        LabeledField urlField = UiCreator.createText("Domain URL");
//        TextArea resultArea = new TextArea();
//        resultArea.setEditable(false);
//        resultArea.setWrapText(true);
//
//        Button searchBtn = UiCreator.createButton("Search");
//        Button backBtn = UiCreator.createButton("Back");
//        Button viewAllBtn = UiCreator.createButton("View all");
//
//        searchBtn.setOnAction(e -> {
//            String url = urlField.getValue();
//            resultArea.clear();
//
//            if (!url.isBlank()) {
//                try {
//                    ArrayList<String[]> entries = fileHandler.checkEntry(url, userName, masterPassword);
//
//                    if (entries.isEmpty()) {
//                        resultArea.setText("No password found for " + url);
//                    } else {
//                        StringBuilder sb = new StringBuilder();
//                        for (String[] entry : entries) {
//                            sb.append("Url: ").append(entry[0]).append("\n");
//                            sb.append("Login: ").append(entry[1]).append("\n");
//                            sb.append("Password: ").append(entry[2]).append("\n");
//                            sb.append("-------------------------\n");
//                        }
//                        resultArea.setText(sb.toString());
//                    }
//                } catch (IllegalArgumentException ex) {
//                    if ("WRONG_MASTER_PASSWORD".equals(ex.getMessage())) {
//                        resultArea.setText("Recheck your master password.");
//                    }
//                }
//            }
//        });
//
//        viewAllBtn.setOnAction(e -> {
//            resultArea.clear();
//            String url = "";
//
//            try {
//                ArrayList<String[]> entries = fileHandler.checkEntry(url, userName, masterPassword);
//
//                if (entries.isEmpty()) {
//                    resultArea.setText("No password found for " + url);
//                } else {
//                    StringBuilder sb = new StringBuilder();
//                    for (String[] entry : entries) {
//                        sb.append("Url: ").append(entry[0]).append("\n");
//                        sb.append("Login: ").append(entry[1]).append("\n");
//                        sb.append("Password: ").append(entry[2]).append("\n");
//                        sb.append("-------------------------\n");
//                    }
//                    resultArea.setText(sb.toString());
//                }
//            } catch (IllegalArgumentException ex) {
//                if ("WRONG_MASTER_PASSWORD".equals(ex.getMessage())) {
//                    resultArea.setText("Recheck your master password.");
//                }
//            }
//        });
//
//        backBtn.setOnAction(e -> openMainMenuView());
//
//        VBox root = new VBox(
//                urlField.getRoot(),
//                searchBtn,
//                viewAllBtn,
//                backBtn,
//                resultArea
//        );
//
//        setView(root, "Check Password");
//    }
//
//    private void openDeletePasswordView() {
//        LabeledField loginField = UiCreator.createText("Login");
//        LabeledField urlField = UiCreator.createText("Domain URL");
//
//        Button delete = UiCreator.createButton("Delete login");
//        Button back = UiCreator.createButton("Cancel");
//
//        delete.setOnAction(e -> {
//            if (!loginField.getValue().isBlank() && !urlField.getValue().isBlank()) {
//
//                fileHandler.deleteEntry(
//                        urlField.getValue(),
//                        loginField.getValue()
//                );
//                openMainMenuView();
//            }
//        });
//
//        back.setOnAction(e -> openMainMenuView());
//
//        VBox root = new VBox(
//                loginField.getRoot(),
//                urlField.getRoot(),
//                delete,
//                back
//        );
//
//        setView(root, "Delete password");
//    }
//
//
//    private void setView(VBox root, String title) {
//        root.setPadding(UiCreator.ROOT_PADDING);
//        root.setSpacing(UiCreator.VBOX_SPACING);
//
//        Scene scene = new Scene(root, UiCreator.WINDOW_WIDTH, UiCreator.WINDOW_HEIGHT);
//        primaryStage.setTitle(title);
//        primaryStage.setScene(scene);
//    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}