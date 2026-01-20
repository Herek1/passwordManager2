package client;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LabeledField {

    private final GridPane root;
    private final TextField field;

    public LabeledField(String labelText, TextField field) {
        this.field = field;
        this.root = new GridPane();

        Label label = new Label(labelText + ":");
        label.setMinWidth(UiCreator.LABEL_WIDTH);
        field.setMinWidth(UiCreator.FIELD_WIDTH);

        root.add(label, 0, 0);
        root.add(field, 1, 0);
    }

    public static LabeledField text(String name) {
        return new LabeledField(name, new TextField());
    }

    public static LabeledField password(String name) {
        return new LabeledField(name, new PasswordField());
    }

    public String getValue() {
        return field.getText();
    }

    public GridPane getRoot() {
        return root;
    }
}
