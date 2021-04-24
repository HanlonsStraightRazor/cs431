package FinalProject;

class Array {
    private String type;
    private Object[] values;
    private int size;
    public Array(String type, int size) {
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
}
