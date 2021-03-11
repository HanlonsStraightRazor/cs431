package Interpreter;

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

class PrintStmt extends Stmt{
  public ExpList exps;
  public PrintStmt(ExpList e){
    exps = e;
  }
}

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

class UnaryOpExp extends Expression{
    public Expression urnExp;
    public String urnOp;
    public UnaryOpExp(Expression urnExp, String urnOp){
        this.urnExp = urnExp;
        this.urnOp = urnOp;
    }
}

abstract class ExpList {}

class ExpListAndExp extends ExpList{
  public Expression exp;
  public ExpList list;
  public ExpListAndExp(Expression exp, ExpList list){
    this.exp = exp;
    this.list = list;
  }
}

class LastExpList extends ExpList{
  public Expression head;
  public LastExpList(Expression h){
    head = h;
  }
}
