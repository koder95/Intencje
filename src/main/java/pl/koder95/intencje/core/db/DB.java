/*
 * Copyright (c) 2022.
 */

package pl.koder95.intencje.core.db;

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
        return "jdbc:" + CONN_PROP.getProperty("driver") + "://"
                + CONN_PROP.getProperty("hostname") + "/" + CONN_PROP.getProperty("dbName");
    }

    static Connection conn() throws SQLException {
        if (CONN == null) {
            CONN = DriverManager.getConnection(url(), CONN_PROP.getProperty("user"), CONN_PROP.getProperty("password"));
            TESTER = new ConnectionTester(DB.CONN_PROP.getProperty("hostname"), DEFAULT_COMMON_SEARCH, DEFAULT_DAY_NAME_ENDING);
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
        tester.test();
        LAST_TEST = tester.getTestResult();
        LAST_FOUND_NAMESPACE = tester.getDatabaseTableNamespace();
    }

    public static boolean test() {
        if (TESTER != null) test(TESTER);
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
