package pl.koder95.intencje.core;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pl.koder95.intencje.core.db.Intention;

import java.time.LocalDateTime;

public class VirtualIntention implements pl.koder95.intencje.core.Intention {

    private final ObjectProperty<LocalDateTime> massTime;
    private final StringProperty chapel;
    private final StringProperty content;

    public VirtualIntention(LocalDateTime massTime, String chapel, String content) {
        this.massTime = new SimpleObjectProperty<>(massTime);
        this.chapel = new SimpleStringProperty(chapel);
        this.content = new SimpleStringProperty(content);
    }

    public VirtualIntention(LocalDateTime massTime, String content) {
        this.massTime = new SimpleObjectProperty<>(massTime);
        this.chapel = new SimpleStringProperty();
        this.content = new SimpleStringProperty(content);
    }

    public VirtualIntention(LocalDateTime massTime) {
        this.massTime = new SimpleObjectProperty<>(massTime);
        this.chapel = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
    }

    public VirtualIntention() {
        this.massTime = new SimpleObjectProperty<>();
        this.chapel = new SimpleStringProperty();
        this.content = new SimpleStringProperty();
    }

    @Override
    public LocalDateTime getMassTime() {
        return massTime.get();
    }

    public ObjectProperty<LocalDateTime> massTimeProperty() {
        return massTime;
    }

    public void setMassTime(LocalDateTime massTime) {
        this.massTime.set(massTime);
    }

    @Override
    public String getChapel() {
        return chapel.get();
    }

    public StringProperty chapelProperty() {
        return chapel;
    }

    public void setChapel(String chapel) {
        this.chapel.set(chapel);
    }

    @Override
    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public RealIntention toReal() throws Exception {
        return new RealIntention(Intention.create(getMassTime(), getChapel(), getContent()));
    }
}
