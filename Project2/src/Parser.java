package Project2;

import java.util.Queue;
import java.util.LinkedList;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    Queue<Token> q;
    Token t;
    Parser(Queue<Token> q) {
        this.q = q;
        t = q.poll();
    }
    private Token getToken(Token token) {
        return q.poll();
    }
    private void match(Token matchMe) {
        if(matchMe == t){
            t = getToken();
        } else {
            error();
        }
    }
    private void factor(){
        switch(t){
            case(lpar) : match(lpar); exp(); match(rpar); break;
            case(number) : match(number); break;
            default: error();
        }
    }
    private void term(){
        factor();
        while(peekToken() == multOp){
            multOp();
            factor();
        }
    }
    private void exp(){
        term();
        while(peekToken() == addOp){
            addOp();
            term();
        }
    }
    private void multOp(){
        switch(t){
            case('*') : match('*'); break;
            case('/') : match('/'); break;
            default: error();
        }
    }
    private void addOp(){
        switch(t){
            case('+') : match('+'); break;
            case('-') : match('-'); break;
            default: error();
        }
    }
    private void error() {
        System.err.println("ERROR");
    }
}
