package FinalProject;

import java.util.HashMap;

class GlobalSet {
    private HashMap<String, Class> classes;
    private HashMap<String, Function> functions;
    private HashMap<String, Symbol> symbols;

    public GlobalSet() {
        classes = new HashMap<>();
        functions = new HashMap<>();
        symbols = new HashMap<>();
    }

    public void addClass(String id, Class c) {
        classes.put(id, c);
    }

    public boolean containsClass(String id) {
        return classes.containsKey(id);
    }

    public Class getClass(String id) {
        return classes.get(id);
    }

    public void addFunction(String id, Function f) {
        functions.put(id, f);
    }

    public boolean containsFunction(String id) {
        return functions.containsKey(id);
    }

    public Function getFunction(String id) {
        return functions.get(id);
    }

    public void addSymbol(String id, Symbol s) {
        symbols.put(id, s);
    }

    public boolean containsSymbol(String id) {
        return symbols.containsKey(id);
    }

    public Symbol getSymbol(String id) {
        return symbols.get(id);
    }
}
