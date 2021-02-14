package Starter;

public class ProgramTwo{
  //the syntax tree representation of:  echo(34)
  //You should create separate programs for each tree you create.
    private static Stmts program = new Stmts(
        new AssignStmt(
          "two",
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
            "three",
            new BinOpExp(
              new BinOpExp(
                new VarExp("two"),
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
                  new VarExp("two")
                ),
                new VarExp("three")
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
