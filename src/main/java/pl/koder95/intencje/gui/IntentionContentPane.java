package pl.koder95.intencje.gui;

import com.sun.webkit.dom.HTMLBodyElementImpl;
import com.sun.webkit.dom.NodeListImpl;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import org.jsoup.Jsoup;
import org.w3c.dom.html.HTMLBodyElement;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;

import java.util.Objects;

public class IntentionContentPane {

    @FXML
    private Tab editorTab;
    @FXML
    private TextArea code;
    @FXML
    private HTMLEditor editor;

    private final StringProperty html = new SimpleStringProperty(this, "html");

    public void initialize() {
        StringBinding editorHtmlTextValue = Bindings.createStringBinding(() -> editor.getHtmlText());
        editor.addEventHandler(InputEvent.ANY, event -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_CLICKED) || event.getEventType().equals(KeyEvent.KEY_RELEASED)) {
                if (editorHtmlTextValue.isValid()) {
                    editorHtmlTextValue.invalidate();
                }
            }
        });

        html.bindBidirectional(code.textProperty());
        editorHtmlTextValue.when(editor.focusWithinProperty()).subscribe(s -> html.set(Jsoup.parse(s).body().html()));
        code.textProperty().when(code.focusedProperty().or(editor.sceneProperty().isNull())).subscribe(s -> editor.setHtmlText(s));

        TabPane editorTabPane = editorTab.getTabPane();
        SingleSelectionModel<Tab> selectionModel = editorTabPane.getSelectionModel();
        selectionModel.select(editorTab);

        Platform.runLater(() -> {
            editor.lookupAll(".tool-bar").forEach(node -> {
                if (node instanceof ToolBar toolBar) {
                    toolBar.getItems().removeIf(n -> {
                        boolean toRemove = !(n instanceof ButtonBase);
                        toRemove |= n.getStyleClass().contains("html-editor-bullets");
                        toRemove |= n.getStyleClass().contains("html-editor-underline");
                        toRemove |= n.getStyleClass().contains("html-editor-strike");
                        toRemove |= n.getStyleClass().contains("html-editor-hr");
                        toRemove |= n.getStyleClass().stream().anyMatch(styleClass -> styleClass.startsWith("html-editor-align"));
                        toRemove |= n.getStyleClass().contains("html-editor-outdent");
                        toRemove |= n.getStyleClass().contains("html-editor-indent");
                        return toRemove;
                    });
                    toolBar.getItems().stream().map(n -> n instanceof ButtonBase button ? button : null)
                            .filter(Objects::nonNull).forEach(button -> {
                        EventHandler<ActionEvent> onAction = button.getOnAction();
                        button.setOnAction(event -> {
                            if (onAction != null) onAction.handle(event);
                            if (editorHtmlTextValue.isValid()) {
                                editorHtmlTextValue.invalidate();
                            }
                        });
                    });
                }
            });
            editor.getChildrenUnmodifiable().forEach(c -> {
                System.out.println("Child: " + c);
                if (c instanceof Parent p) {
                    p.getChildrenUnmodifiable().forEach(c1 -> System.out.println("– Child: " + c1));
                }
            });
        });
    }

    public StringProperty htmlProperty() {
        return html;
    }

    public String getHtml() {
        return htmlProperty().getValue();
    }

    public void setHtml(String html) {
        htmlProperty().setValue(html);
    }
}
