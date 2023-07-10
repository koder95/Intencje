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

    public boolean containsVar(String name) {
        return vars.stream().anyMatch(var -> var.getName().equals(name));
    }

    public Variable getVar(String name) {
        return vars.stream().reduce(null, (r, c) -> c.getName().equals(name)? c : r);
    }

    public Variable popVar(String name) {
        Variable var = getVar(name);
        vars.remove(var);
        return var;
    }

    public void setVars(Collection<Variable> vars) {
        this.vars = vars;
    }

    public Properties getVarsAsProperties() {
        Properties properties = new Properties();
        getVars().stream().peek(var -> {
            if (var.getValue() == null) {
                var.setValue("true");
            }
        }).forEach(var -> properties.setProperty(var.getName(), var.getValue()));
        return properties;
    }
}
