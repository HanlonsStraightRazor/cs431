package FinalProject;

class Variable extends Symbol {
    private String type;
    private int offset;
    private boolean initialized;
    public Variable(String type, int offset) {
        this.type = type;
        this.offset = offset;
        initialized = false;
    }
    public String getType() {
        return type;
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
