package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.Option;
import pl.koder95.intencje.core.cli.OptionService;
import pl.koder95.intencje.core.db.DB;

import java.util.Properties;

public class ConfigCLI implements OptionService {

    @Override
    public boolean consume(Option option) {
        if (option.getName().equalsIgnoreCase("config")) {
            Properties settings = option.getVarsAsProperties();
            DB.initConnectionProperties(settings);
            return true;
        }
        return false;
    }
}
