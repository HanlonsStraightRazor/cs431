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
    private Token getToken() {
        return q.poll();
    }
    private String getName(Token token) {
        String[] tokenClass = t.getClass().getName().split("\\.");
        return tokenClass[tokenClass.length - 1];
    }
    private void match(String tokenName) {
        if(tokenName == getName(t)) {
            t = getToken();
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
        //id
        //exp
    }
    private void printStmt(){
        sb.append("new PrintStmt(\n");
        match("(");
        // explist();
        match(")");
        sb.append(")\n");
    }
    private void expression(){
        switch(getName(t)){
            case("TId"):
                sb.append("new IdExp(\"" + t.getText() + "\"");
                match("TId");
                switch(getName(t)){
                    case("TAdd"):
                        match("TAdd");
                        expression();
                        break;
                    case("TSub"):
                        match("TSub");
                        expression();
                        break;
                    case("TMul"):
                        match("TMul");
                        expression();
                        break;
                    case("TDiv"):
                        match("TDiv");
                        expression();
                        break;
                    case("TMod"):
                        match("TMod");
                        expression();
                        break;
                    case("TLshift"):
                        match("TLshift");
                        break;
                    case("TRshift"):
                        match("TRshift");
                        break;
                    // case(null):
                        // FIXME
                    default:
                        error("TId or TNum");
                }
            case("TNum"):
            default:
                error("TId or TNum");
        }
    }
    /*
    private void explist(){
        expression();
        while(getName(t).equals("TComma")) {
            match("TComma");
            expression();
        }
        sb.append("something");
    }
    */
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
