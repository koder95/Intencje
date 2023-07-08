package pl.koder95.intencje.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.koder95.intencje.core.db.DayName;

import java.time.LocalDate;

public class VirtualDayName implements pl.koder95.intencje.core.DayName {

    private final ObjectProperty<LocalDate> date;
    private final StringProperty name;

    public VirtualDayName(LocalDate date, String name) {
        this.date = new SimpleObjectProperty<>(date);
        this.name = new SimpleStringProperty(name);
    }

    public VirtualDayName(LocalDate date) {
        this.date = new SimpleObjectProperty<>(date);
        this.name = new SimpleStringProperty();
    }

    public VirtualDayName() {
        this.date = new SimpleObjectProperty<>();
        this.name = new SimpleStringProperty();
    }

    @Override
    public LocalDate getDate() {
        return date.get();
    }

    @Override
    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public void setName(String name) {
        this.name.set(name);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty nameProperty() {
        return name;
    }
}
