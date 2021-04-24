package FinalProject;

class Array {
    private String type;
    private Object[] values;
    private int size;
    private int offset;
    public Array(String type, int size, int offset) {
        this.type = type;
        this.values = new Object[size];
        this.size = size;
        this.offset = offset;
    }
    public String getType() {
        return type;
    }
    public Object[] getValues() {
        return values;
    }
    public Object getValueAt(int index) {
        return values[index];
    }
    public void setValueAt(int index, Object value) {
        values[index] = value;
    }
    public int getOffset() {
        return offset;
    }
    public int getSize() {
        return size;
    }
}
