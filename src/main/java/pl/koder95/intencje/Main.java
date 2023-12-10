package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;

public class Main {

    public static final String PARISH_NAME = "Parafia rzymskokatolicka...";
    public static final String APP_NAME = "Intencje";
    public static final String APP_HEADER = "Intencje – " + PARISH_NAME;

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
        App.main(args);
    }
}
