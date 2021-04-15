package FinalProject;

import FinalProject.lexer.*;
import FinalProject.node.*;
import FinalProject.parser.*;
import java.io.*;

public class Main{

   public static void main(String[] arguments){
      try{
            Lexer lexer = new Lexer(new PushbackReader
                  (new InputStreamReader(System.in), 1024));

            Parser parser = new Parser(lexer);

            Start ast = parser.parse();
            ast.apply(new PrintTree());  //this is what gets the depth first search going
      }
      catch(Exception e){ System.out.println("Error: " + e.getMessage()); }
   }
}