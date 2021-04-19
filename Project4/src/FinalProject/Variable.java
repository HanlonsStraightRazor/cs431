package FinalProject;

enum Type {
    INT, REAL, STRING, BOOLEAN, VOID
}

class Variable {
    private Type type;
    private Object value;
    public Variable(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
}
