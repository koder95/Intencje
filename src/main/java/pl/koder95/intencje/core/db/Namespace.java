package pl.koder95.intencje.core.db;

import java.util.Objects;

public class Namespace {

    public static final Namespace UNKNOWN = new Namespace("", "", "");

    public static Namespace instanceOf(String prefix, String intentionTableName, String dayNameTableName) {
        if (prefix == null) prefix = "";
        if (intentionTableName == null || dayNameTableName == null || intentionTableName.isEmpty() || dayNameTableName.isEmpty()) {
            return UNKNOWN;
        }
        return new Namespace(prefix, intentionTableName, dayNameTableName);
    }

    private final String prefix, intentionTableName, dayNameTableName;

    private Namespace(String prefix, String intentionTableName, String dayNameTableName) {
        this.prefix = Objects.requireNonNull(prefix);
        this.intentionTableName = Objects.requireNonNull(intentionTableName);
        this.dayNameTableName = Objects.requireNonNull(dayNameTableName);
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

    @Override
    public String toString() {
        return "Namespace{" +
                "prefix='" + prefix + '\'' +
                ", intentionTableName='" + intentionTableName + '\'' +
                ", dayNameTableName='" + dayNameTableName + '\'' +
                '}';
    }
}
