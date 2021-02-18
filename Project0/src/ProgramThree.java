package Starter;

public class ProgramThree{
    private static Stmts program = new Stmts(
        new AssignStmt(
            "four",
            new BinOpExp(
                new NumExp(5),
                '+',
                new NumExp(10)
            )
        ),
        new Stmts(
            new AssignStmt(
                "five",
                new BinOpExp(
                    new NumExp(10),
                    '/',
                    new NumExp(3)
                )
            ),
            new Stmts(
                new PrintStmt(
                    new ExpListAndExp(
                        new IdExp("four"),
                        new LastExpList(
                            new IdExp("five")
                        )
                    )
                ),
                new Stmts(
                    new AssignStmt(
                        "four",
                        new UnaryOpExp(
                            new IdExp("four"),
                            ">>"
                        )
                    ),
                    new Stmts(
                        new PrintStmt(
                            new LastExpList(
                                new IdExp("four")
                            )
                        ),
                        new Stmts(
                            new AssignStmt(
                                "five",
                                new UnaryOpExp(
                                    new BinOpExp(
                                        new UnaryOpExp(
                                            new IdExp("four"),
                                            "<<"
                                        ),
                                        '/',
                                        new IdExp("four")
                                    ),
                                    ">>"
                                )
                            ),
                            new Stmts(
                                new PrintStmt(
                                    new ExpListAndExp(
                                        new IdExp("four"),
                                        new LastExpList(
                                            new IdExp("five")
                                        )
                                    )
                                )
                            )
                        )
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
