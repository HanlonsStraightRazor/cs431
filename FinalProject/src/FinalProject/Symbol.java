package FinalProject;

abstract class Symbol {
    private String type;
    public Symbol(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
