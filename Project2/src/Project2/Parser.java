package Project2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import Project2.lexer.*;
import Project2.node.*;

class Parser {
    Queue<Token> q;
    StringBuilder sb;
    void parse(Queue<Token> q, String className) {
        this.q = q;
        sb = new StringBuilder("");
        addStartBoilerplate(className);
        stmts();
        addEndBoilerplate();
        System.out.print(sb);
    }
    Token getToken() {
        return q.peek();
    }
    Token consume() {
        return q.poll();
    }
    private String getName(Token token) {
        if (token == null) {
            return null;
        }
        String[] tokenClass = token.getClass().getName().split("\\.");
        return tokenClass[tokenClass.length - 1];
    }
    private void stmts() {
        sb.append("private static Stmts program = new Stmts(\n");
        stmt();
        int i = 0;
        for (; getToken() != null; i++){
            fail("TSemi", "semicolon");
            sb.append(",\nnew Stmts(\n");
            stmt();
        }
        for(int x = 0; x < i; x++){
            sb.append(")\n");
        }
        sb.append(");\n");
    }
    private void stmt(){
        switch(getName(getToken())) {
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
        sb.append("\"" + consume().getText() + "\",\n");
        //fail("TEquals", "<--");
        consume();
        sb.append(expression());
        sb.append(")");
    }
    private void printStmt(){
        consume();
        sb.append("new PrintStmt(\n");
        fail("TLparen", "(");
        explist();
        fail("TRparen", ")");
        sb.append(")");
    }
    private String expression(){
        Queue<Token> tokens = infixToPostfix();
        Stack<String> stack = new Stack<>();
        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            switch (getName(token)) {
                case ("TId"):
                    stack.push(
                        "new IdExp(\""
                        + token.getText()
                        + "\")"
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
    private Queue<Token> infixToPostfix() {
        Queue<Token> tokens = new LinkedList<>();
        Stack<Token> stack = new Stack<>();
        while (getName(getToken()) != null
                && !getName(getToken()).equals("TSemi")
                && !getName(getToken()).equals("TComma")
                && !getName(getToken()).equals("TRparen")) {
            switch (getName(getToken())) {
                case ("TId"):
                    tokens.add(consume());
                    break;
                case ("TNum"):
                    tokens.add(consume());
                    break;
                case ("TAdd"):
                    while (!stack.empty()) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TSub"):
                    while (!stack.empty()) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TMul"):
                    while (!stack.empty() && !(getName(stack.peek()).equals("TAdd")
                            || getName(stack.peek()).equals("TSub"))) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TDiv"):
                    while (!stack.empty() && !(getName(stack.peek()).equals("TAdd")
                            || getName(stack.peek()).equals("TSub"))) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TMod"):
                    while (!stack.empty() && !(getName(stack.peek()).equals("TAdd")
                            || getName(stack.peek()).equals("TSub"))) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TLshift"):
                    while (!stack.empty() && (stack.peek().equals("TLshift")
                            || stack.peek().equals("TRshift"))) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
                    break;
                case ("TRshift"):
                    while (!stack.empty() && (stack.peek().equals("TLshift")
                            || stack.peek().equals("TRshift"))) {
                        tokens.add(stack.pop());
                    }
                    stack.push(consume());
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
    private void explist(){
        String holdingExp = expression();
        if(getName(getToken()).equals("TComma")){
            consume();
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
    private void fail(String name, String msg) {
        if (!getName(getToken()).equals(name)) {
            error(msg);
        }
        consume();
    }
    private void error(String expected) {
        System.err.printf(
            "Parsing Error: Expected %s got %s\n",
            expected,
            getToken().getText()
        );
        System.exit(1);
    }
    private void addStartBoilerplate(String className) {
        sb.append("package Interpreter;\n");
        sb.append("public class " + className + " {\n");
    }
    private void addEndBoilerplate() {
        sb.append("public static void main(String[] args) {\n");
        sb.append("Interpreter interpreter = new Interpreter();\n");
        sb.append("interpreter.interpret(program);\n");
        sb.append("}\n");
        sb.append("}\n");
    }
}
