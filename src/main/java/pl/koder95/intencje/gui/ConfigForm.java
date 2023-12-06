package pl.koder95.intencje.gui;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import pl.koder95.intencje.Paths;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class ConfigForm {

    @FXML
    private TextField hostname, dbName, user, password;

    public void initialize() {
        if (Files.exists(Paths.DB_CONN_DATA_FILE)) {
            try {
                Properties properties = new Properties();
                properties.load(Files.newInputStream(Paths.DB_CONN_DATA_FILE));
                hostname.textProperty().setValue(properties.getProperty("hostname"));
                dbName.textProperty().setValue(properties.getProperty("dbName"));
                user.textProperty().setValue(properties.getProperty("user"));
                password.textProperty().setValue(properties.getProperty("password"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchAutoUser(ActionEvent actionEvent) {
        boolean selected = ((CheckBox) actionEvent.getSource()).isSelected();
        user.editableProperty().set(!selected);
        if (selected) {
            user.textProperty().bind(dbName.textProperty());
        } else {
            user.textProperty().unbind();
        }
    }

    public StringProperty getHostname() {
        return hostname.textProperty();
    }

    public StringProperty getDbName() {
        return dbName.textProperty();
    }

    public StringProperty getUser() {
        return user.textProperty();
    }

    public StringProperty getPassword() {
        return password.textProperty();
    }
}
