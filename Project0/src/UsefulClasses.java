package Starter;

/*
	You will need to add many more classes to this file to get the interpreter 
	to work. The pattern shown below for the simple example should be enough 
	to show you what to do for the remaining classes.
*/

class Stmts {
  public Stmt s;
  public Stmts ss = null;
  public Stmts(Stmt s) {
    this.s = s;
  }
  public Stmts(Stmt s, Stmts ss) {
    this.s = s;
    this.ss = ss;
  }
}

abstract class Stmt {}

//handles the Stmt --> echo ( ExpList ) production
class PrintStmt extends Stmt{
    public ExpList exps;
    public PrintStmt(ExpList e){
        exps = e;
    }
}

//handles the Stmt --> id <-- Expression production
class AssignStmt extends Stmt{
    public String id;
    public Expression exp;
    public AssignStmt(String i, Expression e){
        id = i;
        exp = e;
    }
}

abstract class Expression {}

class IdExp extends Expression{
    public String id;
    public IdExp(String i){
        id = i;
    }
}

class NumExp extends Expression
{
    public int num;
    public NumExp(int n){
        num = n;
    }
}

class BinOpExp extends Expression{
    public Expression firstExp;
    public Expression secondExp;
    public char binOp;
    public BinOpExp(Expression firstExp, char binOp, Expression secondExp){
        this.firstExp = firstExp;
        this.binOp = binOp;
        this.secondExp = secondExp;
    }
}


class 

abstract class ExpList {}

class LastExpList extends ExpList
{
    public Expression head;
    public LastExpList(Expression h){
        head = h;
    }
}
