package FinalProject;

import java.util.HashMap;

class GlobalSet {
    private String currentClassName;
    private String currentFunctionName;
    private Class currentClass;
    private Function currentFunction;
    private HashMap<String, Class> classes;
    private HashMap<String, Function> functions;

    public GlobalSet() {
        currentClassName = "";
        currentFunctionName = "";

        currentClass = null;
        currentFunction = null;

        classes = new HashMap<>();
        functions = new HashMap<>();
    }

    public void setCurrentClass(String id) {
        currentClass = getClass(id);
        if (currentClass != null) {
            currentClassName = id;
        }
    }

    public void clearCurrentClass() {
        currentClassName = "";
        currentClass = null;
    }

    public String getCurrentClassName() {
        return currentClassName;
    }

    public Class getCurrentClass() {
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
        if (currentFunction != null) {
            currentFunctionName = id;
        }
    }

    public void clearCurrentFunction() {
        currentFunctionName = "";
        currentFunction = null;
    }

    public String getCurrentFunctionName() {
        return currentFunctionName;
    }

    public Function getCurrentFunction() {
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
}
