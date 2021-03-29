package ParserProject;

import ParserProject.lexer.*;
import ParserProject.node.*;
import ParserProject.parser.*;
import java.io.*;

public class Main{

   public static void main(String[] arguments){
      try{
            Lexer lexer = new Lexer(new PushbackReader
                  (new InputStreamReader(System.in), 1024));

            Parser parser = new Parser(lexer);

            Start ast = parser.parse();
            // So the line under the comments this is wack, that is just the wrong output.
            // Also why the '!' ? We expect this to work right? *denies PR*
            System.out.println("It's valid!");
      }
      catch(Exception e){ System.out.println("NOT VALID: " + e.getMessage()); }
   }
}