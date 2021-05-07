package FinalProject;

class Variable extends Symbol {
    private boolean initialized;
    public Variable(String type, int offset) {
        super(type, offset);
        initialized = false;
    }
    public boolean isInitialized() {
        return initialized;
    }
    public void initialize() {
        initialized = true;
    }
}
