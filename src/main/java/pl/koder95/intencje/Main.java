package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;
import pl.koder95.intencje.gui.MainFrame;

import javax.swing.*;

public class Main {

    public static final String PARISH_NAME = "Parafia rzymskokatolicka...";
    public static final String APP_NAME = "Intencje";
    public static final String APP_HEADER = "Intencje – " + PARISH_NAME;

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        setupLookAndFeel();
		
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    private static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
