package client.Views;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

public class ShowAlert {

    private static void show(Alert.AlertType type, String title, String message) {

        Platform.runLater(() -> {

            Alert alert = new Alert(type, message, ButtonType.OK);

            alert.setTitle(title);
            alert.setHeaderText(null);

            DialogPane pane = alert.getDialogPane();

            pane.getStylesheets().add(ShowAlert.class.getResource("/main/passwordmanager/style.css").toExternalForm());
            pane.getStyleClass().add("dialog-pane");

            alert.showAndWait();
        });
    }

    public static void error(String message) {
        show(Alert.AlertType.ERROR, "Error", message);
    }
    public static void info(String message) {
        Platform.runLater(() -> {
            show(Alert.AlertType.INFORMATION, "Information", message);
        });
    }
}