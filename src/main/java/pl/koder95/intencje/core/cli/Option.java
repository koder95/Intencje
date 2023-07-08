package pl.koder95.intencje.core.cli;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

public class Option {

    private final String name;
    private Collection<Variable> vars;

    public Option(String name, Collection<Variable> vars) {
        this.name = Objects.requireNonNull(name);
        this.vars = vars;
    }

    public Option(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public Collection<Variable> getVars() {
        return vars;
    }

    public Variable getVar(String name) {
        return vars.stream().reduce(null, (r, c) -> c.getName().equalsIgnoreCase(name)? c : r);
    }

    public void setVars(Collection<Variable> vars) {
        this.vars = vars;
    }

    public Properties getVarsAsProperties() {
        Properties properties = new Properties();
        getVars().stream().forEach(var -> properties.put(var.getName(), var.getValue()));
        return properties;
    }
}
