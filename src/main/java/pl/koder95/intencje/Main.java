package pl.koder95.intencje;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.koder95.intencje.core.cli.CL;
import pl.koder95.intencje.core.db.DB;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class Main extends Application {
	
    public static final String PARISH_NAME = "Parafia rzymskokatolicka...";
    public static final String APP_NAME = "Intencje";
    public static final String APP_HEADER = "Intencje – " + PARISH_NAME;
	
    public static final int ERR_ENV_REQ = 300;

    @Override
    public void init() throws Exception {
        super.init();
        if (Files.exists(Paths.DB_CONN_DATA_FILE)) {
            try {
                Properties properties = new Properties();
                properties.load(Files.newInputStream(Paths.DB_CONN_DATA_FILE));
                DB.initConnectionProperties(properties);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage config = FXMLLoader.load(ClassLoader.getSystemResource("pl/koder95/intencje/gui/Config.fxml"));
        config.showAndWait();

        VBox main = new VBox();

        primaryStage.setScene(new Scene(main));
        primaryStage.setTitle(APP_HEADER);
        primaryStage.show();
    }

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        launch(args);
    }
}
