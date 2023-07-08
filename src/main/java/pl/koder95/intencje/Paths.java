package pl.koder95.intencje;

import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Path;

public class Paths {

    private Paths() {}

    public static final Path USER_HOME_DIR = java.nio.file.Paths.get(SystemUtils.USER_HOME);
    public static final Path MY_APP_DATA_DIR = getAppDataDir().resolve(Main.APP_NAME + "/");
    public static final Path DB_CONN_DATA_FILE = MY_APP_DATA_DIR.resolve("db-conn.properties");

    public static Path getAppDataDir() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return java.nio.file.Paths.get(System.getenv("LOCALAPPDATA"));
        } else if (SystemUtils.IS_OS_UNIX) {
            return USER_HOME_DIR.resolve(".appdata/local/");
        } else {
            return USER_HOME_DIR.resolve("AppData/Local/");
        }
    }
}
