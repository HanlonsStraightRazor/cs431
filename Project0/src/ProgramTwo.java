package Starter;

public class ProgramTwo{
  //the syntax tree representation of:  echo(34)
  //You should create separate programs for each tree you create.
    private static Stmts program = new Stmts(
        new AssignStmt(
          new IdExp("two"),
          new BinOpExp(
            new BinOpExp(
              new NumExp(20),
              '-',
              new NumExp(10)
            ),
            '*',
            new NumExp(5)
          )
        ),
        new Stmts(
          new AssignStmt(
            new IdExp("three"),
            new BinOpExp(
              new BinOpExp(
                new IdExp("two"),
                '%',
                new NumExp(4)
              ),
              '+',
              new NumExp(6)
            )
          ),
          new Stmts(
            new PrintStmt(
              new ExpListAndExp(
                new LastExpList(
                  new IdExp("two")
                ),
                new IdExp("three")
              )
            )
          )
        )
      );

  public static void main(String[] args) {
    //Create a new Interpreter Object
      Interpreter interpreter = new Interpreter();
      System.out.println("Evaluating...");
      //Call the overloaded interpret method with the
      //static program created above. Should print out 34.
      interpreter.interpret(program);
  }
}
