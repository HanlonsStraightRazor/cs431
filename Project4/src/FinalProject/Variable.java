package FinalProject;

class Variable extends Symbol {
    private String type;
    private Object value;
    private int offset;
    public Variable(String type, int offset) {
        this.type = type;
        this.value = null;
        this.offset = offset;
    }
    public String getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public int getOffset() {
        return offset;
    }
}
