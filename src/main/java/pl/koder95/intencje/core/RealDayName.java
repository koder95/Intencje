package pl.koder95.intencje.core;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class RealDayName extends VirtualDayName {

    private final pl.koder95.intencje.core.db.DayName dbInstance;

    public RealDayName(pl.koder95.intencje.core.db.DayName dbInstance) throws Exception {
        super(dbInstance.getDate(), dbInstance.getName());
        this.dbInstance = dbInstance;
    }

    /**
     * Save data in database.
     */
    public void sync() throws Exception {
        dbInstance.sync(this);
        update();
    }

    /**
     * Load data from database.
     */
    public void update() throws Exception {
        setDate(dbInstance.getDate());
        setName(dbInstance.getName());
    }

    @Override
    public RealDayName toReal() {
        return this;
    }

    public static List<RealDayName> loadAll() throws Exception {
        List<pl.koder95.intencje.core.DayName> loaded = pl.koder95.intencje.core.db.DayName.loadAll();
        for (int i = 0; i < loaded.size(); i++) {
            pl.koder95.intencje.core.DayName e = loaded.get(i);
            if (e instanceof pl.koder95.intencje.core.db.DayName) {
                loaded.set(i, new RealDayName((pl.koder95.intencje.core.db.DayName) e));
            }
        }
        List<RealDayName> result = loaded.stream().filter(dayName -> dayName instanceof RealDayName)
                .map(dayName -> (RealDayName) dayName).collect(Collectors.toList());
        loaded.clear();
        return result;
    }

    public static RealDayName load(LocalDate date) throws Exception {
        pl.koder95.intencje.core.db.DayName db = pl.koder95.intencje.core.db.DayName.get(date);
        if (db != null) {
            RealDayName result = new RealDayName(db);
            result.update();
            return result;
        }
        return null;
    }
}
