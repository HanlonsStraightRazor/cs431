package FinalProject;

class Function {
    private String label;
    private String returnType;
    private Symbol[] symbols;

    public Function(String label, String returnType, Symbol[] symbols) {
        this.label = label;
        this.returnType = returnType;
        this.symbols = symbols;
    }

    public String getLabel() {
        return label;
    }

    public String getReturnType() {
        return returnType;
    }

    public Symbol[] getSymbols() {
        return symbols;
    }
}
