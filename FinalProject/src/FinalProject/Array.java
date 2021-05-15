package FinalProject;

class Array extends Symbol {
    private int size;
    private boolean[] initialized;
    public Array(String type, int offset, int size) {
        super(type, offset);
        this.size = size;
        initialized = new boolean[size];
        for (boolean b : initialized) {
            b = false;
        }
    }
    public boolean isOutOfBounds(int index) {
        return index < 0 || index >= size;
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
