package pl.koder95.intencje;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.koder95.intencje.core.cli.CL;

public class Main extends Application {

    public static final String APP_NAME = "Intencje mszalne";

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.show();
    }

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        launch(args);
    }
}
