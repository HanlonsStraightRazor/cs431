package Project2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    Queue<Token> q;
    Token t;
    StringBuilder sb;
    void parse(Queue<Token> q) {
        this.q = q;
        t = q.poll();
        sb = new StringBuilder("");
        addStartBoilerplate();
        stmts();
        addEndBoilerplate();
        System.out.print(sb);
    }
    private String getName(Token token) {
        if (t == null) {
            return null;
        }
        String[] tokenClass = t.getClass().getName().split("\\.");
        return tokenClass[tokenClass.length - 1];
    }
    private void match(String tokenName) {
        if(tokenName.equals(getName(t))) {
            t = q.poll();
        } else {
            error(tokenName);
        }
    }
    private void stmts() {
        sb.append("private static Stmts program = new Stmts(\n");
        stmt();
        while (q.peek() != null){
            match("TSemi");
            sb.append(",\nnew Stmts(\n");
            stmt();
            sb.append(")\n");
        }
        sb.append(");\n");
    }
    private void stmt(){
        switch(getName(t)) {
            case("TId"):
                assignStmt();
                break;
            case("TEcho"):
                printStmt();
                break;
            default:
                error("identifer or print statement");
        }
    }
    private void assignStmt(){
        sb.append("new AssignStmt(\n");
        sb.append("\"" + t.getText() + "\",\n");
        match(getName(t));
        match("TEquals");
        // sb.append("new NumExp(30)");
        // match("TNum");
        sb.append(expression());
        sb.append(")");
    }
    private void printStmt(){
        sb.append("new PrintStmt(\n");
        match("TLparen");
        explist();
        match("TRparen");
        sb.append(")");
    }
    private String expression(){
        Queue<Token> tokens = infixToPostfix();
        Stack<String> stack = new Stack<>();
        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            System.out.println(token);
            switch (getName(token)) {
                case ("TId"):
                    stack.push(
                        "new IdExp("
                        + token.getText()
                        + ")"
                    );
                    break;
                case ("TNum"):
                    stack.push(
                        "new NumExp("
                        + token.getText()
                        + ")"
                    );
                    break;
                case ("TAdd"):
                    String addend = stack.pop();
                    String augend = stack.pop();
                    stack.push(
                        "new BinOpExp(\n"
                        + augend
                        + ",\n'+',\n"
                        + addend
                        + "\n)"
                    );
                    break;
                case ("TSub"):
                    String subtrahend = stack.pop();
                    String minuend = stack.pop();
                    stack.push(
                        "new BinOpExp(\n"
                        + minuend
                        + ",\n'-',\n"
                        + subtrahend
                        + "\n)"
                    );
                    break;
                case ("TMul"):
                    String multiplier = stack.pop();
                    String multiplicand = stack.pop();
                    stack.push(
                        "new BinOpExp(\n"
                        + multiplicand
                        + ",\n'*',\n"
                        + multiplier
                        + "\n)"
                    );
                    break;
                case ("TDiv"):
                    String divisor = stack.pop();
                    String dividend = stack.pop();
                    stack.push(
                        "new BinOpExp(\n"
                        + dividend
                        + ",\n'/',\n"
                        + divisor
                        + "\n)"
                    );
                    break;
                case ("TMod"):
                    String mdivisor = stack.pop();
                    String mdividend = stack.pop();
                    stack.push(
                        "new BinOpExp(\n"
                        + mdividend
                        + ",\n'%',\n"
                        + mdivisor
                        + "\n)"
                    );
                    break;
                case ("TLshift"):
                    String loperand = stack.pop();
                    stack.push(
                        "new UnaryOpExp(\n"
                        + loperand
                        + ",\n\"<<\""
                        + "\n)"
                    );
                    break;
                case ("TRshift"):
                    String roperand = stack.pop();
                    stack.push(
                        "new UnaryOpExp(\n"
                        + roperand
                        + ",\n\">>\""
                        + "\n)"
                    );
            }
        }
        return stack.pop();
    }
    private void explist(){
        String holdingExp = expression();
        if(getName(t).equals("TComma")){
            sb.append("new ExpListAndExp(\n");
            sb.append(holdingExp + ",\n");
            explist();
            sb.append(")");
        } else {
            sb.append("new LastExpList(\n");
            sb.append(holdingExp + "\n");
            sb.append(")\n");
        }
    }
    private Queue<Token> infixToPostfix() {
        Queue<Token> tokens = new LinkedList<>();
        Stack<Token> stack = new Stack<>();
        while (getName(t) != null
                && !getName(t).equals("TSemi")
                && !getName(t).equals("TComma")
                && !getName(t).equals("TRparen")) {
            switch (getName(t)) {
                case ("TId"):
                    tokens.add(t);
                    match("TId");
                    break;
                case ("TNum"):
                    tokens.add(t);
                    match("TNum");
                    break;
                case ("TAdd"):
                    while (!stack.empty()) {
                        tokens.add(stack.pop());
                    }
                    stack.push(t);
                    match("TAdd");
                    break;
                case ("TSub"):
                    while (!stack.empty()) {
                        tokens.add(stack.pop());
                    }
                    stack.push(t);
                    match("TSub");
                    break;
                case ("TMul"):
                    if (!stack.empty()) {
                        while (!getName(stack.peek()).equals("TAdd")
                                && !getName(stack.peek()).equals("TSub")) {
                            tokens.add(stack.pop());
                        }
                    }
                    stack.push(t);
                    match("TSub");
                    break;
                case ("TDiv"):
                    if (!stack.empty()) {
                        while (!getName(stack.peek()).equals("TAdd")
                                && !getName(stack.peek()).equals("TSub")) {
                            tokens.add(stack.pop());
                        }
                    }
                    stack.push(t);
                    match("TDiv");
                    break;
                case ("TMod"):
                    if (!stack.empty()) {
                        while (!getName(stack.peek()).equals("TAdd")
                                && !getName(stack.peek()).equals("TSub")) {
                            tokens.add(stack.pop());
                        }
                    }
                    stack.push(t);
                    match("TMod");
                    break;
                case ("TLshift"):
                    if (!stack.empty()) {
                        while (stack.peek().equals("TLShift")
                                || stack.peek().equals("TRshift")) {
                            tokens.add(stack.pop());
                        }
                    }
                    stack.push(t);
                    match("TLshift");
                    break;
                case ("TRshift"):
                    if (!stack.empty()) {
                        while (stack.peek().equals("TLShift")
                                || stack.peek().equals("TRshift")) {
                            tokens.add(stack.pop());
                        }
                    }
                    stack.push(t);
                    match("TRshift");
                    break;
                default:
                    error("operator or operand");
            }
        }
        while (!stack.empty()) {
            tokens.add(stack.pop());
        }
        return tokens;
    }
    private void error(String expected) {
        System.err.printf(
            "Parsing Error: Expected %s got %s\n",
            expected,
            getName(t)
        );
        System.exit(1);
    }
    private void addStartBoilerplate() {
        sb.append("package Interpreter;\n");
        sb.append("public class ProgExpr {\n");
    }
    private void addEndBoilerplate() {
        sb.append("public static void main(String[] args) {\n");
        sb.append("Interpreter interpreter = new Interpreter();\n");
        sb.append("interpreter.interpret(program);\n");
        sb.append("}\n");
        sb.append("}\n");
    }
}
