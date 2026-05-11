package client.Views;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LabeledSelect {

    private final GridPane root;
    private final ComboBox<String> comboBox;

    public LabeledSelect(String labelText, String... options) {

        this.root = new GridPane();

        Label label = new Label(labelText + ":");
        label.setMinWidth(UiCreator.LABEL_WIDTH);

        this.comboBox = new ComboBox<>(
                FXCollections.observableArrayList(options)
        );
        comboBox.setMinWidth(UiCreator.FIELD_WIDTH);
        if (options.length > 0) {
            comboBox.setValue(options[0]);
        }
        root.add(label, 0, 0);
        root.add(comboBox, 1, 0);
    }

    public String getValue() {
        return comboBox.getValue();
    }

    public GridPane getRoot() {
        return root;
    }
}