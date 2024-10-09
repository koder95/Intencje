/*
 * Copyright (c) 2022.
 */

package pl.koder95.intencje.core.db;

import pl.koder95.intencje.event.ConnectionTestingEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class DB {

    private static final Properties CONN_PROP = new Properties();
    private static final String DEFAULT_COMMON_SEARCH = "intencje";
    private static final String DEFAULT_DAY_NAME_ENDING = "_nazwy";
    private static Connection CONN = null;
    private static ConnectionTester TESTER = null;
    private DB() {}

    private static String url(String driver, String hostname, String dbName) {
        if (driver == null || hostname == null || dbName == null) {
            throw new IllegalStateException("The connection has not been configured yet");
        }
        return "jdbc:" + driver + "://" + hostname + "/" + dbName;
    }

    private static String url(Properties settings, String driverKey, String hostnameKey, String dbNameKey) {
        if (settings == null || !settings.containsKey(driverKey) || !settings.containsKey(hostnameKey) || !settings.containsKey(dbNameKey)) {
            throw new IllegalStateException("The connection has not been configured yet");
        }
        return url(settings.getProperty(driverKey), settings.getProperty(hostnameKey), settings.getProperty(dbNameKey));
    }

    public static List<String> tables(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute("SHOW TABLES;");
        ResultSet resultSet = statement.getResultSet();
        List<String> tables = new LinkedList<>();
        while (resultSet.next()) {
            tables.add(resultSet.getString(1));
        }
        ArrayList<String> result = new ArrayList<>(tables);
        tables.clear();
        return result;
    }

    public static List<String> tables(String url, String user, String password) throws SQLException {
        user = user == null? "" : user;
        password = password == null? "" : password;
        if (!user.isEmpty() && !password.isEmpty())
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                return tables(conn);
            }
        else throw new IllegalStateException("The connection has not been configured yet");
    }

    private static String url() {
        return url(CONN_PROP, "driver", "hostname", "dbName");
    }

    static Connection conn() throws SQLException {
        if (CONN == null) {
            if (CONN_PROP.containsKey("user") && CONN_PROP.containsKey("password"))
                CONN = DriverManager.getConnection(url(), CONN_PROP.getProperty("user"), CONN_PROP.getProperty("password"));
            else throw new IllegalStateException("The connection has not been configured yet");
        }
        if (CONN.isValid(0)) return CONN;
        else {
            CONN = null;
            return conn();
        }
    }

    public static List<String> tables() throws SQLException {
        if (CONN != null && CONN_PROP.containsKey("user") && CONN_PROP.containsKey("password")) {
            return tables(url(), CONN_PROP.getProperty("user"), CONN_PROP.getProperty("password"));
        }
        else throw new IllegalStateException("The connection has not been configured yet");
    }

    public static void initConnectionProperties(Properties p) {
        CONN_PROP.clear();
        TESTER = null;
        if (p != null) CONN_PROP.putAll(p);
    }

    public static String getTablePrefix() {
        if (CONN_PROP.containsKey("prefix")) {
            return CONN_PROP.getProperty("prefix");
        } else {
            throw new IllegalStateException("Cannot find property called 'prefix'. Please check configuration files.");
        }
    }
    
    private static ConnectionTester.Test LAST_TEST = null;
    private static Namespace LAST_FOUND_NAMESPACE = null;

    static void test(ConnectionTester tester) {
        tester.test(new ConnectionTestingEvent(DB.class));
        LAST_TEST = tester.getTestResult();
        LAST_FOUND_NAMESPACE = tester.getDatabaseTableNamespace();
    }

    public static boolean test() {
        if (TESTER == null && CONN_PROP.containsKey("hostname")) {
            TESTER = new ConnectionTester(CONN_PROP.getProperty("hostname"), DEFAULT_COMMON_SEARCH, DEFAULT_DAY_NAME_ENDING);
        } else throw new IllegalStateException("The connection has not been configured yet");
        test(TESTER);
        return LAST_TEST.isDatabaseConfig();
    }

    static String getDayNameTableName() {
        if (LAST_FOUND_NAMESPACE == null) throw new IllegalStateException("Test the connection before!");
        return LAST_FOUND_NAMESPACE.getPrefix() + LAST_FOUND_NAMESPACE.getDayNameTableName();
    }

    static String getIntentionTableName() {
        if (LAST_FOUND_NAMESPACE == null) throw new IllegalStateException("Test the connection before!");
        return LAST_FOUND_NAMESPACE.getPrefix() + LAST_FOUND_NAMESPACE.getIntentionTableName();
    }
}
