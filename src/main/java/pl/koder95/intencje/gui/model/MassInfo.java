package pl.koder95.intencje.gui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalTime;

public class MassInfo {

    private final ObjectProperty<LocalTime> time = new SimpleObjectProperty<>(LocalTime.now());
    private final StringProperty chapel = new SimpleStringProperty("");
    private final StringProperty intentions = new SimpleStringProperty("za parafian");

    public LocalTime getTime() {
        return time.get();
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    public String getChapel() {
        return chapel.get();
    }

    public StringProperty chapelProperty() {
        return chapel;
    }

    public void setChapel(String chapel) {
        this.chapel.set(chapel);
    }

    public String getIntentions() {
        return intentions.get();
    }

    public StringProperty intentionsProperty() {
        return intentions;
    }

    public void setIntentions(String intentions) {
        this.intentions.set(intentions);
    }
}
