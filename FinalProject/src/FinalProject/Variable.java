package FinalProject;

class Variable extends Symbol {
    private int offset;
    private boolean initialized;
    public Variable(String type, int offset) {
        super(type);
        this.offset = offset;
        initialized = false;
    }
    public int getOffset() {
        return offset;
    }
    public boolean isInitialized() {
        return initialized;
    }
    public void initialize() {
        initialized = true;
    }
}
