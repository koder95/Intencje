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

    private static String url() {
        if (!CONN_PROP.containsKey("driver") || !CONN_PROP.containsKey("hostname") || !CONN_PROP.containsKey("dbName")) {
            throw new IllegalStateException("The connection has not been configured yet");
        }
        return "jdbc:" + CONN_PROP.getProperty("driver") + "://"
                + CONN_PROP.getProperty("hostname") + "/" + CONN_PROP.getProperty("dbName");
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
        try (Connection conn = DB.conn()) {
            Statement test = conn.createStatement();
            test.execute("SHOW TABLES;");
            ResultSet resultSet = test.getResultSet();
            List<String> tables = new LinkedList<>();
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            ArrayList<String> result = new ArrayList<>(tables);
            tables.clear();
            return result;
        }
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
    private static ConnectionTester.DatabaseTableNamespace LAST_FOUND_NAMESPACE = null;

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
        if (LAST_FOUND_NAMESPACE == null) throw new IllegalStateException("Test the connection first!");
        return LAST_FOUND_NAMESPACE.getPrefix() + LAST_FOUND_NAMESPACE.getDayNameTableName();
    }

    static String getIntentionTableName() {
        if (LAST_FOUND_NAMESPACE == null) throw new IllegalStateException("Test the connection first!");
        return LAST_FOUND_NAMESPACE.getPrefix() + LAST_FOUND_NAMESPACE.getIntentionTableName();
    }
}
