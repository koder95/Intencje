package pl.koder95.intencje.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.koder95.intencje.gui.model.MassInfo;

import java.net.URL;
import java.time.LocalTime;

class MassEditor extends Stage {

    private static final URL FXML_RES_URL = ClassLoader.getSystemResource("pl/koder95/intencje/gui/MassEditorPane.fxml");
    private static MassEditor INSTANCE;

    private static MassEditor get() {
        if (INSTANCE == null) INSTANCE = new MassEditor();
        return INSTANCE;
    }

    public static void edit(MassInfo info, boolean timeDisable) {
        get().showAndEdit(info, timeDisable);
    }

    public static void edit(MassInfo info) {
        edit(info, false);
    }

    private final MassEditorPane controller;

    private MassEditor() {
        setTitle("Edytor intencji Mszy świętej");
        FXMLLoader loader = new FXMLLoader(FXML_RES_URL);
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            setScene(scene);
            controller = loader.getController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized void showAndEdit(MassInfo result, boolean timeDisable) {
        ObjectProperty<LocalTime> time = result.timeProperty();
        StringProperty chapel = result.chapelProperty();
        StringProperty content = result.intentionsProperty();
        controller.setTimePickerDisable(timeDisable);
        controller.massTimeProperty().set(time.get());
        controller.chapelProperty().set(chapel.get());
        controller.contentProperty().set(content.get());
        time.bind(controller.massTimeProperty());
        chapel.bind(controller.chapelProperty());
        content.bind(controller.contentProperty());
        showAndWait();
        time.unbind();
        chapel.unbind();
        content.unbind();
    }
}
