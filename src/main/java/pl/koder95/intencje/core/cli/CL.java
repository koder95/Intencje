package pl.koder95.intencje.core.cli;

import java.util.*;

public class CL {

    private CL() {}

    private final Queue<Option> options = new LinkedList<>();

    public static CL capture(String[] args) {
        if (args == null || args.length == 0) {
            return new CL();
        }
        return new CL().load(args);
    }

    public CL load(String[] args) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(args));
        Stack<Object> memory = new Stack<>();

        while (!queue.isEmpty()) {
            String str = queue.poll();
            if (str.startsWith("-")) {
                Variable var = new Variable(str.substring(1));
                memory.push(var);
            } else {
                if (memory.empty()) memory.push(new Option(str));
                else {
                    Object top = memory.pop();
                    if (top instanceof Option) {
                        memory.push(new Option(str));
                    } else if (top instanceof Variable var) {
                        if (var.getValue() == null) {
                            var.setValue(str);
                            memory.push(var);
                        } else {
                            memory.push(var);
                            memory.push(new Option(str));
                        }
                    } else throw new IllegalStateException("Unknown object in parser memory. " + top);
                }
            }
        }
        while (!memory.empty()) {
            Object top = memory.pop();
            if (top instanceof Option) options.offer((Option) top);
            else if (top instanceof Variable) {
                Deque<Variable> vars = new LinkedList<>();
                vars.offerLast((Variable) top);
                while (!memory.empty()) {
                    Object underTop = memory.pop();
                    if (underTop instanceof Option option) {
                        if (option.getVars() == null)
                            option.setVars(new ArrayList<>(vars));
                        else {
                            option.getVars().addAll(vars);
                        }
                        vars.clear();
                        options.offer(option);
                        break;
                    } else if (underTop instanceof Variable var) {
                        vars.offerFirst(var);
                    } else throw new IllegalStateException("Unknown object in parser memory. " + underTop);
                }
                if (!vars.isEmpty()) memory.push(new Option("", vars));
            } else throw new IllegalStateException("Unknown object in parser memory. " + top);
        }
        return this;
    }

    public CL service(OptionService service) {
        int i = 0;
        while (i < options.size()) {
            Option o = options.poll();
            if (!service.consume(o)) {
                options.offer(o);
                i++;
            }
        }
        return this;
    }
}
