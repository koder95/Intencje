package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.Option;
import pl.koder95.intencje.core.cli.OptionService;
import pl.koder95.intencje.core.db.DB;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigCLI implements OptionService {

    @Override
    public boolean consume(Option option) {
        if (option.getName().equalsIgnoreCase("config")) {
            boolean saving = false, printing = false;
            if (option.containsVar("s")) {
                option.popVar("s");
                saving = true;
            }
            if (option.containsVar("p")) {
                option.popVar("p");
                printing = true;
            }
            Properties settings = option.getVarsAsProperties();
            if (printing) {
                System.out.println("Wprowadzone ustawienia: " + settings);
            }
            if (saving) {
                try {
                    Path dir = Paths.DB_CONN_DATA_FILE.getParent();
                    if (Files.notExists(dir)) Files.createDirectories(dir);
                    settings.store(Files.newBufferedWriter(Paths.DB_CONN_DATA_FILE, StandardCharsets.UTF_8), "");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            DB.initConnectionProperties(settings);
            if (printing) {
                if (DB.test()) System.out.println("Połączono z bazą danych");
                else System.out.println("Nie udało się połączyć z bazą danych");
            }
            return true;
        }
        return false;
    }
}
