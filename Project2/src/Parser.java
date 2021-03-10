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
    private Token getToken() {
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
            case(lparen) : match(lparen); exp(); match(rparen); break;
            case(num) : match(num); break;
            default: error();
        }
    }
    private void term(){
        factor();
        while(peekToken() == '*' | '/'){
            multOp();
            factor();
        }
    }
    private void exp(){
        term();
        while(peekToken() == '+' | '-'){
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
