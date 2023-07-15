package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;
import pl.koder95.intencje.core.db.DB;
import pl.koder95.intencje.gui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {

    public static final String PARISH_NAME = "Parafia rzymskokatolicka...";
    public static final String APP_NAME = "Intencje";
    public static final String APP_HEADER = "Intencje – " + PARISH_NAME;

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        setupLookAndFeel();
		
        Properties settings = new Properties();
        if (Files.exists(Paths.DB_CONN_DATA_FILE)) {
            try {
                settings.load(Files.newInputStream(Paths.DB_CONN_DATA_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Path parent = Paths.DB_CONN_DATA_FILE.getParent();
            if (Files.notExists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String hostname = "";
            String dbName = "";
            String user = "";
            String password = "";
            settings.setProperty("driver", "mysql");
            settings.setProperty("hostname", hostname);
            settings.setProperty("dbName", dbName);
            settings.setProperty("user", user);
            settings.setProperty("password", password);
        }
        DB.initConnectionProperties(settings);
        if (DB.test()) {
            if (Files.notExists(Paths.DB_CONN_DATA_FILE)) {
                try {
                    settings.store(Files.newOutputStream(Paths.DB_CONN_DATA_FILE), "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
