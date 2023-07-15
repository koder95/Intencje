package pl.koder95.intencje.gui;

import pl.koder95.intencje.core.DayName;

import javax.swing.*;
import java.beans.*;
import java.time.LocalDate;

public class DayNameModel implements DayName {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private final VetoableChangeSupport vetoableChangeSupport = new VetoableChangeSupport(this);
    private DayName value;

    public DayNameModel(JLabel label) {
        addPropertyChangeListener("name", evt -> updateText(label));
        addPropertyChangeListener("value", evt -> updateText(label));
    }

    public DayName getValue() {
        return value;
    }

    public void setValue(DayName value) throws PropertyVetoException {
        DayName old = this.value;
        vetoableChangeSupport.fireVetoableChange("value", old, value);
        this.value = value;
        propertyChangeSupport.firePropertyChange("value", old, value);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    public void addVetoableChangeListener(String propertyName, VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(propertyName, listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    @Override
    public LocalDate getDate() throws Exception {
        return value == null? null : value.getDate();
    }

    @Override
    public void setDate(LocalDate date) throws Exception {
        LocalDate old = getDate();
        vetoableChangeSupport.fireVetoableChange("date", old, date);
        value.setDate(date);
        propertyChangeSupport.firePropertyChange("date", old, date);
    }

    @Override
    public String getName() throws Exception {
        return value == null? null : value.getName();
    }

    @Override
    public void setName(String name) throws Exception {
        String old = getName();
        vetoableChangeSupport.fireVetoableChange("name", old, name);
        value.setName(name);
        propertyChangeSupport.firePropertyChange("name", old, name);
    }

    private void updateText(JLabel label) {
        String text = "- brak -";
        if (getValue() != null) {
            try {
                String name = getName();
                if (name != null && !name.isEmpty())
                    text = name;
            } catch (Exception e) {
                text = "błąd (" + e.getClass().getTypeName() + "): " + e.getLocalizedMessage();
            }
        }
        label.setText(text);
    }
}
