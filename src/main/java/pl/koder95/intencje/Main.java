package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;
import javax.swing.*;

public class Main {

    public static final String APP_NAME = "Intencje mszalne";

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        setupLookAndFeel();
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
