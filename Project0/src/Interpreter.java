package Starter;
import java.util.HashMap;
import java.util.ArrayList;


public class Interpreter{

    public static HashMap<String, Integer> symbolTable = new HashMap<>();

    public Interpreter() {
        symbolTable = new HashMap<>();
    }

    public int interpret(Stmts ss) {
        if (ss == null)
            return 0;
        else
            interpret(ss.s);
            if (ss.ss != null)
                interpret(ss.ss);
        return 0;
    }

    public int interpret(Stmt stm) {
        if (stm instanceof PrintStmt)
            return interpret((PrintStmt)stm);
        else if (stm instanceof AssignStmt)
            symbolTable.put(((AssignStmt) stm).id, interpret(((AssignStmt)stm).exp));
        return 0;
    }

    public int interpret(PrintStmt stm) {
        interpret(stm.exps).forEach((i) -> System.out.println(i));
        return 0;
    }

    public int interpret(AssignStmt stm) {
        interpret(stm.exp);
        return 0;
    }

    public int interpret(Expression exp) {
        if (exp instanceof NumExp)
            return interpret((NumExp)exp);
        else if (exp instanceof IdExp)
            return interpret((IdExp)exp);
        else if (exp instanceof BinOpExp)
            return interpret((BinOpExp)exp);
        else if (exp instanceof UnaryOpExp)
            return interpret((UnaryOpExp)exp);
        return 0;
    }

    public int interpret(NumExp exp) {
        return exp.num;
    }

    public int interpret(IdExp exp) {
        return symbolTable.get(exp.id);
    }

    public int interpret(BinOpExp exp){
        if (exp.binOp == '+')
            return interpret(exp.firstExp) + interpret(exp.secondExp);
        else if (exp.binOp == '-')
            return interpret(exp.firstExp) - interpret(exp.secondExp);
        else if (exp.binOp == '*')
            return interpret(exp.firstExp) * interpret(exp.secondExp);
        else if (exp.binOp == '/')
            return interpret(exp.firstExp) / interpret(exp.secondExp);
        else if (exp.binOp == '%')
            return interpret(exp.firstExp) % interpret(exp.secondExp);
        return 0;
    }

    public int interpret(UnaryOpExp exp) {
        if (exp.urnOp.equals(">>"))
            return interpret(exp.urnExp) >> 1;
        else if (exp.urnOp.equals("<<"))
            return interpret(exp.urnExp) << 1;
        return 0;
    }

    public ArrayList interpret(ExpList expr) {
        ArrayList<Integer> finalList = new ArrayList<Integer>();
        ExpList el = expr;
        while(true){
            if(el instanceof LastExpList){
                finalList.add(interpret(((LastExpList) el).head));
                return finalList;
            } else {
              finalList.add(interpret(((ExpListAndExp) el).exp));
              el = ((ExpListAndExp) el).list;
            }
        }
    }
}
