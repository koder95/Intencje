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
        if (Files.notExists(Paths.DB_CONN_DATA_FILE)) {
            checkParentDirExists();
            fromUI(settings);
            DB.initConnectionProperties(settings);
            if (DB.test()) {
                int option = showConfirmDialog(null, "Udało się połączyć z bazą danych. Zapisać ustawienia?", "Połączono", JOptionPane.YES_NO_OPTION);
                while (true) {
                    try {
                        if (option == JOptionPane.YES_OPTION) {
                            settings.store(Files.newOutputStream(Paths.DB_CONN_DATA_FILE), "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (showConfirmDialog(null, "Nie udało się zapisać ustawień połączenia z bazą danych. Ponowić próbę?", "Błąd zapisu", YES_NO_OPTION, ERROR_MESSAGE) == NO_OPTION) break;
                    }
                }
            }
            else {
                showMessageDialog(null, "Nie udało się nawiązać połączenia z bazą danych.\nPorzucono ustawienia.", "Błąd łączenia", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            // W przypadku istnienia zapisanych ustawień dla połączenia z bazą danych:
            try {
                settings.load(Files.newInputStream(Paths.DB_CONN_DATA_FILE)); // – wczytywanie ustawień
                DB.initConnectionProperties(settings); // – ustawianie parametrów połączenia
            } catch (IOException e) {
                e.printStackTrace();
                showMessageDialog(null, "Nie można wczytać ustawień dla połączenia z bazą danych", "Błąd wczytywania ustawień", ERROR_MESSAGE);
            }
        }
        DB.test();
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    private static void checkParentDirExists() {
        Path parent = Paths.DB_CONN_DATA_FILE.getParent();
        if (Files.notExists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                e.printStackTrace();
                showMessageDialog(null, "Nie udało się utworzyć folderu dla ustawień programu", "Błąd podczas tworzenia folderu", ERROR_MESSAGE);
            }
        }
    }

    private static void fromUI(Properties settings) {
        String hostname = showInputDialog(null, "Wprowadź nazwę serwera bazy danych", "Serwer bazy danych", JOptionPane.QUESTION_MESSAGE);
        String dbName = showInputDialog(null, "Wprowadź nazwę bazy danych", "Baza danych", JOptionPane.QUESTION_MESSAGE);
        String user = showInputDialog(null, "Wprowadź nazwę użytkownika bazy danych", "Nazwa użytkownika", JOptionPane.QUESTION_MESSAGE);
        String password = showInputDialog(null, "Wprowadź hasło", "Hasło", JOptionPane.QUESTION_MESSAGE);
        settings.setProperty("driver", "mysql");
        settings.setProperty("hostname", hostname);
        settings.setProperty("dbName", dbName);
        settings.setProperty("user", user);
        settings.setProperty("password", password);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
