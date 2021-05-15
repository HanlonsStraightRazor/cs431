package FinalProject;

import java.util.HashMap;

class Class {
    private String currentMethodName;
    private Function currentMethod;

    private HashMap<String, Function> methods;
    private HashMap<String, Symbol> symbols;

    public Class() {
        currentMethodName = "";
        currentMethod = null;

        methods = new HashMap<>();
        symbols = new HashMap<>();
    }

    public void setCurrentMethod(String id) {
        currentMethod = getMethod(id);
        if (currentMethod != null) {
            currentMethodName = id;
        }
    }

    public void clearCurrentMethod() {
        currentMethodName = "";
        currentMethod = null;
    }

    public String getCurrentMethodName() {
        return currentMethodName;
    }

    public Function getCurrentMethod() {
        return currentMethod;
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
