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
            // So the line under these comments is just wack, its just not the output we are looking for.  
            // And there is no need to get excited, we expect it to work anyways ._.
            System.out.println("It's valid!");
      }
      catch(Exception e){ System.out.println("NOT VALID: " + e.getMessage()); }
   }
}