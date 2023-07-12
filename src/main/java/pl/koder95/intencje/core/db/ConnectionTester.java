package pl.koder95.intencje.core.db;

import java.time.LocalDateTime;

public class ConnectionTester {

    public static class Test {

        private final LocalDateTime testTime = LocalDateTime.now();
        private final boolean internetConnection, domainNameResolvingCorrect, databaseServerConnection, databaseConfig;

        private Test(boolean internetConnection, boolean domainNameResolvingCorrect,
                     boolean databaseServerConnection, boolean databaseConfig) {
            this.internetConnection = internetConnection;
            this.domainNameResolvingCorrect = domainNameResolvingCorrect;
            this.databaseServerConnection = databaseServerConnection;
            this.databaseConfig = databaseConfig;
        }

        public LocalDateTime getTestTime() {
            return testTime;
        }

        public boolean isInternetConnection() {
            return internetConnection;
        }

        public boolean isDomainNameResolvingCorrect() {
            return domainNameResolvingCorrect;
        }

        public boolean isDatabaseServerConnection() {
            return databaseServerConnection;
        }

        public boolean isDatabaseConfig() {
            return databaseConfig;
        }
    }

    public static class DatabaseTableNamespace {

        private final String prefix, intentionTableName, dayNameTableName;

        public DatabaseTableNamespace(String prefix, String intentionTableName, String dayNameTableName) {
            this.prefix = prefix;
            this.intentionTableName = intentionTableName;
            this.dayNameTableName = dayNameTableName;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getIntentionTableName() {
            return intentionTableName;
        }

        public String getDayNameTableName() {
            return dayNameTableName;
        }
    }
}
