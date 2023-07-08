package pl.koder95.intencje.core.cli;

import java.util.Objects;

class Variable {

    private final String name;
    private String value;

    public Variable(String name) {
        this(name, null);
    }

    public Variable(String name, String value) {
        this.name = Objects.requireNonNull(name);
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
