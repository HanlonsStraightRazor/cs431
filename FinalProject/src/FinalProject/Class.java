package FinalProject;

import java.util.HashMap;

class Class {
    private HashMap<String, Function> methods;
    private HashMap<String, Symbol> symbols;

    public Class() {
        methods = new HashMap<>();
        symbols = new HashMap<>();
    }

    public void addMethod(String id, Function f) {
        methods.put(id, f);
    }

    public boolean containsMethod(String id) {
        return methods.containsKey(id);
    }

    public Function getMethod(String id) {
        return methods.get(id);
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
