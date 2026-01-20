package client.Users;

import javafx.scene.layout.VBox;

public abstract class User {
    private final int id;
    private final String name;
    private final String surname;

    public User(int id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public abstract VBox generateLayout();

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}