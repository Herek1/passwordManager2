module main.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires org.apache.commons.csv;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens main.passwordmanager to javafx.fxml;
    exports client;
    exports client.Users;
}