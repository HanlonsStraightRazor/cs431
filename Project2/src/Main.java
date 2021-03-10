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
}
