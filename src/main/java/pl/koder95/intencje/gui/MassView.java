package pl.koder95.intencje.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import pl.koder95.intencje.gui.model.MassInfo;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MassView {

    @FXML
    private Label time;
    @FXML
    private Label chapel;
    @FXML
    private WebView content;

    private final ObjectProperty<MassInfo> massInfo = new SimpleObjectProperty<>();

    public void initialize() {
        massInfo.set(new MassInfo());

        time.textProperty().bind(getMassInfo().timeProperty().map(t -> t.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))));
        chapel.textProperty().bind(getMassInfo().chapelProperty());
        getMassInfo().intentionsProperty().subscribe(s -> content.getEngine().loadContent(s));
    }

    public MassInfo getMassInfo() {
        return massInfo.get();
    }

    public ObjectProperty<MassInfo> massInfoProperty() {
        return massInfo;
    }

    public void setMassInfo(MassInfo massInfo) {
        this.massInfo.set(massInfo);
    }

    public void edit(ActionEvent event) {
        MassEditor.edit(getMassInfo());
    }
}
