package Project2;

import java.util.ArrayList;
import java.util.Queue;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    Queue<Token> q;
    Token t;
    Parser(Queue<Token> q) {
        this.q = q;
        t = q.poll();
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
        stmt();
        while (q.peek() != null){
            match("TSemi");
            stmt();
        }
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
    }
    private void printStmt(){
    }
    private double expression(){
        switch(getName(t)){
            case("TId"):
                return 0.0;
            case("TNum"):
                return 0.0;
            default:
                error("TId or TNum");
                return 0.0;
        }
    }
    private ArrayList<Double> explist(){
        ArrayList<Double> list = new ArrayList<>();
        list.add(expression());
        while(getName(t).equals("TComma")) {
            match("TComma");
            list.add(expression());
        }
        return list;
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
                error("TAdd, TSub, TMul, TDiv, TMod");
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
}
