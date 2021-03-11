package Project2;

import java.util.ArrayList;
import java.util.Queue;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    Queue<Token> q;
    Token t;
    StringBuilder sb;
    Parser(Queue<Token> q) {
        this.q = q;
        t = q.poll();
        sb = new StringBuilder();
        addStartBoilerplate();
        stmts();
        addEndBoilerplate();
        System.out.print(sb);
    }
    private String getName(Token token) {
        String[] tokenClass = t.getClass().getName().split("\\.");
        return tokenClass[tokenClass.length - 1];
    }
    private void match(String tokenName) {
        if(tokenName == getName(t)) {
            t = q.poll();
        } else {
            error(tokenName);
        }
    }
    private void stmts() {
        sb.append("private static Stmts program = new Stmts(\n");
        stmt();
        while (q.peek() != null){
            match("TSemi");
            sb.append(",\nnew Stmts(\n");
            stmt();
            sb.append(")\n");
        }
        sb.append(");\n");
    }
    private void stmt(){
        switch(getName(t)) {
            case("TId"):
                assignStmt();
                break;
            case("TEcho"):
                printStmt();
                break;
            default:
                error("TId or TEcho");
        }
    }
    private void assignStmt(){
        sb.append("new AssignStmt(\n");
        sb.append("\"" + t.getText() + "\",\n");
        match(getName(t));
        match("<--");
        sb.append(expression());
        sb.append(")");
    }
    private void printStmt(){
        sb.append("new PrintStmt(\n");
        match("(");
        explist();
        match(")");
        sb.append(")");
    }
    private String expression(){
        switch(getName(t)){
            case("TId"):
                switch(getName(t)){
                    case("TAdd"):
                        binop();
                        expression();
                    case("TSub"):
                        binop();
                        expression();
                    case("TMul"):
                        binop();
                        expression();
                    case("TDiv"):
                        binop();
                        expression();
                    case("TMod"):
                        binop();
                        expression();
                    case("TLshift"):
                        unop();
                    case("TRshift"):
                        unop();
                    default:
                        sb.append("new IdExp(\"" + t.getText() + "\" )");
                        match("TId");
                }
            case("TNum"):
                match("TNum");
                switch(getName(t)){
                    case("TAdd"):
                        binop();
                        expression();
                    case("TSub"):
                        binop();
                        expression();
                    case("TMul"):
                        binop();
                        expression();
                    case("TDiv"):
                        binop();
                        expression();
                    case("TMod"):
                        binop();
                        expression();
                    case("TLshift"):
                        unop();
                    case("TRshift"):
                        unop();
                    default:
                        sb.append("new NumExp(" + t.getText() +  ")");
                        match("TNum");
                }
            default:
                error("TId or TNum");
        }
    }
    private void explist(){
        String holdingExp = expression();
        if(getName(t).equals("TComma")){
            sb.append("new ExpListAndExp(\n");
            sb.append(holdingExp + ",\n");
            explist();
            sb.append(")");
        } else {
            sb.append("new LastExpList(\n");
            sb.append(holdingExp + "\n");
            sb.append(")\n");
        }
    }
    private void binop(){
        switch(getName(t)){
            case("TAdd"):
                break;
            case("TSub"):
                break;
            case("TMul"):
                break;
            case("TDiv"):
                break;
            case("TMod"):
                break;
            default:
                error("TAdd, TSub, TMul, TDiv, or TMod");
        }
    }
    private void unop(){
        switch(getName(t)){
            case("TLshift"):
                break;
            case("TRshift"):
                break;
            default:
                error("TLshift or TRshift");
        }
    }
    private void error(String expected) {
        System.err.printf(
            "Parsing Error: Expected %s got %s\n",
            expected,
            getName(t)
        );
        System.exit(1);
    }
    private void addStartBoilerplate() {
        sb.append("package Interpreter;\n");
        sb.append("public class ProgExpr {\n");
    }
    private void addEndBoilerplate() {
        sb.append("public static void main(String[] args) {\n");
        sb.append("Interpreter interpreter = new Interpreter();\n");
        sb.append("interpreter.interpreter(program);\n");
        sb.append("}\n");
        sb.append("}\n");
    }
}
