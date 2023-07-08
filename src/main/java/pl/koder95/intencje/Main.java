package pl.koder95.intencje;

import pl.koder95.intencje.core.cli.CL;

public class Main {

    public static final String APP_NAME = "Intencje mszalne";

    public static void main(String[] args) {
        CL.capture(args).service(new ConfigCLI());
    }
}
