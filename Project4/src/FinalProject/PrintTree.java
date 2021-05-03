package FinalProject;
import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;

/*
* Project 4
* Professor: Erik Krohn
* Authors: Ben Pink, Martin Mueller, Isaiah Ley
*/
class PrintTree extends DepthFirstAdapter {
    // Class variables
    private static ArrayList<HashMap<String, Symbol>> symbolTables;
    private static StringBuilder text;
    private static StringBuilder data;
    private static final String DELIMITER = "    ";
    private static final String LABELPREFIX = "label";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private static int labelnum;
    private static int offset;
    private static Queue<String> error;
    private static int currentScope;
    /*
     * Constructor. Initializes non final class variables.
     */
    public PrintTree() {
        symbolTables = new ArrayList<HashMap<String, Symbol>>();
        HashMap<String, Symbol> scopeZeroHashMap = new HashMap<String, Symbol>();
        symbolTables.add(scopeZeroHashMap);
        error = new LinkedList<String>();
        text  = new StringBuilder();
        data  = new StringBuilder();
        labelnum = 0;
        offset = 0;
        currentScope = 0;
    }
    /*
     * <Prog> ::= BEGIN <ClassMethodStmts> END
     */
    @Override
    public void caseAProg(AProg node) {
        if (node.getBegin() != null) {
            data.append(DELIMITER
                + ".data\n\n"
                + "TRUE:\n"
                + DELIMITER
                + ".asciiz \""
                + TRUE
                + "\"\nFALSE:\n"
                + DELIMITER
                + ".asciiz \""
                + FALSE
                +"\"\n");
            text.append("\n"
                + DELIMITER
                + ".text\n\n");
            node.getBegin().apply(this);
        }
        if (node.getClassmethodstmts() != null) {
            node.getClassmethodstmts().apply(this);
        }
        if (node.getEnd() != null) {
            if(!error.isEmpty()){
                for(String er : error){
                    System.err.println(er);
                }
            } else {
                text.append(DELIMITER
                    + "li $v0, 10\n");
                text.append(DELIMITER
                    + "syscall");
                System.out.print(data);
                System.out.print(text);
                node.getEnd().apply(this);
            }
        }
    }

    @Override
    public void caseAClassStmtsClassmethodstmts(AClassStmtsClassmethodstmts node) {
        if (node.getClassmethodstmts() != null) {
            node.getClassmethodstmts().apply(this);
        }
        if (node.getClassmethodstmt() != null) {
            node.getClassmethodstmt().apply(this);
        }
    }

    @Override
    public void caseAClassDefClassmethodstmt(AClassDefClassmethodstmt node) {
        if (node.getClassLit() != null) {
            error.add("Erroneous class definition.");
            node.getClassLit().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
        }
        if (node.getMethodstmtseqs() != null) {
            node.getMethodstmtseqs().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAMethodDeclClassmethodstmt(AMethodDeclClassmethodstmt node) {
        if (node.getType() != null) {
            String type = node.getType() instanceof AIdType
                ? ((AIdType) node.getType()).toString().trim()
                : ((ATypesType) node.getType()).toString().trim();
            if(!type.equals("VOID")){
                error.add("Method type must be VOID. Got " + type);
            }
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            if(!node.getId().toString().trim().equals("MAIN")){
                error.add("Method ID must be MAIN. Got "
                    + node.getId().toString());
            }
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarlist() != null) {
            if (!(node.getVarlist() instanceof AEpsilonVarlist)) {
                error.add("Argument must be empty in method. Got "
                    + node.getVarlist());
            }
            node.getVarlist().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getLcurly() != null) {
            incScope();
            node.getLcurly().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            decScope();
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAVarDeclClassmethodstmt(AVarDeclClassmethodstmt node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            node.getMoreIds().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAMethodStmtsMethodstmtseqs(AMethodStmtsMethodstmtseqs node) {
        if (node.getMethodstmtseqs() != null) {
            node.getMethodstmtseqs().apply(this);
        }
        if (node.getMethodstmtseq() != null) {
            node.getMethodstmtseq().apply(this);
        }
    }

    @Override
    public void caseAMethodDeclMethodstmtseq(AMethodDeclMethodstmtseq node) {
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarlist() != null) {
            node.getVarlist().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAVarDeclMethodstmtseq(AVarDeclMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            node.getMoreIds().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignEqualsMethodstmtseq(AAssignEqualsMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignStringMethodstmtseq(AAssignStringMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getAnychars() != null) {
            node.getAnychars().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAPrintStmtMethodstmtseq(APrintStmtMethodstmtseq node) {
        if (node.getPut() != null) {
            node.getPut().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignReadInMethodstmtseq(AAssignReadInMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getGet() != null) {
            node.getGet().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignIncMethodstmtseq(AAssignIncMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getIncr() != null) {
            node.getIncr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignDecMethodstmtseq(AAssignDecMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getDecr() != null) {
            node.getDecr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseADeclObjectMethodstmtseq(ADeclObjectMethodstmtseq node) {
        if (node.getLeftSide() != null) {
            node.getLeftSide().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getNew() != null) {
            node.getNew().apply(this);
        }
        if (node.getRightSide() != null) {
            node.getRightSide().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignBooleanMethodstmtseq(AAssignBooleanMethodstmtseq node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getBoolean() != null) {
            node.getBoolean().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAFirstStmtStmtseq(AFirstStmtStmtseq node) {
        if (node.getStmt() != null) {
            node.getStmt().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
    }

    @Override
    public void caseAAssignExprStmt(AAssignExprStmt node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExpr() != null) {
            String id = node.getId().toString().trim();
            int scope = getScope(id);
            if (scope == -1) {
                error.add("Variable " + id + " has not been declared.");
            }
            Variable var = (Variable) getSymbol(scope, id);
            if(!(var.getType().equals("INT")
                || var.getType().equals("REAL"))) {
                error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to INT.");
            }
            text.append(DELIMITER
                + "sw $s0, -"
                + Integer.toString(var.getOffset())
                + "($sp)\n");
            var.initialize();
            addToSymbolTable(id, var, scope);
            node.getExpr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignStringStmt(AAssignStringStmt node) {
        //need to get the string Variable and say it was inited
        String idVal = "", type = "";
        boolean isArray = false;
        if (node.getId() != null) {
            idVal = node.getId().toString().trim();
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                isArray = true;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getAnychars() != null) {
            node.getAnychars().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if(!isArray){
            data.append(idVal + ":\n" +
                        DELIMITER + ".asciiz " + node.getAnychars().toString() + "\n");
            int scope = getScope(idVal);
            if (scope == -1) {
                error.add("Variable "
                    + idVal
                    + " has not been declared.");
            } else {
                Variable var = (Variable)getSymbol(scope, idVal);
                var.initialize();
                addToSymbolTable(idVal, var, scope);
            }
        }
    }

    @Override
    public void caseAVarDeclStmt(AVarDeclStmt node) {
        String idVal = "", type = "";
        boolean isArray = false, alreadyDeclared = false;
        if (node.getId() != null) {
            idVal = node.getId().toString().trim();
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            node.getMoreIds().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            if(node.getType() instanceof AIdType){
                //type = ((AIdType) node.getType()).toString().trim();
                error.add("Can not have type " + ((AIdType) node.getType()).toString().trim() + ".");
            } else {
                type = ((ATypesType) node.getType()).toString().trim();
            }
            node.getType().apply(this);
        }
        if(symbolTables.get(currentScope).containsKey(idVal)){
            alreadyDeclared = true;
            error.add(idVal + " has already been declared in this scope.");
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                isArray = true;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if((!isArray) && (!alreadyDeclared)){
            Variable newVar = new Variable(type, offset);
            addToSymbolTable(idVal, newVar);
            offset = offset + 4;
        }
    }

    @Override
    public void caseAIfBlockStmt(AIfBlockStmt node) {
        String falselabel = LABELPREFIX
            + labelnum;
        boolean isConstant = false;
        boolean constant = false;
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().getText().trim();
                int scope = getScope(id);
                if (scope == -1) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum++;
                }
                Variable var = (Variable) getSymbol(scope, id);
                if(!((Variable) var).getType().toString().equals("BOOLEAN")){
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, -"
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "beq $zero, $t0, "
                    + falselabel
                    + "\n");
            } else {
                ABoolBoolid node2 = (ABoolBoolid) node.getBoolid();
                if (node2.getBoolean() instanceof ATrueBoolean){
                    isConstant = true;
                    constant = true;
                } else if (node2.getBoolean() instanceof AFalseBoolean) {
                    isConstant = true;
                } else if (node2.getBoolean() instanceof AConditionalBoolean) {
                    isConstant = true;
                    //FIXME : AConditionalBoolean
                }
            }
            node.getBoolid().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getThen() != null) {
            node.getThen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
            incScope();
        }
        if ((node.getStmtseq() != null)
            && (!isConstant || constant)) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            if (!isConstant) {
                text.append("\n" + falselabel
                    + ":\n");
            }
            decScope();
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAIfElseBlockStmt(AIfElseBlockStmt node) {
        String falselabel = LABELPREFIX
            + labelnum;
        String endlabel = LABELPREFIX
            + (labelnum + 1);
        boolean isConstant = false;
        boolean constant = false;
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().toString().trim();
                int scope = getScope(id);
                if (scope == -1) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum += 2;
                }
                Variable var = (Variable) getSymbol(scope, id);
                if(!var.getType().toString().equals("BOOLEAN")){
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, -"
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "beq $zero, $t0, "
                    + falselabel
                    + "\n");
            } else {
                ABoolBoolid node2 = (ABoolBoolid) node.getBoolid();
                if (node2.getBoolean() instanceof ATrueBoolean) {
                    isConstant = true;
                    constant = true;
                } else if (node2.getBoolean() instanceof AFalseBoolean) {
                    isConstant = true;
                } else if (node2.getBoolean() instanceof AConditionalBoolean) {
                    isConstant = true;
                    // FIXME: Add conditional support
                }
            }
            node.getBoolid().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getThen() != null) {
            node.getThen().apply(this);
        }
        if (node.getIflcurly() != null) {
            incScope();
            node.getIflcurly().apply(this);
        }
        if (node.getIfBlockStmts() != null) {
            if (constant) {
                node.getIfBlockStmts().apply(this);
            }
        }
        if (node.getIfrcurly() != null) {
            if (!isConstant) {
                text.append(DELIMITER + "j "
                   + endlabel + "\n");
            }
            decScope();
            node.getIfrcurly().apply(this);
        }
        if (node.getElse() != null) {
            node.getElse().apply(this);
        }
        if (node.getElselcurly() != null) {
            incScope();
            node.getElselcurly().apply(this);
        }
        if (node.getElseBlockStmts() != null) {
            if (!isConstant) {
                text.append("\n" + falselabel
                    + ":\n");
            }
            if (!isConstant || !constant) {
                node.getElseBlockStmts().apply(this);
            }
        }
        if (node.getElsercurly() != null) {
            if (!isConstant) {
                text.append("\n" + endlabel
                    + ":\n");
            }
            decScope();
            node.getElsercurly().apply(this);
        }
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node) {
        String trueLabel = LABELPREFIX
            + labelnum;
        String falseLabel = LABELPREFIX
            + (labelnum + 1);
        boolean isConstant = false;
        boolean constant = false;
        Variable var = null;
        if (node.getWhile() != null) {
            node.getWhile().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if(node.getBoolid() instanceof AIdBoolid) {
                boolean found = false;
                String id = ((AIdBoolid) node.getBoolid()).getId().toString().trim();
                int scope = getScope(id);
                if (scope == -1) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum += 2;
                }
                var = (Variable) getSymbol(scope, id);
                if(!var.getType().toString().equals("BOOLEAN")) {
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, -"
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "beq $zero, $t0, "
                    + falseLabel
                    + "\n");
                text.append(trueLabel
                    + ":\n");
                }
            else {
                ABoolBoolid ABoolBoolidNode = (ABoolBoolid)node.getBoolid();
                if((ABoolBoolidNode.getBoolean()) instanceof ATrueBoolean) {
                    isConstant = true;
                    constant = true;
                    text.append(trueLabel
                    + ":\n");
                    }
                else if((ABoolBoolidNode.getBoolean()) instanceof AFalseBoolean) {
                    isConstant = true;
                } 
                else if((ABoolBoolidNode.getBoolean()) instanceof AConditionalBoolean) {
                    //FIXME : AConditionalBoolean
                }
            }
            node.getBoolid().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
            incScope();
        }
        if (node.getStmtseq() != null) {
            if(isConstant && constant) {
                node.getStmtseq().apply(this);
                text.append(DELIMITER
                    + "j "
                    + trueLabel
                    + "\n");
            }
            else if(!isConstant) {
                node.getStmtseq().apply(this);
                text.append(DELIMITER
                    + "lw $t0, -"
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "bne $zero, $t0, "
                    + trueLabel
                    + "\n");
                text.append(DELIMITER
                    + "j "
                    + falseLabel
                    + "\n");
            }
        }
        if (node.getRcurly() != null) {
            if (!isConstant) {
                text.append(falseLabel
                    + ":\n");
            }
            decScope();
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAForStmt(AForStmt node) {
        if (node.getFor() != null) {
            node.getFor().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getForOptionalType() != null) {
            node.getForOptionalType().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
        }
        if (node.getFirst() != null) {
            node.getFirst().apply(this);
        }
        if (node.getBoolid() != null) {
            node.getBoolid().apply(this);
        }
        if (node.getSecond() != null) {
            node.getSecond().apply(this);
        }
        if (node.getForIncrStep() != null) {
            node.getForIncrStep().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAGetStmt(AGetStmt node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getGet() != null) {
            node.getGet().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAPutStmt(APutStmt node) {
        Symbol symbol = null;
        String id = "<INVALID>";
        int scope = -1;
        boolean array = false;
        int index = 0;
        if (node.getPut() != null) {
            node.getPut().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getId() != null) {
            id = node.getId().toString().trim();
            scope = getScope(id);
            if (scope == -1) {
                error.add("Variable " + id + " has not been declared.");
                return;
            }
            symbol = getSymbol(scope, id);
            array = isArray(symbol);
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (array) {
                index =
                     Integer.parseInt(((AArrayArrayOption) node.getArrayOption()).getInt().getText());
                if (!((Array) symbol).isInitializedAt(index)) {
                    error.add("Variable "
                        + id
                        + " has not been initialized at index "
                        + index
                        + ".");
                    return;
                }
            } else {
                if (!((Variable) symbol).isInitialized()) {
                    error.add("Variable "
                        + id
                        + " has not been initialized.");
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            text.append(DELIMITER + "li $v0, ");
            switch (symbol.getType().trim()) {
                case "INTEGER":
                    text.append("1\n");
                    if (array) {
                        text.append(DELIMITER
                            + "la $a0, "
                            + id
                            + "\n");
                        text.append(DELIMITER
                            + "lw $a0, "
                            + index
                            + "($a0)\n");
                    } else {
                        text.append(DELIMITER
                            + "lw $a0, -"
                            + ((Variable) symbol).getOffset()
                            + "($sp)\n");
                    }
                    break;
                case "REAL":
                    text.append("2\n");
                    if (array) {
                        text.append(DELIMITER
                            + "la $t0, "
                            + id
                            + "\n");
                        text.append(DELIMITER
                            + "lw $f12, "
                            + index
                            + "($t0)\n");
                    } else {
                        text.append(DELIMITER
                            + "lw $f12, -"
                            + ((Variable) symbol).getOffset()
                            + "($sp)\n");
                    }
                    break;
                case "STRING":
                    text.append("4\n");
                    if (array) {
                        text.append(DELIMITER
                            + "la $a0, "
                            + id
                            + "\n");
                        text.append(DELIMITER
                            + "lw $a0, "
                            + index
                            + "($a0)\n");
                    } else {
                        text.append(DELIMITER
                            + "lw $a0, -"
                            + ((Variable) symbol).getOffset()
                            + "($sp)\n");
                    }
                    break;
                case "BOOLEAN":
                    text.append("4\n");
                    if (array) {
                        text.append(DELIMITER
                            + "la $t0, "
                            + id
                            + "\n");
                        text.append(DELIMITER
                            + "lw $t0, "
                            + index
                            + "($t0)\n");
                    } else {
                        text.append(DELIMITER
                            + "lw $t0, -"
                            + ((Variable) symbol).getOffset()
                            + "($sp)\n");
                    }
                    String falseLabel = LABELPREFIX + labelnum++;
                    String endLabel   = LABELPREFIX + labelnum++;
                    text.append(DELIMITER
                        + "beq $zero, $t0, "
                        + falseLabel
                        + "\n");
                    text.append(DELIMITER
                        + "la $a0, TRUE\n");
                    text.append(DELIMITER
                        + "j "
                        + endLabel
                        + "\n");
                    text.append("\n"
                        + falseLabel
                        + ":\n");
                    text.append(DELIMITER
                        + "la $a0, FALSE\n");
                    text.append("\n"
                        + endLabel
                        + ":\n");
                    break;
                default:
                    text.append("1\n");
                    if (array) {
                        text.append(DELIMITER
                            + "la $a0, "
                            + id
                            + "\n");
                        text.append(DELIMITER
                            + "lw $a0, "
                            + index
                            + "($a0)\n");
                    } else {
                        text.append(DELIMITER
                            + "lw $a0, -"
                            + ((Variable) symbol).getOffset()
                            + "($sp)\n");
                    }
            }
            text.append(DELIMITER
                + "syscall\n");
            text.append(DELIMITER
                + "li $v0, 11\n"
                + DELIMITER
                + "li $a0, 0xA\n"
                + DELIMITER
                + "syscall\n"); // Print newline
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAIncrStmt(AIncrStmt node) {
        String id = "";
        boolean array = false;
        Object val;
        if (node.getId() != null) {
            id = node.getId().toString().trim();
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                array = true;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getIncr() != null) {
            node.getIncr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if (!array) {
            int scope = getScope(id);
            if (scope == -1) {
                error.add("Variable " + id + " has not been declared.");
            }
            Variable var = (Variable) getSymbol(scope, id);
            if(!(var.getType().equals("INT")
                || var.getType().equals("REAL"))){
                error.add("Variable " + id + " has type " + var.getType() + " which cannot be decremented.");
            } else {
                if (var.getType().equals("INT")){
                    text.append(DELIMITER + "lw $t0, -" + var.getOffset() + "($sp)\n");
                    text.append(DELIMITER + "li $t1, " + "1" + "\n");
                    text.append(DELIMITER + "add $t0, " + "$t0, " + "$t1\n");
                    text.append(DELIMITER + "sw $t0, -" + var.getOffset() + "($sp)\n");
                } else {
                    text.append(DELIMITER + "lw $f0, -" + var.getOffset() + "($sp)\n");
                    text.append(DELIMITER + "li $f1, " + "1.0" + "\n");
                    text.append(DELIMITER + "add $f0, " + "f0, " + "f1\n");
                    text.append(DELIMITER + "sw $f0, -" + var.getOffset() + "($sp)\n");
                }
            }
        }
    }

    @Override
    public void caseADecrStmt(ADecrStmt node) {
        String id = "";
        boolean array = false;
        if (node.getId() != null) {
            id = node.getId().toString().trim();
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                array = true;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getDecr() != null) {
            node.getDecr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if (!array) {
            int scope = getScope(id);
            if (scope == -1) {
                error.add("Variable " + id + " has not been declared.");
            }
            Variable var = (Variable) getSymbol(scope, id);
            if (!(var.getType().equals("INT")
                || var.getType().equals("REAL"))) {
                error.add("Variable " + id + " has type " + var.getType() + " which cannot be decremented.");
            }
            if (var.getType().equals("INT")) {
                text.append(DELIMITER + "lw $t0, -" + var.getOffset() + "($sp)\n");
                text.append(DELIMITER + "li $t1, " + "1" + "\n");
                text.append(DELIMITER + "sub $t0, " + "$t0, " + "$t1\n");
                text.append(DELIMITER + "sw $t0, -" + var.getOffset() + "($sp)\n");
            } else {
                text.append(DELIMITER + "lw $f0, -" + var.getOffset() + "($sp)\n");
                text.append(DELIMITER + "li $f1, " + "1.0" + "\n");
                text.append(DELIMITER + "sub $f0, " + "$f0, " + "$f1\n");
                text.append(DELIMITER + "sw $f0, -" + var.getOffset() + "($sp)\n");
            }
        }
    }

    @Override
    public void caseADeclObjectStmt(ADeclObjectStmt node) {
        if (node.getLeftSide() != null) {
            node.getLeftSide().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getNew() != null) {
            node.getNew().apply(this);
        }
        if (node.getRightSide() != null) {
            node.getRightSide().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAMethodCallStmt(AMethodCallStmt node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAMethodCallInClassStmt(AMethodCallInClassStmt node) {
        if (node.getFirstId() != null) {
            node.getFirstId().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getPeriod() != null) {
            node.getPeriod().apply(this);
        }
        if (node.getInstanceId() != null) {
            node.getInstanceId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getMethodChainingOption() != null) {
            node.getMethodChainingOption().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAReturnStmt(AReturnStmt node) {
        if (node.getReturn() != null) {
            node.getReturn().apply(this);
        }
        if (node.getExprOrBool() != null) {
            if (node.getExprOrBool() instanceof AExprOrBool) {
                //TODO: Add value to S0 when expr works
            }
            else if (node.getExprOrBool() instanceof ABoolExprOrBool) {
                if(((ABoolExprOrBool)node.getExprOrBool()).getBoolean() instanceof AFalseBoolean) {
                    text.append(DELIMITER 
                        + "li $s0, 0\n");
                }
                else {
                    text.append(DELIMITER 
                        + "li $s0, 1\n");
                }
            }
            else {
                error.add("Return type is not an expression or boolean.");
            }
            node.getExprOrBool().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignBooleanStmt(AAssignBooleanStmt node) {
        String id = "<INVALID>";
        boolean isArray = false;
        if (node.getId() != null) {
            id = node.getId().toString().trim();
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                isArray = true;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getBoolean() != null) {
            if (!isArray) {
                int scope = getScope(id);
                if (scope == -1) {
                    error.add("Variable " + id + " has not been declared.");
                }
                Variable var = (Variable) getSymbol(scope, id);
                if(!var.getType().equals("BOOLEAN")){
                    error.add("Variable " + id + " has type " + var.getType() + " which cannot be converted to BOOLEAN.");
                }
                var.initialize();
                addToSymbolTable(id, var, scope);
                if (node.getBoolean() instanceof ATrueBoolean) {
                    text.append(DELIMITER + "li $t0, " + 1 + "\n");
                    text.append(DELIMITER + "sw $t0, -" + var.getOffset() + "($sp)\n");
                } else if (node.getBoolean() instanceof AFalseBoolean) {
                    text.append(DELIMITER + "li $t0, " + 0 + "\n");
                    text.append(DELIMITER + "sw $t0, -" + var.getOffset() + "($sp)\n");
                } else if(node.getBoolean() instanceof AConditionalBoolean){
                    //FIXME : AConditionalBoolean has not been implemented yet
                }
            }
            node.getBoolean().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        if (node.getSwitch() != null) {
            node.getSwitch().apply(this);
        }
        if (node.getFirst() != null) {
            node.getFirst().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
        }
        if (node.getSecond() != null) {
            node.getSecond().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
        }
        if (node.getCase() != null) {
            node.getCase().apply(this);
        }
        if (node.getThird() != null) {
            node.getThird().apply(this);
        }
        if (node.getInt() != null) {
            node.getInt().apply(this);
        }
        if (node.getFourth() != null) {
            node.getFourth().apply(this);
        }
        if (node.getFifth() != null) {
            node.getFifth().apply(this);
        }
        if (node.getStmts() != null) {
            node.getStmts().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            node.getCaseHelper().apply(this);
        }
        if (node.getDefault() != null) {
            node.getDefault().apply(this);
        }
        if (node.getSeccolon() != null) {
            node.getSeccolon().apply(this);
        }
        if (node.getDefaultStmts() != null) {
            node.getDefaultStmts().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAAnotherCaseCaseHelper(AAnotherCaseCaseHelper node) {
        if (node.getCase() != null) {
            node.getCase().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getInt() != null) {
            node.getInt().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            node.getCaseHelper().apply(this);
        }
    }

    @Override
    public void caseABreakBreakHelper(ABreakBreakHelper node) {
        if (node.getBreak() != null) {
            node.getBreak().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAMethodCallMethodChainingOption(AMethodCallMethodChainingOption node) {
        if (node.getPeriod() != null) {
            node.getPeriod().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getMethodChainingOption() != null) {
            node.getMethodChainingOption().apply(this);
        }
    }

    @Override
    public void caseAIncrForIncrStep(AIncrForIncrStep node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getIncr() != null) {
            node.getIncr().apply(this);
        }
    }

    @Override
    public void caseADecrForIncrStep(ADecrForIncrStep node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getDecr() != null) {
            node.getDecr().apply(this);
        }
    }

    @Override
    public void caseAAssignmentForIncrStep(AAssignmentForIncrStep node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
        }
    }

    @Override
    public void caseAForOptionalType(AForOptionalType node) {
        if (node.getType() != null) {
            node.getType().apply(this);
        }
    }

    @Override
    public void caseAMoreIdsMoreIds(AMoreIdsMoreIds node) {
        if (node.getComma() != null) {
            node.getComma().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            node.getMoreIds().apply(this);
        }
    }

    @Override
    public void caseAMoreIdsVarlist(AMoreIdsVarlist node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getMoreVarlist() != null) {
            node.getMoreVarlist().apply(this);
        }
    }

    @Override
    public void caseAArrayArrayOption(AArrayArrayOption node) {
        if (node.getLbracket() != null) {
            node.getLbracket().apply(this);
        }
        if (node.getInt() != null) {
            if(node.parent() instanceof AVarDeclStmt){
                Node node2 = node.parent();
                String id = ((AVarDeclStmt) node2).getId().toString().trim();
                String type = ((AVarDeclStmt) node2).getType().toString().trim();
                int size = Integer.parseInt(node.getInt().toString().trim());
                addToSymbolTable(id, new Array(type, size));
                data.append(id + ":\n");
                if (type.equals("BOOLEAN")) {
                    data.append(DELIMITER + ".byte 0:" + size + "\n");
                } else {
                    data.append(DELIMITER + ".word 0:" + size + "\n");
                }
                data.append("\n");
            } else if(node.parent() instanceof AAssignExprStmt){
                //FIXME
            } else if(node.parent() instanceof AAssignStringStmt){
                String idVal = "";
                int index = 0;
                String anyChar = "";
                AAssignStringStmt AAssignStringStmtNode = (AAssignStringStmt)node.parent();
                idVal = AAssignStringStmtNode.getId().toString().trim();
                index = Integer.parseInt(node.getInt().toString().trim());
                int scope = getScope(idVal);
                if(scope == -1){
                    error.add("Array " + idVal + " has not been declared.");
                } else {
                    Array var = (Array)getSymbol(scope, idVal);
                    if(!var.getType().equals("STRING")){
                        error.add("Array " + idVal + " has type " + var.getType() + " which cannot be converted to STRING.");
                    } else {
                        var.initializeAt(index);
                        addToSymbolTable(idVal, var, scope);
                        anyChar = AAssignStringStmtNode.getAnychars().toString();
                        String label = LABELPREFIX + labelnum;
                                labelnum++;
                        data.append(label + ":\n" +
                                    DELIMITER  + ".asciiz " + anyChar + "\n");
                        Variable dumbyString = new Variable("String", offset);
                        offset = offset + 4;
                        addToSymbolTable(label, dumbyString, scope);
                        text.append(DELIMITER + "la $t0, " + label + "\n");
                        text.append(DELIMITER + "la $t1, " + idVal + "\n");
                        text.append(DELIMITER + "sw $t0, " + Integer.toString(index).trim() + "($t1)\n");
                    }
                }              
            } else if(node.parent() instanceof AIncrStmt){
                AIncrStmt AIncrStmtNode = (AIncrStmt)node.parent();
                String idVal = AIncrStmtNode.getId().toString().trim();
                int scope = getScope(idVal);
                int index = Integer.parseInt(node.getInt().toString().trim());
                
                if (scope == -1) {
                    error.add("Array " + idVal + " has not been declared.");
                }
                Array var = (Array) getSymbol(scope, idVal);
                if(!(var.getType().equals("INT")
                    || var.getType().equals("REAL"))){
                    error.add("Array " + idVal + " has type " + var.getType() + " which cannot be incremented.");
                } else {
                    if(var.isInitializedAt(index)){
                        if (var.getType().equals("INT")){
                            text.append(DELIMITER + "la $t2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $t0, " + Integer.toString(index).trim() + "($t2)\n");
                            text.append(DELIMITER + "li $t1, " + "1" + "\n");
                            text.append(DELIMITER + "add $t0, " + "$t0, " + "$t1\n");
                            text.append(DELIMITER + "sw $t0, " + Integer.toString(index).trim() + "($t2)\n");
                        } else {
                            text.append(DELIMITER + "la $f2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $f0, " + Integer.toString(index).trim() + "($f2)\n");
                            text.append(DELIMITER + "li $f1, " + "1" + "\n");
                            text.append(DELIMITER + "add $f0, " + "$f0, " + "$f1\n");
                            text.append(DELIMITER + "sw $f0, " + Integer.toString(index).trim() + "($f2)\n");
                        }
                    } else {
                        error.add("Array " + idVal + " at index: " + index + " has not been initialized yet.");
                    }
                }
            } else if(node.parent() instanceof ADecrStmt){
                AIncrStmt AIncrStmtNode = (AIncrStmt)node.parent();
                String idVal = AIncrStmtNode.getId().toString().trim();
                int scope = getScope(idVal);
                int index = Integer.parseInt(node.getInt().toString().trim());
                if (scope == -1) {
                    error.add("Array " + idVal + " has not been declared.");
                }
                Array var = (Array) getSymbol(scope, idVal);
                if(!(var.getType().equals("INT")
                    || var.getType().equals("REAL"))){
                    error.add("Array " + idVal + " has type " + var.getType() + " which cannot be decremented.");
                } else {
                    if(var.isInitializedAt(index)){
                        if (var.getType().equals("INT")){
                            text.append(DELIMITER + "la $t2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $t0, " + Integer.toString(index).trim() + "($t2)\n");
                            text.append(DELIMITER + "li $t1, " + "1" + "\n");
                            text.append(DELIMITER + "sub $t0, " + "$t0, " + "$t1\n");
                            text.append(DELIMITER + "sw $t0, " + Integer.toString(index).trim() + "($t2)\n");
                        } else {
                            text.append(DELIMITER + "la $f2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $f0, " + Integer.toString(index).trim() + "($f2)\n");
                            text.append(DELIMITER + "li $f1, " + "1" + "\n");
                            text.append(DELIMITER + "sub $f0, " + "$f0, " + "$f1\n");
                            text.append(DELIMITER + "sw $f0, " + Integer.toString(index).trim() + "($f2)\n");
                        }
                    } else {
                        error.add("Array " + idVal + " at index: " + index + " has not been initialized yet.");
                    }
                }
            } else if(node.parent() instanceof AAssignBooleanStmt){
                AAssignBooleanStmt node2 = (AAssignBooleanStmt) node.parent();
                String id = node2.getId().toString().trim();
                int index = Integer.parseInt(node.getInt().toString().trim());
                int scope = getScope(id);
                if (scope == -1) {
                    error.add("Array " + id + " has not been declared.");
                }
                Array arr = (Array) getSymbol(scope, id);
                if(!arr.getType().equals("BOOLEAN")){
                    error.add("Array " + arr + " has type " + arr.getType() + " which cannot be converted to BOOLEAN.");
                }
                Node bool = node2.getBoolean();
                arr.initializeAt(index);
                addToSymbolTable(id, arr, scope);
                if (bool instanceof ATrueBoolean) {
                    text.append(DELIMITER + "li $t0, " + 1 + "\n");
                    text.append(DELIMITER + "la $t1, " + id + "\n");
                    text.append(DELIMITER + "sw $t0, " + Integer.toString(index).trim() + "($t1)\n");
                } else if (bool instanceof AFalseBoolean) {
                    text.append(DELIMITER + "li $t0, " + 0 + "\n");
                    text.append(DELIMITER + "la $t1, " + id + "\n");
                    text.append(DELIMITER + "sw $t0, " + Integer.toString(index).trim() + "($t1)\n");
                } else if (bool instanceof AConditionalBoolean) {
                    //FIXME : AConditionalBoolean
                }
            }
            node.getInt().apply(this);
        }
        if (node.getRbracket() != null) {
            node.getRbracket().apply(this);
        }
    }

    @Override
    public void caseAMoreIdsMoreVarlist(AMoreIdsMoreVarlist node) {
        if (node.getComma() != null) {
            node.getComma().apply(this);
        }
        if (node.getVarlist() != null) {
            node.getVarlist().apply(this);
        }
    }

    @Override
    public void caseAVarListVarListTwo(AVarListVarListTwo node) {
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
        }
        if (node.getMoreVarListTwo() != null) {
            node.getMoreVarListTwo().apply(this);
        }
    }

    @Override
    public void caseAExprOrBool(AExprOrBool node) {
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
    }

    @Override
    public void caseABoolExprOrBool(ABoolExprOrBool node) {
        if (node.getBoolean() != null) {
            node.getBoolean().apply(this);
        }
    }

    @Override
    public void caseAMoreVarListTwo(AMoreVarListTwo node) {
        if (node.getComma() != null) {
            node.getComma().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
    }

    @Override
    public void caseAAddExpr(AAddExpr node) {
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        if (node.getAddop() != null) {
            node.getAddop().apply(this);
        }
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
        }
    }

    @Override
    public void caseATermExpr(ATermExpr node) {
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
        }
    }

    @Override
    public void caseAMultTerm(AMultTerm node) {
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
        }
        if (node.getMultop() != null) {
            node.getMultop().apply(this);
        }
        if (node.getFactor() != null) {
            node.getFactor().apply(this);
        }
    }

    @Override
    public void caseAFactorTerm(AFactorTerm node) {
        if (node.getFactor() != null) {
            node.getFactor().apply(this);
        }
    }

    @Override
    public void caseAExprFactor(AExprFactor node) {
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
    }

    @Override
    public void caseANegativeFactor(ANegativeFactor node) {
        if (node.getNegop() != null) {
            node.getNegop().apply(this);
        }
        if (node.getFactor() != null) {
            node.getFactor().apply(this);
            text.append(DELIMITER + "li $t0, -1\n");
            text.append(DELIMITER + "mul $t0, $s0\n");
            text.append(DELIMITER + "mflo $s0\n");
        }
    }

    @Override
    public void caseAIntFactor(AIntFactor node) { 
        if (node.getInt() != null) {
            node.getInt().apply(this);
        }
    }

    @Override
    public void caseARealFactor(ARealFactor node) {
        if (node.getReal() != null) {
            node.getReal().apply(this);
        }
    }

    @Override
    public void caseAArrayFactor(AArrayFactor node) {
        if (node.getArrayOrId() != null) {
            node.getArrayOrId().apply(this);
        }
    }

    @Override
    public void caseAIdvarlistFactor(AIdvarlistFactor node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
    }

    @Override
    public void caseALastFactor(ALastFactor node) {
        if (node.getArrayOrId() != null) {
            node.getArrayOrId().apply(this);
        }
        if (node.getPeriod() != null) {
            node.getPeriod().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getVarListTwo() != null) {
            node.getVarListTwo().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
    }

    @Override
    public void caseAArrayArrayOrId(AArrayArrayOrId node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getLbracket() != null) {
            node.getLbracket().apply(this);
        }
        if (node.getInt() != null) {
            node.getInt().apply(this);
        }
        if (node.getRbracket() != null) {
            node.getRbracket().apply(this);
        }
    }

    @Override
    public void caseAIdArrayOrId(AIdArrayOrId node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
    }

    @Override
    public void caseATrueBoolean(ATrueBoolean node) {
        if (node.getTrue() != null) {
            node.getTrue().apply(this);
        }
    }

    @Override
    public void caseAFalseBoolean(AFalseBoolean node) {
        if (node.getFalse() != null) {
            node.getFalse().apply(this);
        }
    }

    @Override
    public void caseAConditionalBoolean(AConditionalBoolean node) {
        if (node.getFirst() != null) {
            node.getFirst().apply(this);
        }
        if (node.getCond() != null) {
            node.getCond().apply(this);
        }
        if (node.getSec() != null) {
            node.getSec().apply(this);
        }
    }

    @Override
    public void caseABoolBoolid(ABoolBoolid node) {
        if (node.getBoolean() != null) {
            node.getBoolean().apply(this);
        }
    }

    @Override
    public void caseAIdBoolid(AIdBoolid node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
    }

    @Override
    public void caseAPlusAddop(APlusAddop node) {
        if (node.getPlusop() != null) {
            node.getPlusop().apply(this);
        }
    }

    @Override
    public void caseAMinusAddop(AMinusAddop node) {
        if (node.getNegop() != null) {
            node.getNegop().apply(this);
        }
    }

    @Override
    public void caseATypesType(ATypesType node) {
        if (node.getTypeDecl() != null) {
            node.getTypeDecl().apply(this);
        }
    }

    @Override
    public void caseAIdType(AIdType node) {
        if (node.getId() != null) {
            node.getId().apply(this);
        }
    }

    public void incScope(){
        HashMap<String, Symbol> notinitialized = new HashMap<String, Symbol>();
        symbolTables.add(notinitialized);
        currentScope++;
    }

    public void decScope(){
        try{
            symbolTables.remove(currentScope);
        } catch(Exception ex){};
        currentScope--;
    }

    public void addToSymbolTable(String idVal, Symbol val){
        symbolTables.get(currentScope).put(idVal, val);
    }

    public void addToSymbolTable(String idVal, Symbol val, int scope){
        symbolTables.get(scope).put(idVal, val);
    }

    public int getScope(String id) {
        int scope = -1;
        for(int i = currentScope; i >= 0; i--){
            if(symbolTables.get(i).containsKey(id)){
                scope = i;
                break;
            }
        }
        return scope;
    }

    public Symbol getSymbol(int scope, String id) {
        return symbolTables.get(scope).get(id);
    }

    public boolean isArray(Symbol symbol) {
        return symbol instanceof Array;
    }
}
