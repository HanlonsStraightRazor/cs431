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
        Queue<Token> q = new LinkedList<Token>();
        try {
            for (String arg : args) {
                Lexer l = new Lexer(
                    new PushbackReader(
                        new FileReader(
                            new File(arg)
                        ),
                        4096
                    )
                );
                for (Token t = l.next(); !t.getText().equals(""); t = l.next()) {
                    String [] currToken = t.getClass().getName().split("\\.");
                    if (!currToken[currToken.length - 1].equals("TWhitespace")) {
                        q.add(t.getText());
                    }
                }
            }
        }
        catch(Exception e){ System.out.println(e.getMessage()); }
    }
}
