package pl.koder95.intencje.gui;

import com.dlsc.gemsfx.TimePicker;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.time.LocalTime;

public class MassEditorPane {

    @FXML
    private IntentionContentPane contentController;
    @FXML
    private TabPane content;
    @FXML
    private TimePicker mass;
    @FXML
    private CheckBox chapelCheck;
    @FXML
    private TextField chapel;

    private final StringProperty contentProperty = new SimpleStringProperty();

    public void initialize() {
        chapel.disableProperty().bind(chapelCheck.selectedProperty().not());
        chapelCheck.selectedProperty().subscribe(selected -> {
            if (!selected) chapel.setText("");
        });
        chapel.textProperty().isEmpty().subscribe(notEmpty -> {
            chapelCheck.setSelected(false);
        });
        contentProperty.bindBidirectional(contentController.htmlProperty());
    }

    public ObjectProperty<LocalTime> massTimeProperty() {
        return mass.timeProperty();
    }

    public StringProperty contentProperty() {
        return contentProperty;
    }

    public StringProperty chapelProperty() {
        return chapel.textProperty();
    }

    public void setTimePickerDisable(boolean timePickerDisable) {
        timePickerDisableProperty().set(timePickerDisable);
    }

    public boolean isTimePickerDisable() {
        return timePickerDisableProperty().get();
    }

    public BooleanProperty timePickerDisableProperty() {
        return mass.disableProperty();
    }
}
