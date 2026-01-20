package client;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UiCreator {
    public static final int WINDOW_WIDTH  = 520;
    public static final int WINDOW_HEIGHT = 420;

    public static final int VBOX_SPACING = 10;
    public static final Insets ROOT_PADDING = new Insets(20);

    public static final int LABEL_WIDTH = 120; // for LabeledField
    public static final int FIELD_WIDTH = 200;

    public static final int BUTTON_WIDTH = 200;
    public static final int BUTTON_HEIGHT = 32;

    public static Button createButton(String text) {
        Button btn = new Button(text);
        btn.setMinWidth(BUTTON_WIDTH);
        btn.setMinHeight(BUTTON_HEIGHT);
        return btn;
    }

    public static LabeledField createText(String name) {
        return new LabeledField(name, new TextField());
    }

    public static LabeledField createPassword(String name) {
        return new LabeledField(name, new PasswordField());
    }
}