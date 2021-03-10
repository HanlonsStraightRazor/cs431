package Project2;

import Project2.lexer.*;
import Project2.node.*;
import java.io.*;

/*
 * CS 431 - Compilers
 * Project 2
 * Authors: Ben Pink, Martin Mueller, Isaiah Ley
 */
public class Main {
    public static void main(String[] args){
        try{
            // Create a lexer instance.
            for (String arg : args) {
                // String name = arg.split(".")[0];
                // System.setOut(new PrintStream(new FileOutputStream(name + ".answer")));
                Lexer l = new Lexer(new PushbackReader
                        (new FileReader(new File(arg)), 4096));
                Token t = l.next();
                while (!t.getText().equals("")){
                    String[] sarr = t.getClass().getName().split("\\.");
                    if (!sarr[sarr.length - 1].equals("TWhitespace")) {
                        System.out.println("<"+sarr[sarr.length - 1]+">");
                    }
                    t = l.next();
                }
            }
        }
        catch(Exception e){ System.out.println(e.getMessage()); }
    }

    public void parser(){
        Token t = getToken();
        void match(Token matchMe){
            if(matchMe == t){
                t = getToekn();
            } else {
                //error
            }
        }
        void factor(){
            switch(t){
                case(lpar) : match(lpar); exp(); match(rpar); break;
                case(number) : match(number); break;
                default: error();
            }
        }
        void term(){
            switch(t){
                case(term) : term(); multOp(); factor(); break;
                case(factor) : factor(); break;
                default: error();
        }
        void exp(){
            switch(t){
                case(exp) : exp(); addOp(); term(); break;
                case(term) : term(); break;
                default: error();
        }
        void multOp(){
            switch(t){
                case('*') : match('*'); break;
                case('/') : match('/'); break;
                default: error();
        }
        void addOp(){
            switch(t){
                case('+') : match('+'); break;
                case('-') : match('-'); break;
                default: error();
        }
    }
}
