package Project1;

import Project1.lexer.*;
import Project1.node.*;
import java.io.*;

/*
 * CS 431 - Compilers
 * Project 1
 * Authors: Ben Pink, Martin Mueller, Isaiah Ley
 */
public class Main{
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
                    System.out.print("<"+t.toString()+">");
                    t = l.next();
                }
            }
        }
        catch(Exception e){ System.out.println(e.getMessage()); }
    }
}
