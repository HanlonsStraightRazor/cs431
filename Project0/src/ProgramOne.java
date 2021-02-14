package Starter;

public class ProgramOne {
    private static Stmts program = new Stmts(
        new PrintStmt(
            new LastExpList(
                new AssignStmt(
                    "one",
                    new NumExp(30)
                )
            )
        );

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        System.out.println("Evaluating...");
        interpreter.interpret(program);
    }
}
