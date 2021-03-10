package Project2;

import java.util.Queue;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    // Class variables
    Queue<Token> q;
    Token t;
    // Constructor
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
    private void match(String matchMe) {
        if(matchMe == getName(t)) {
            t = getToken();
        } else {
            error();
        }
    }
    private void stmts() {
        stmt();
        while (q.peek() != null){
            match(semi);
            stmts();
        }
    }
    private void stmt(){
        switch(getName(t)) {
            case("TId"):
            case("TEcho"):
            default:
                error();
        }
    }
    private void expression(){
        switch(t){
            case(id):
                match(id);
                break;
            case(number):
                match(number);
                break;
            expression();
            while(peekToken().equals("*") | peekToken().equals("/")){
                binop();
                expression();
            }
            if(peekToken().equals("<<") | peekToken().equals(">>")){
                unop();
            }
        }
    }
    private void explist(){
        term();
        while(peekToken().equals("+") | peekToken().equals("-")){
            addOp();
            term();
        }
    }
    private void binop(){
        switch(t){
            case("*"):
                match("*");
                break;
            case("/"):
                match("/");
                break;
            default:
                error();
        }
    }
    private void unop(){
        switch(t){
            case("+"):
                match("+");
                break;
            case("-"):
                match("-");
                break;
            default:
                error();
        }
    }
    private void error() {
        System.err.println("ERROR");
        System.exit(1);
    }
}
