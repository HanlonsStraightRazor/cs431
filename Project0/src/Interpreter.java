package Starter;
import java.util.HashMap;

/*
	Add methods to handle the traversal of other nodes in 
	the syntax tree. Some methods will need to be updated. 
	For instance, the interpret method for a Stmt assumes 
	that all statements are print statements. This is 
	obviously not the case and needs to be handled.
*/

public class Interpreter{

  public static HashMap<String, Integer> symbolTable = new HashMap<>();

  public Interpreter() {
    symbolTable = new HashMap<>();
  }

  public int interpret(Stmts ss) {
    if (ss == null) {
      return 0;
    } else if (ss.s instanceof AssignStmt) {
      AssignStmt as = interpret(ss.s);
      symbolTable.put(as.id, as.exp);
    } else {
      interpret(ss.ss);
    }
  }
	//currently assumes all Stmt are PrintStmt
	//probably needs to be updated
 	public int interpret(Stmt stm) {
	   if (stm instanceof PrintStmt)
    		return this.interpret((PrintStmt)stm);
		else if (stm instanceof AssignStmt)
			return this.interpret((AssignStmt)stm);
		return 0;
 	}

	//each PrintStmt contains an ExpList
	//evaluate the ExpList
 	public int interpret(PrintStmt stm) {
 		ExpList exp = stm.exps;
 	   	System.out.println(this.interpret(exp));
 	   	return 0;
 	}

 	public int interpret(Expression exp) {
    	if (exp instanceof NumExp)
      		return this.interpret((NumExp)exp);
    	else if (exp instanceof IdExp)
      		return this.interpret((IdExp)exp);
    	else if (exp instanceof BinOpExp)
      		return this.interpret((BinOpExp)exp);
    	else if (exp instanceof UnaryOpExp)
      		return this.interpret((UnaryOpExp)exp);
    	return 0;
 	}

 	public int interpret(NumExp exp) {
    	return exp.num;
 	}

 	public String interpret(IdExp exp) {
    	return exp.id;
 	}

	public int interpret(BinOpExp exp){
    	if(exp.binOp == '+')
        	return interpret(exp.firstExp) + interpret(exp.secondExp);
    	else if(exp.binOp == '-')
        	return interpret(exp.firstExp) - interpret(exp.secondExp);
        else if(exp.binOp == '*')
            return interpret(exp.firstExp) * interpret(exp.secondExp);
        else if(exp.binOp == '/')
            return interpret(exp.firstExp) / interpret(exp.secondExp);
        else if(exp.binOp == '%')
            return interpret(exp.firstExp) % interpret(exp.secondExp);
		return 0;
	}

 	public int interpret(UnaryOpExp exp) {
    	if(exp.urnOp == ">>")
        	return interpret(exp.urnExp) >> 1;
    	else if(exp.urnOP == "<<")
        	return interpret(exp.urnExp) << 1;
		return 0;
 	}

 	public ExpList interpret(ExpList list) {
    	return this.interpret((LastExpList)list);
 	}

 	public Expression interpret(LastExpList list) {
    	return this.interpret(list.head);
  	}
}
