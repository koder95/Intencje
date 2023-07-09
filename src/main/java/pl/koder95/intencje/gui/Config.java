package pl.koder95.intencje.gui;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import pl.koder95.intencje.Paths;
import pl.koder95.intencje.core.db.DB;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class Config {

    private static final Paint UNDEFINED_STATUS = Color.GREY;
    private static final Paint PROCESSING_STATUS = Color.ORANGE;
    private static final Paint FAILED_STATUS = Color.RED;
    private static final Paint OK_STATUS = Color.GREEN;

    @FXML private Stage stage;
    @FXML private VBox form;
    @FXML private ConfigForm formController;
    @FXML private Circle testing;

    private void setConnectionStatus(int status) {
        testing.setFill(status == -1? FAILED_STATUS : status == 0? PROCESSING_STATUS : status == 1? OK_STATUS : UNDEFINED_STATUS);
    }

    private Properties getSettings() {
        Properties settings = new Properties();
        settings.setProperty("driver", "mysql");
        settings.setProperty("hostname", formController.getHostname().getValue());
        settings.setProperty("dbName", formController.getDbName().getValue());
        settings.setProperty("user", formController.getUser().getValue());
        settings.setProperty("prefix", formController.getPrefix().getValue());
        settings.setProperty("password", formController.getPassword().getValue());
        return settings;
    }

    public void acceptSettings(ActionEvent actionEvent) {
        try {
            getSettings().store(Files.newOutputStream(Paths.DB_CONN_DATA_FILE), null);
            stage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startTest(ActionEvent actionEvent) {
        setConnectionStatus(0);
        new Thread(() -> {
            DB.initConnectionProperties(getSettings());
            if (DB.test()) {
                setConnectionStatus(1);
            } else {
                setConnectionStatus(-1);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setConnectionStatus(-2);
        }).start();
    }

    public void abortSettings(Event Event) {
        stage.close();
    }
}
