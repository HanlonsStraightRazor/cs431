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
            String name = args[0].split(".")[0];
            System.setOut(new PrintStream(new File(name + ".answer")));
            File program = new File(args[0]);
            Lexer l = new Lexer(new PushbackReader
                    (new FileReader(program), 4096));
            Token t = l.next();
            while (!t.getText().equals("")){
                System.out.print("<"+t.toString()+">");
                t = l.next();
            }
        }
        catch(Exception e){ System.out.println(e.getMessage()); }
    }
}
