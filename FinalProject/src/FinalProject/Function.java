package FinalProject;
import java.util.*;

class Function {
    private String label;
    private String returnType;
    private ArrayList<Symbol> symbols;
    private ArrayList<String> symbolsNames;

    public Function(String label, String returnType) {
        this.label = label;
        this.returnType = returnType;
        symbols = new ArrayList<Symbol>();
        symbolsNames = new ArrayList<String>();
    }

    public Function(String label, String returnType, ArrayList<Symbol> symbols, ArrayList<String> symbolsNames) {
        this.label = label;
        this.returnType = returnType;
        this.symbols = symbols;
        this.symbolsNames = symbolsNames;
    }

    public int getSize() {
        return symbols.size();
    }

    public String getLabel() {
        return label;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getSymbolName(int num) {
        return symbolsNames.get(num);
    }

    public Symbol getSymbol(int num) {
        return symbols.get(num);
    }

    public void setSymbols(ArrayList<String> symbolsNames, ArrayList<Symbol> symbols){
        this.symbolsNames = symbolsNames;
        this.symbols = symbols;
    }

    public void addSymbol(String name, Symbol symb){
        symbolsNames.add(name);
        symbols.add(symb);
    }
}
