package FinalProject;

class Array extends Symbol {
    private String type;
    private int size;
    public boolean[] initialized;
    public Array(String type, int size) {
        this.type = type;
        this.size = size;
        initialized = new boolean[size];
        for (boolean b : initialized) {
            b = false;
        }
    }
    public String getType() {
        return type;
    }
    public int getSize() {
        return size;
    }
    public boolean isInitializedAt(int index) {
        return initialized[index];
    }
    public void initializeAt(int index) {
        initialized[index] = true;
    }
}
