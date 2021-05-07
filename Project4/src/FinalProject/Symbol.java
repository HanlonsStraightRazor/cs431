package FinalProject;

abstract class Symbol {
    private String type;
    private int offset;
    public Symbol(String type, int offset) {
        this.type = type;
        this.offset = offset;
    }
    public String getType() {
        return type;
    }
    public int getOffset() {
        return offset;
    }
}
