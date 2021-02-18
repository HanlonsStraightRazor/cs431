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

    public static void main(String[] arguments){
            try{
                // Create a lexer instance.
                Lexer l = new Lexer(new PushbackReader
                        (new InputStreamReader(System.in), 1024));

                Token t = l.next();
                while (!t.getText().equals("")){
                        System.out.print("<"+t.toString()+">");
                        t = l.next();
                }
            }
            catch(Exception e){ System.out.println(e.getMessage()); }
    }
}
