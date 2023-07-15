package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;
import pl.koder95.intencje.core.db.DB;
import pl.koder95.intencje.gui.MainFrame;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static javax.swing.JOptionPane.*;

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
                showMessageDialog(null, "Nie udało się załadować ustawień połączenia z bazą danych", "Błąd wczytywania", ERROR_MESSAGE);
            }
        } else {
            Path parent = Paths.DB_CONN_DATA_FILE.getParent();
            if (Files.notExists(parent)) {
                try {
                    Files.createDirectories(parent);
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessageDialog(null, "Nie udało się utworzyć folderu dla ustawień programu", "Błąd podczas tworzenia folderu", ERROR_MESSAGE);
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
                    int option = showConfirmDialog(null, "Udało się połączyć z bazą danych. Zapisać ustawienia?", "Połączono", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                    settings.store(Files.newOutputStream(Paths.DB_CONN_DATA_FILE), "");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showMessageDialog(null, "Nie udało się zapisać ustawień połączenia z bazą danych", "Błąd zapisu", ERROR_MESSAGE);
                }
            }
        } else {
            showMessageDialog(null, "Nie udało się nawiązać połączenia z bazą danych.\nPorzucono ustawienia.", "Błąd łączenia", JOptionPane.ERROR_MESSAGE);
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
