package FinalProject;

class Array extends Symbol {
    private int size;
    public boolean[] initialized;
    public Array(String type, int size) {
        super(type);
        this.size = size;
        initialized = new boolean[size];
        for (boolean b : initialized) {
            b = false;
        }
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
