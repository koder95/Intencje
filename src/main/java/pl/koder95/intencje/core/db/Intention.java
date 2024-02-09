package pl.koder95.intencje.core.db;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Intention implements pl.koder95.intencje.core.Intention {

    private static final String TABLE_NAME = DB.getIntentionTableName();
    private LocalDateTime massTime;

    private Intention(LocalDateTime massTime) {
        this.massTime = massTime;
    }

    private Intention(ResultSet resultSet) throws SQLException {
        this(resultSet.getTimestamp("msza").toLocalDateTime());
    }

    @Override
    public LocalDateTime getMassTime() {
        return massTime;
    }

    @Override
    public void setMassTime(LocalDateTime massTime) throws SQLException {
        if (!isAlive())
            throw new SQLException("Nie można zmienić czasu odprawienia Mszy, ponieważ ten obiekt jest martwy. " +
                    "Należy stworzyć nowy obiekt.");
        if (exists(massTime)) throw new SQLException("Nie można zmienić czasu odprawienia Mszy. W tym samym czasie" +
                " jest już zapisana intencja. Najpierw ją usuń.");
        else {
            try (Connection conn = DB.conn()) {
                PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                        "SET `msza` = ? WHERE `msza` = ?");
                pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
                pstmt.setTimestamp(2, Timestamp.valueOf(this.massTime));
                pstmt.execute();
                results = pstmt.getResultSet();
                if (results.next()) {
                    int count = results.getInt(0);
                    System.out.println("Updated rows: " + count);
                    if (count == 0) return;
                }
                this.massTime = Objects.requireNonNull(massTime);
            }
        }
    }

    @Override
    public String getChapel() throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `kaplica` FROM `" + TABLE_NAME +
                    "` WHERE `msza` = ? LIMIT 1");
            pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
            ResultSet results = pstmt.executeQuery();
            return results.next()? results.getString("kaplica") : null;
        }
    }

    @Override
    public void setChapel(String chapel) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                    "SET `kaplica` = ? WHERE `msza` = ?");
            pstmt.setString(1, chapel);
            pstmt.setTimestamp(2, Timestamp.valueOf(massTime));
            pstmt.execute();
            ResultSet results = pstmt.getResultSet();
            if (results.next()) {
                int count = results.getInt(0);
                System.out.println("Updated rows: " + count);
            }
        }
    }

    @Override
    public String getContent() throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `intencja` FROM `" + TABLE_NAME +
                    "` WHERE `msza` = ? LIMIT 1");
            pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
            ResultSet results = pstmt.executeQuery();
            return results.next()? results.getString("intencja") : null;
        }
    }

    @Override
    public void setContent(String content) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                    "SET `kaplica` = ?, `intencja` = ? WHERE `msza` = ?");
            pstmt.setString(1, content);
            pstmt.setTimestamp(2, Timestamp.valueOf(massTime));
            pstmt.execute();
            ResultSet results = pstmt.getResultSet();
            if (results.first()) {
                int count = results.getInt(0);
                System.out.println("Updated rows: " + count);
            }
        }
    }

    public void sync(pl.koder95.intencje.core.Intention i) throws Exception {
        if (!i.getMassTime().equals(getMassTime())) {
            setMassTime(i.getMassTime());
        }
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("UPDATE `" + TABLE_NAME + "` " +
                    "SET `kaplica` = ?, `intencja` = ? WHERE `msza` = ?");
            pstmt.setString(1, i.getChapel());
            pstmt.setString(2, i.getContent());
            pstmt.setTimestamp(3, Timestamp.valueOf(massTime));
            pstmt.execute();
            ResultSet results = pstmt.getResultSet();
            if (results.first()) {
                int count = results.getInt(0);
                System.out.println("Sync rows: " + count);
            }
        }
    }

    /**
     * Sprawdza, czy obiekt jest żywy, czyli czy posiada odniesienie do bazy danych. Nie ma takiego
     * odniesienia, jeżeli nie ma zapisanego czasu odprawiania Mszy. Bez tej informacji większość metod
     * z tej klasy wyrzuci wyjątek {@link NullPointerException}.
     *
     * @return {@code true} – możliwe jest korzystanie z obiektu, bez obawy o wyjątki typu
     * {@link NullPointerException}, obiekt zawiera odniesienie do zapisanych danych
     * @see #kill()
     */
    public boolean isAlive() {
        return this.massTime != null;
    }

    /**
     * Usuwa zapisane dane i odniesienie do nich, czyniąc przez to obiekt niezdolny do funkcjonowania.
     * Ta metoda powoduje usunięcie informacji i zaleca się najpierw zrobienie kopii zapasowej, w celu
     * możliwości odzyskania danych.
     * @throws SQLException nie można połączyć się z bazą danych lub usunięcie danych nie jest możliwe
     */
    public void kill() throws SQLException {
        if (isAlive()) {
            try (Connection conn = DB.conn()) {
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM `" + TABLE_NAME + "` " +
                        " WHERE `msza` = ?");
                pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
                pstmt.execute();
                ResultSet results = pstmt.getResultSet();
                if (results.first()) {
                    int count = results.getInt(0);
                    System.out.println("Delete rows: " + count);
                }
                massTime = null;
            }
        }
    }

    public static Intention create(LocalDateTime massTime, String chapel, String content) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt;
            if (chapel == null) {
                pstmt = conn.prepareStatement("INSERT INTO `" + TABLE_NAME +
                        "` (`msza`, `intencja`) VALUES (?, ?)");
                pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
                pstmt.setString(2, content);
            } else {
                pstmt = conn.prepareStatement("INSERT INTO `" + TABLE_NAME +
                        "` (`msza`, `kaplica`, `intencja`) VALUES (?, ?, ?)");
                pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
                pstmt.setString(2, chapel);
                pstmt.setString(3, content);
            }
            pstmt.execute();
            return get(massTime);
        }
    }

    public static Intention get(LocalDateTime massTime) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `msza` FROM `" + TABLE_NAME +
                    "` WHERE `msza` = ? LIMIT 1");
            pstmt.setTimestamp(1, Timestamp.valueOf(massTime));
            ResultSet results = pstmt.executeQuery();
            return results.next()? new Intention(results) : null;
        }
    }

    public static boolean exists(LocalDateTime massTime) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement ps = conn.prepareStatement("SELECT `msza` FROM `" + TABLE_NAME +
                    "` WHERE `msza` = ? LIMIT 1");
            ps.setTimestamp(1, Timestamp.valueOf(massTime));
            ResultSet results = ps.executeQuery();
            return results.next();
        }
    }

    public static List<pl.koder95.intencje.core.Intention> load(LocalDate date) throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `msza` FROM `" + TABLE_NAME + "` " +
                    "WHERE DATE(`msza`) = DATE(?)");
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet results = pstmt.executeQuery();
            LinkedList<pl.koder95.intencje.core.Intention> loaded = new LinkedList<>();
            while (results.next()) {
                loaded.add(new Intention(results));
            }
            List<pl.koder95.intencje.core.Intention> result = new ArrayList<>(loaded);
            loaded.clear();
            return result;
        }
    }

    public static List<pl.koder95.intencje.core.Intention> load(LocalDateTime beginMassTime, LocalDateTime endMassTime)
            throws SQLException {
        try (Connection conn = DB.conn()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT `msza` FROM `" + TABLE_NAME + "` " +
                    "WHERE `msza` >= ? AND `msza` <= ?");
            pstmt.setTimestamp(1, Timestamp.valueOf(beginMassTime));
            pstmt.setTimestamp(2, Timestamp.valueOf(endMassTime));
            ResultSet results = pstmt.executeQuery();
            LinkedList<pl.koder95.intencje.core.Intention> loaded = new LinkedList<>();
            while (results.next()) {
                loaded.add(new Intention(results));
            }
            List<pl.koder95.intencje.core.Intention> result = new ArrayList<>(loaded);
            loaded.clear();
            return result;
        }
    }

    public static List<pl.koder95.intencje.core.Intention> loadAll() throws SQLException {
        try (Connection conn = DB.conn()) {
            Statement stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("SELECT `msza` FROM `" + TABLE_NAME + "`");
            List<pl.koder95.intencje.core.Intention> loaded = new LinkedList<>();
            while (results.next()) {
                loaded.add(new Intention(results));
            }
            ArrayList<pl.koder95.intencje.core.Intention> result = new ArrayList<>(loaded);
            loaded.clear();
            return result;
        }
    }
}
