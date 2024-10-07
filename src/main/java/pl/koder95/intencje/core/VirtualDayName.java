package pl.koder95.intencje.core;

import pl.koder95.intencje.core.db.DayName;

import java.time.LocalDate;

public class VirtualDayName implements pl.koder95.intencje.core.DayName {

    private LocalDate date;
    private String name;

    public VirtualDayName(LocalDate date, String name) {
        this.date = date;
        this.name = name;
    }

    public VirtualDayName(LocalDate date) {
        this(date, "");
    }

    public VirtualDayName() {
        this(LocalDate.now().plusDays(1));
    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public RealDayName toReal() throws Exception {
        return new RealDayName(DayName.create(getDate(), getName()));
    }
}
