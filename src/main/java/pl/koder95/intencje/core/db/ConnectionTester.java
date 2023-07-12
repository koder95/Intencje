package pl.koder95.intencje.core.db;

import java.net.InetAddress;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionTester {

    private static final byte[] DEFAULT_ADDRESS = {1, 1, 1, 1};
    private static final String DEFAULT_HOST = "google.com";
    private final TestMaker maker = new TestMaker();
    private final byte[] address;
    private final String host, databaseHost, commonSearch, dayNameEnding;

    ConnectionTester(byte[] address, String host, String databaseHost, String commonSearch, String dayNameEnding) {
        this.address = address;
        this.host = host;
        this.databaseHost = databaseHost;
        this.commonSearch = commonSearch;
        this.dayNameEnding = dayNameEnding;
    }

    ConnectionTester(String databaseHost, String commonSearch, String dayNameEnding) {
        this(DEFAULT_ADDRESS, DEFAULT_HOST, databaseHost, commonSearch, dayNameEnding);
    }

    
    public void test() {
        maker.clear().testInternetConnection(address).testDomainNameResolving(host)
                .testDatabaseServerConnection(databaseHost).testDatabaseServerConnection(databaseHost)
                .testDatabaseConfig(commonSearch, dayNameEnding);
    }

    public Test getTestResult() {
        return maker.getConnectionTest();
    }

    public DatabaseTableNamespace getDatabaseTableNamespace() {
        return maker.getDatabaseTableNamespace();
    }

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

    private static class TestMaker {

        boolean internetConnection, domainNameResolvingCorrect, databaseServerConnection, databaseConfig;
        String prefix, intentionTableName, dayNameTableName;

        private TestMaker() {}

        private boolean testConnection(byte[] address) {
            try {
                InetAddress ip = InetAddress.getByAddress(address);
                return ip.isReachable(500);
            } catch (Exception e) {
                return false;
            }
        }

        private boolean testConnection(String host) {
            try {
                InetAddress ip = InetAddress.getByName(host);
                return ip.isReachable(500);
            } catch (Exception e) {
                return false;
            }
        }

        TestMaker testInternetConnection(byte[] address) {
            internetConnection = testConnection(address);
            return this;
        }

        TestMaker testDomainNameResolving(String host) {
            domainNameResolvingCorrect = internetConnection && testConnection(host);
            return this;
        }

        TestMaker testDatabaseServerConnection(String dbHost) {
            databaseServerConnection = domainNameResolvingCorrect && testConnection(dbHost);
            return this;
        }

        TestMaker testDatabaseConfig(String commonSearch, String dayNameEnding) {
            if (!databaseServerConnection) {
                databaseConfig = false;
                return this;
            }
            try {
                Stream<String> stream = DB.tables().parallelStream().filter(table -> table.contains(commonSearch));
                List<String> candidates = stream.collect(Collectors.toList());
                String intentionTable = candidates.parallelStream()
                        .filter(table -> table.endsWith(commonSearch))
                        .findFirst().orElse(null);
                String dayNameTable = candidates.parallelStream()
                        .filter(table -> table.endsWith(commonSearch + dayNameEnding))
                        .findFirst().orElse(null);
                StringBuilder prefix = new StringBuilder();
                databaseConfig = intentionTable != null && dayNameTable != null;
                if (databaseConfig) {
                    while (!intentionTable.isEmpty() && !dayNameTable.isEmpty()) {
                        char intentionFirst = intentionTable.charAt(0);
                        char dayNameFirst = dayNameTable.charAt(0);
                        if (intentionFirst != dayNameFirst) break;
                        prefix.append(intentionFirst);
                        intentionTable = intentionTable.substring(1);
                        dayNameTable = dayNameTable.substring(1);
                    }
                    this.prefix = prefix.toString();
                    this.intentionTableName = intentionTable;
                    this.dayNameTableName = dayNameTable;
                } else {
                    this.prefix = null;
                    this.intentionTableName = null;
                    this.dayNameTableName = null;
                }
            } catch (SQLException e) {
                databaseConfig = false;
            }
            return this;
        }

        Test getConnectionTest() {
            return new Test(internetConnection, domainNameResolvingCorrect, databaseServerConnection, databaseConfig);
        }

        DatabaseTableNamespace getDatabaseTableNamespace() {
            return new DatabaseTableNamespace(prefix, intentionTableName, dayNameTableName);
        }

        TestMaker clear() {
            internetConnection = false; domainNameResolvingCorrect = false;
            databaseServerConnection = false; databaseConfig = false;
            prefix = null; intentionTableName = null; dayNameTableName = null;
            return this;
        }
    }
}
