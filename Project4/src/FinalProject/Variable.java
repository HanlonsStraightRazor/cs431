package FinalProject;

/*
enum Type {
    INT, REAL, STRING, BOOLEAN, VOID
}
*/

class Variable {
    private String type;
    private Object value;
    private int offset;
    public Variable(String type, Object value, int offset) {
        this.type = type;
        this.value = value;
        this.offset = offset;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
