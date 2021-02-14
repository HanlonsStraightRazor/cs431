package Starter;

public class ProgramTwo{
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
                        new IdExp("three"),
                        new LastExpList(
                            new IdExp("two")
                        )
                    )
                )
            )
        )
    );

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        System.out.println("Evaluating...");
        interpreter.interpret(program);
    }
}
