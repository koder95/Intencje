package pl.koder95.intencje.core.db;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DayName implements pl.koder95.intencje.core.DayName {

    private static final String TABLE_NAME = DB.getDayNameTableName();
    private LocalDate date;

    private DayName(LocalDate date) {
        this.date = date;
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `data` FROM `" + TABLE_NAME +
                    "` WHERE `data` = ?");
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet results = pstmt.executeQuery();
            if (results.first()) throw new SQLException("Nie można zmienić daty dla nazwy dnia. Do wprowadzonego dnia" +
                    " jest już przypisana nazwa. Najpierw zmień ją albo usuń.");
            else {
                pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                        "SET `data` = ? WHERE `data` = ?");
                pstmt.setDate(1, Date.valueOf(date));
                pstmt.setDate(2, Date.valueOf(getDate()));
                pstmt.execute();
                results = pstmt.getResultSet();
                if (results.first()) {
                    int count = results.getInt(0);
                    System.out.println("Updated rows: " + count);
                    if (count == 0) return;
                }
                this.date = date;
            }
        }
    }

    @Override
    public String getName() throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `nazwa` FROM `" + TABLE_NAME +
                    "` WHERE `data` = ?");
            pstmt.setDate(1, Date.valueOf(getDate()));
            ResultSet results = pstmt.executeQuery();
            return results.first() ? results.getString("nazwa") : null;
        }
    }

    @Override
    public void setName(String name) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                    "SET `nazwa` = ? WHERE `data` = ?");
            pstmt.setString(1, name);
            pstmt.setDate(2, Date.valueOf(getDate()));
            pstmt.execute();
            ResultSet results = pstmt.getResultSet();
            if (results.first()) {
                int count = results.getInt(0);
                System.out.println("Updated rows: " + count);
            }
        }
    }

    public void sync(pl.koder95.intencje.core.DayName i) throws Exception {
        if (i == null) return;
        if (!i.getDate().equals(getDate())) {
            setDate(i.getDate());
        }
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                    "SET `nazwa` = ? WHERE `data` = ?");
            pstmt.setString(1, i.getName());
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.execute();
            ResultSet results = pstmt.getResultSet();
            if (results.first()) {
                int count = results.getInt(0);
                System.out.println("Sync rows: " + count);
            }
        }
    }

    public static DayName create(LocalDate date, String name) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `" + TABLE_NAME +
                    "` (`data`, `nazwa`) VALUES (?, ?)");
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setString(2, name);
            pstmt.execute();
            return get(date);
        }
    }

    public static DayName get(LocalDate date) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `data` FROM `" + TABLE_NAME +
                    "` WHERE `data` = ?");
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet results = pstmt.executeQuery();
            return results.first() ? new DayName(results.getDate("data").toLocalDate())
                    : null;
        }
    }

    public static List<pl.koder95.intencje.core.DayName> load(LocalDate beginDate, LocalDate endDate)
            throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `data` FROM `" + TABLE_NAME + "` " +
                    "WHERE `data` >= ? AND `data` <= ?");
            pstmt.setDate(1, Date.valueOf(beginDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet results = pstmt.executeQuery();
            LinkedList<pl.koder95.intencje.core.DayName> loaded = new LinkedList<>();
            while (results.next()) {
                loaded.add(new DayName(results.getDate("data").toLocalDate()));
            }
            List<pl.koder95.intencje.core.DayName> result = new ArrayList<>(loaded);
            loaded.clear();
            return result;
        }
    }

    public static List<pl.koder95.intencje.core.DayName> loadAll() throws SQLException {
        try (Connection conn = DB.conn()) {
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT `data` FROM `" + TABLE_NAME + "`");
            LinkedList<pl.koder95.intencje.core.DayName> loaded = new LinkedList<>();
            while (results.next()) {
                loaded.add(new DayName(results.getDate("data").toLocalDate()));
            }
            List<pl.koder95.intencje.core.DayName> result = new ArrayList<>(loaded);
            loaded.clear();
            return result;
        }
    }
}
