package FinalProject;

import java.util.HashMap;

class GlobalSet {
    private Class currentClass;
    private Function currentFunction;
    private boolean mainMethod;
    private HashMap<String, Class> classes;
    private HashMap<String, Function> functions;

    public GlobalSet() {
        currentClass = null;
        currentFunction = null;
        mainMethod = false;

        classes = new HashMap<>();
        functions = new HashMap<>();
    }

    public void setCurrentClass(String id) {
        currentClass = getClass(id);
    }

    public void clearCurrentClass() {
        currentClass = null;
    }

    public Class getCurrentClass(String id) {
        return currentClass;
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

    public void setCurrentFunction(String id) {
        currentFunction = getFunction(id);
    }

    public void clearCurrentFunction() {
        currentFunction = null;
    }

    public Function getCurrentFunction(String id) {
        return currentFunction;
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

    public void mainMethodCalled() {
        mainMethod = true;
    }

    public boolean hasMainMethodDecl() {
        return mainMethod;
    }
}
