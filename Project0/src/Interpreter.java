package Starter;
import java.util.HashMap;

/*
   Add methods to handle the traversal of other nodes in 
   the syntax tree. Some methods will need to be updated. 
   For instance, the interpret method for a Stmt assumes 
   that all statements are print statements. This is 
   obviously not the case and needs to be handled.
   */

public class Interpreter{

    public static HashMap<String, Integer> symbolTable = new HashMap<>();

    public Interpreter() {
        symbolTable = new HashMap<>();
    }

    public int interpret(Stmts ss) {
        if (ss == null) {
            return 0;
        } else {
            interpret(ss.s);
            if (ss.ss != null) {
                interpret(ss.ss);
            }
        }
    }
    //currently assumes all Stmt are PrintStmt
    //probably needs to be updated
    public int interpret(Stmt stm) {
        if (stm instanceof PrintStmt)
            return interpret((PrintStmt)stm);
        else if (stm instanceof AssignStmt)
            symbolTable.put(interpret((IdExp) (((AssignStmt) stm).id)), interpret(((AssignStmt)stm).exp));
        return 0;
    }

    //each PrintStmt contains an ExpList
    //evaluate the ExpList
    public int interpret(PrintStmt stm) {
        ExpList exp = stm.exps;
        System.out.println(interpret(exp));
        return 0;
    }

    public int interpret(AssignStmt stm) {
        Expression exp = stm.exp;
        interpret(exp);
        return 0;
    }

    public int interpret(Expression exp) {
        if (exp instanceof NumExp)
            return interpret((NumExp)exp);
        else if (exp instanceof BinOpExp)
            return interpret((BinOpExp)exp);
        else if (exp instanceof UnaryOpExp)
            return interpret((UnaryOpExp)exp);
        return 0;
    }

    public int interpret(NumExp exp) {
        return exp.num;
    }

    public String interpret(IdExp exp) {
        return exp.id;
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

    public int interpret(ExpList exp) {
        if (exp instanceof LastExpList)
            return interpret((LastExpList)exp);
        else if (exp instanceof ExpListAndExp)
            return interpret((ExpListAndExp)exp);
        return 0;
    }

    public int interpret(ExpListAndExp oneExAndList) {
        interpret(oneExAndList.exp);
        interpret(oneExAndList.list);
        return 0;
    }

    public Expression interpret(LastExpList list) {
        return interpret(list.head);
    }
}
