package client.Users;

import javafx.scene.layout.VBox;

public abstract class User {
    private final String username;
    private final String master_password;
    private final String role;

    public User(String username, String master_password, String role) {
        this.username = username;
        this.master_password = master_password;
        this.role = role;
    }

    public abstract VBox generateLayout();
}