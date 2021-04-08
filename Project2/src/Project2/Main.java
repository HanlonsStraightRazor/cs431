package Project2;

import Project2.lexer.*;
import Project2.node.*;
import java.io.*;
import java.util.Queue;
import java.util.LinkedList;

/*
 * CS 431 - Compilers
 * Project 2
 * Authors: Ben Pink, Martin Mueller, Isaiah Ley
 */
public class Main {
    public static void main(String[] args){
        if (args.length != 1) {
            System.err.println("Main takes exactly one argument!");
            System.exit(1);
        }
        Queue<Token> q = new LinkedList<Token>();
        String className = "ProgExpr";
        try {
            File f = new File(args[0]);
            className = "Prog" + f.getName().split(".")[0];
            Lexer l = new Lexer(
                new PushbackReader(
                    new FileReader(f),
                    (int) f.length()
                )
            );
            for (Token t = l.next(); !t.getText().equals(""); t = l.next()) {
                String[] tokenClass = t.getClass().getName().split("\\.");
                if (!tokenClass[tokenClass.length - 1].equals("TWhitespace")) {
                    q.add(t);
                }
            }
            Parser p = new Parser();
            p.parse(q, className);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
