package Starter;

public class ProgramTwo{
	//the syntax tree representation of:  echo(34)
	//You should create separate programs for each tree you create.
  	private static Stmt program = new PrintStmt(new LastExpList(new NumExp(34)));

	public static void main(String[] args) {
		//Create a new Interpreter Object
	    Interpreter interpreter = new Interpreter();
	    System.out.println("Evaluating...");
	    //Call the overloaded interpret method with the
	    //static program created above. Should print out 34.
	    interpreter.interpret(program);
	}
}