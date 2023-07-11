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
    private static Connection CONN = null;
    private DB() {}

    private static String url() {
        return "jdbc:" + CONN_PROP.getProperty("driver") + "://"
                + CONN_PROP.getProperty("hostname") + "/" + CONN_PROP.getProperty("dbName");
    }

    static Connection conn() throws SQLException {
        if (CONN == null) {
            CONN = DriverManager.getConnection(url(), CONN_PROP.getProperty("user"), CONN_PROP.getProperty("password"));
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
                String tableName = resultSet.getString(1);
                if (tableName.startsWith(getTablePrefix())) {
                    tables.add(tableName);
                }
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

    public static boolean test() {
        try {
            return !tables().isEmpty();
        } catch (SQLException e) {
            return false;
        }
    }
}
