package FinalProject;
import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;
import java.lang.*;

/*
* Project 4
* Professor: Erik Krohn
* Authors: Ben Pink, Martin Mueller, Isaiah Ley
*/
class PrintTree extends DepthFirstAdapter {
    // Class variables
    private GlobalSet globalSet;
    private SymbolTable symbolTable;
    private MIPS mips;
    private int offset;
    private String breakLabel;
    private boolean isExprStringOrVoidOrBool;
    private int argsNum;
    private String type;
    /*
     * Constructor. Initializes non final class variables.
     */
    public PrintTree() {
        globalSet = new GlobalSet();
        symbolTable = new SymbolTable();
        mips = new MIPS();
        offset = 0;
        type = "REAL";
        breakLabel = "shouldNotShowUp";
        argsNum = 0;
    }

    @Override
    public void caseAProg(AProg node) {
        if (node.getBegin() != null) {
            node.getBegin().apply(this);
        }
        if (node.getClassmethodstmts() != null) {
            node.getClassmethodstmts().apply(this);
        }
        if (node.getEnd() != null) {
            node.getEnd().apply(this);
        }
        System.out.print(mips.getCode());
    }

    //seperates the global variables, methods, and classes
    @Override
    public void caseAClassStmtsClassmethodstmts(AClassStmtsClassmethodstmts node) {
        if (node.getClassmethodstmts() != null) {
            node.getClassmethodstmts().apply(this);
        }
        if (node.getClassmethodstmt() != null) {
            node.getClassmethodstmt().apply(this);
        }
    }

    //global class methods
    @Override
    public void caseAClassDefClassmethodstmt(AClassDefClassmethodstmt node) {
        String id = null;
        if (node.getClassLit() != null) {
            node.getClassLit().apply(this);
        }
        if (node.getId() != null) {
            id = node.getId().getText();
            if (globalSet.containsClass(id)) {
                errorClassAlreadyDeclared(id);
            } else {
                globalSet.addClass(id, new Class());
            }
            node.getId().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
            if (id != null) {
                globalSet.setCurrentClass(id);
            }
        }
        if (node.getMethodstmtseqs() != null) {
            node.getMethodstmtseqs().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
            if (id != null) {
                globalSet.clearCurrentClass();
            }
        }
    }

    //global method decl
    @Override
    public void caseAMethodDeclClassmethodstmt(AMethodDeclClassmethodstmt node) {
        int offset = this.offset;
        String id = node.getId().getText();
        boolean mainTwice = false;
        String type = checkType(node.getType());
        if (id.equals("MAIN")) {
            if(globalSet.containsFunction("MAIN")){
                mips.printError("MAIN has already been declared.");
                mainTwice = true;
            }
            if(!type.equals("VOID")){
                mips.printError(
                    String.format(
                        "Invalid return type for main method. " +
                        "Must be void, got %s.",
                        type
                    )
                );
            }
            if (!(node.getVarlist() instanceof AEpsilonVarlist)) {
                mips.printError(
                    String.format(
                        "Arguments not allowed in main method. " +
                        "Got %s.",
                        node.getVarlist()
                    )
                );
            }
            mips.setMain(true);
        } else if(globalSet.containsFunction(id) && mainTwice == false){
            mips.printError(
                String.format(
                    "Method %s has already been declared.",
                    id
                )
            );
        } else {
            globalSet.addFunction(id, new Function(mips.addLabel(), type));
        }

        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        globalSet.setCurrentFunction(id);
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
            symbolTable.incScope();
            if (!id.equals("MAIN")) {
                for(int i = 0; i < globalSet.getCurrentFunction().getSize(); i++) {
                    Symbol s = globalSet.getCurrentFunction().getSymbol(i);
                    if(s instanceof Variable){
                        ((Variable)s).initialize();
                    }
                    symbolTable.add(globalSet.getCurrentFunction().getSymbolName(i), s);
                }
            }
            node.getLcurly().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
            symbolTable.decScope();
        }
        if (id.equals("MAIN")) {
            mips.li("$v0", 10);
            mips.syscall();
            mips.setMain(false);
        } else {
            mips.jr("$ra");
        }
        globalSet.clearCurrentFunction();
    }

    //global variables
    @Override
    public void caseAVarDeclClassmethodstmt(AVarDeclClassmethodstmt node) {
        ArrayList<String> id = new ArrayList<String>();
        id.add(node.getId().getText());
        Symbol s = null;
        String type = checkType(node.getType());
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if (node.getMoreIds() instanceof AMoreIdsMoreIds) {
                childNode = node.getMoreIds();
                while (childNode instanceof AMoreIdsMoreIds) {
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for (int i = 0; i < id.size(); i++) {
                if (symbolTable.declaredAtCurrentScope(id.get(i))) {
                    errorVariableAlreadyDeclared(id.get(i));
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getSemicolon() != null) {
            for(int i = 0; i < id.size(); i++){
                symbolTable.add(id.get(i), new Variable(type, offset));
                offset -= 4;
            }
            node.getSemicolon().apply(this);
        }
    }

    //inside classes
    @Override
    public void caseAMethodStmtsMethodstmtseqs(AMethodStmtsMethodstmtseqs node) {
        if (node.getMethodstmtseqs() != null) {
            node.getMethodstmtseqs().apply(this);
        }
        if (node.getMethodstmtseq() != null) {
            node.getMethodstmtseq().apply(this);
        }
    }

    //method decl in classes
    @Override
    public void caseAMethodDeclMethodstmtseq(AMethodDeclMethodstmtseq node) {
        int offset = this.offset;
        String id = node.getId().getText();
        String type = checkType(node.getType());
        mips.addLabel();
        this.offset = 0;
        mips.addi("$sp", "$sp", offset);
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
            if (node.getId().getText().equals("MAIN")) {
                mips.printError(
                    "The main method may not be declared inside of a class."
                );
            } else if(globalSet.getCurrentClass().containsMethod(id)){
                mips.printError(
                    String.format(
                        "Method %s has already been declared.",
                        id
                    )
                );
            } else {
                Function newFunction = new Function(id, type);
                globalSet.getCurrentClass().addMethod(id, newFunction);
                globalSet.getCurrentClass().setCurrentMethod(id);
            }
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
            symbolTable.incScope();
            node.getLcurly().apply(this);
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
            symbolTable.decScope();
        }
        this.offset = offset;
        mips.addi("$sp", "$sp", -1 * offset);
        mips.jr("$ra");
        globalSet.getCurrentClass().clearCurrentMethod();
    }

    //var decl in a class
    @Override
    public void caseAVarDeclMethodstmtseq(AVarDeclMethodstmtseq node) {
        ArrayList<String> id = new ArrayList<String>();
        Symbol s = null;
        String type = checkType(node.getType());
        if (node.getId() != null) {
            id.add(node.getId().getText());
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if (node.getMoreIds() instanceof AMoreIdsMoreIds) {
                childNode = node.getMoreIds();
                while (childNode instanceof AMoreIdsMoreIds) {
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for (int i = 0; i < id.size(); i++) {
                if (symbolTable.declaredAtCurrentScope(id.get(i))) {
                    errorVariableAlreadyDeclared(id.get(i));
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getSemicolon() != null) {
            for (int i = 0; i < id.size(); i++) {
                symbolTable.add(id.get(i), new Variable(type, offset));
                offset -= 4;
            }
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignEqualsMethodstmtseq(AAssignEqualsMethodstmtseq node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
        if (index > -1) {
            type = s.getType();
        }
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
        if (index > -1) {
            if (isArray(s)) {
                mips.lw("$t0", s.getOffset(), "$sp");
                if (type.equals("REAL")) {
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.sw("$s0", 4 * index, "$t0");
                }
                ((Array) s).initializeAt(index);
            } else {
                if (type.equals("REAL")) {
                    mips.swc1("$f0", s.getOffset(), "$sp");
                } else {
                    mips.sw("$s0", s.getOffset(), "$sp");
                }
                ((Variable) s).initialize();
            }
        }
    }

    @Override
    public void caseAAssignStringMethodstmtseq(AAssignStringMethodstmtseq node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (type.equals("STRING")) {
                mips.la("$t0", mips.addString(node.getAnychars().getText()));
                if (isArray(s)) {
                    mips.lw("$t1", s.getOffset(), "$sp");
                    mips.sw("$t0", 4 * index, "$t1");
                    ((Array) s).initializeAt(index);
                } else {
                    mips.sw("$t0", s.getOffset(), "$sp");
                    ((Variable) s).initialize();
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s is type %s. " +
                        "Must be type STRING.",
                        id,
                        s.getType()
                    )
                );
            }
        }
    }

    @Override
    public void caseAPrintStmtMethodstmtseq(APrintStmtMethodstmtseq node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            switch (s.getType()) {
                case "REAL":
                    mips.li("$v0", 2);
                    if (isArray(s)) {
                        mips.lwc1("$f12", 4 * index, "$t0");
                    } else {
                        mips.lwc1("$f12", s.getOffset(), "$sp");
                    }
                    break;
                case "STRING":
                    mips.li("$v0", 4);
                    if (isArray(s)) {
                        mips.lw("$a0", 4 * index, "$t0");
                    } else {
                        mips.lw("$a0", s.getOffset(), "$sp");
                    }
                    break;
                case "BOOLEAN":
                    mips.li("$v0", 4);
                    if (isArray(s)) {
                        mips.lw("$t0", 4 * index, "$t0");
                    } else {
                        mips.lw("$t0", s.getOffset(), "$sp");
                    }
                    String falselabel = mips.getLabel();
                    mips.incLabel();
                    String endlabel   = mips.getLabel();
                    mips.incLabel();
                    mips.beq("$zero", "$t0", falselabel);
                    mips.la("$a0", "TRUE");
                    mips.j(endlabel);
                    mips.addLabel(falselabel);
                    mips.la("$a0", "FALSE");
                    mips.addLabel(endlabel);
                    break;
                default:
                    mips.li("$v0", 1);
                    if (isArray(s)) {
                        mips.lw("$a0", 4 * index, "$t0");
                    } else {
                        mips.lw("$a0", s.getOffset(), "$sp");
                    }
            }
            mips.syscall();
            // Print newline
            mips.li("$v0", 11);
            mips.li("$a0", 0xA);
            mips.syscall();
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
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (s.getType().equals("INT") || s.getType().equals("VOID")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lw("$t1", 4 * index, "$t0");
                    mips.addi("$t1", "$t1", 1);
                    mips.sw("$t1", 4 * index, "$t0");
                } else {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.addi("$t0", "$t0", 1);
                    mips.sw("$t0", s.getOffset(), "$sp");
                }
            } else if (s.getType().equals("REAL")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lwc1("$f0", 4 * index, "$t0");
                    mips.li("$t1", Float.floatToIntBits((float) 1.0));
                    mips.mtc1("$t1", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.lwc1("$f0", s.getOffset(), "$sp");
                    mips.li("$t0", Float.floatToIntBits((float) 1.0));
                    mips.mtc1("$t0", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", s.getOffset(), "$sp");
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has type %s " +
                        "which cannot be incremented.",
                        id,
                        s.getType()
                    )
                );
            }
        }
    }

    @Override
    public void caseAAssignDecMethodstmtseq(AAssignDecMethodstmtseq node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (s.getType().equals("INT") || s.getType().equals("VOID")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lw("$t1", 4 * index, "$t0");
                    mips.addi("$t1", "$t1", -1);
                    mips.sw("$t1", 4 * index, "$t0");
                } else {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.addi("$t0", "$t0", -1);
                    mips.sw("$t0", s.getOffset(), "$sp");
                }
            } else if (s.getType().equals("REAL")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lwc1("$f0", 4 * index, "$t0");
                    mips.li("$t1", Float.floatToIntBits((float) -1.0));
                    mips.mtc1("$t1", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.lwc1("$f0", s.getOffset(), "$sp");
                    mips.li("$t0", Float.floatToIntBits((float) -1.0));
                    mips.mtc1("$t0", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", s.getOffset(), "$sp");
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has type %s " +
                        "which cannot be decremented.",
                        id,
                        s.getType()
                    )
                );
            }
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
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
        if (s != null) {
            type = s.getType();
        }
        if (index > -1) {
            type = s.getType();
        }
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
        if (index > -1) {
            if (isArray(s)) {
                mips.lw("$t0", s.getOffset(), "$sp");
                if (type.equals("REAL")) {
                    mips.cvt_w_s("$f0", "$f0");
                    mips.mfc1("$f0", "$s0");
                }
                mips.sw("$s0", 4 * index, "$t0");
                ((Array) s).initializeAt(index);
            } else {
                if (type.equals("REAL")) {
                    mips.cvt_w_s("$f0", "$f0");
                    mips.mfc1("$f0", "$s0");
                }
                mips.sw("$s0", s.getOffset(), "$sp");
                ((Variable) s).initialize();
            }
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
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
        if (index > -1) {
            type = s.getType();
        }
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
        if (index > -1) {
            if (isArray(s)) {
                mips.lw("$t0", s.getOffset(), "$sp");
                if (s.getType().equals("REAL")) {
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.sw("$s0", 4 * index, "$t0");
                }
                ((Array) s).initializeAt(index);
            } else {
                if (s.getType().equals("REAL")) {
                    mips.swc1("$f0", s.getOffset(), "$sp");
                } else {
                    mips.sw("$s0", s.getOffset(), "$sp");
                }
                ((Variable) s).initialize();
            }
            symbolTable.add(id, s);
        }
    }

    @Override
    public void caseAAssignStringStmt(AAssignStringStmt node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (s.getType().equals("STRING")) {
                mips.la("$t0", mips.addString(node.getAnychars().getText()));
                if (isArray(s)) {
                    mips.lw("$t1", s.getOffset(), "$sp");
                    mips.sw("$t0", 4 * index, "$t1");
                    ((Array) s).initializeAt(index);
                } else {
                    mips.sw("$t0", s.getOffset(), "$sp");
                    ((Variable) s).initialize();
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s is type %s. " +
                        "Must be type STRING.",
                        id,
                        s.getType()
                    )
                );
            }
        }
    }

    @Override
    public void caseAVarDeclStmt(AVarDeclStmt node) {
        ArrayList<String> id = new ArrayList<String>();
        id.add(node.getId().getText());
        String type = checkType(node.getType());
        int size = -1;
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if (node.getMoreIds() instanceof AMoreIdsMoreIds) {
                childNode = node.getMoreIds();
                while (childNode instanceof AMoreIdsMoreIds) {
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for (int i = 0; i < id.size(); i++) {
                if (symbolTable.declaredAtCurrentScope(id.get(i))) {
                    errorVariableAlreadyDeclared(id.get(i));
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (node.getArrayOption() instanceof AArrayArrayOption) {
                size = Integer.parseInt(
                    ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                );
                if (size < 1) {
                    mips.printError(
                        String.format(
                            "Size %d is invalid for array %s.",
                            size,
                            id
                        )
                    );
                    size = -1;
                }
            } else {
                size = 0;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if (size >= 1) {
            for (int i = 0; i < id.size(); i++) {
                mips.la("$t0", mips.addWords(size));
                mips.sw("$t0", offset, "$sp");
                symbolTable.add(id.get(i), new Array(type, offset, size));
                offset -= 4;
            }
        } else if (size == 0) {
            for (int i = 0; i < id.size(); i++) {
                symbolTable.add(id.get(i), new Variable(type, offset));
                offset -= 4;
            }
        }
    }

    @Override
    public void caseAIfBlockStmt(AIfBlockStmt node) {
        String falselabel = mips.getLabel();
        boolean isConstant = false;
        boolean constant = false;
        Symbol s = null;
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            node.getBoolid().apply(this);
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().getText();
                s = getSymbol(id);
                if (s != null) {
                    if (s.getType().equals("BOOLEAN")) {
                        mips.incLabel();
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.beq("$zero", "$t0", falselabel);
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to BOOLEAN.",
                                id,
                                s.getType()
                            )
                        );
                    }
                }
            } else {
                ABoolBoolid node2 = (ABoolBoolid) node.getBoolid();
                if (node2.getBoolean() instanceof ATrueBoolean){
                    isConstant = true;
                    constant = true;
                } else if (node2.getBoolean() instanceof AFalseBoolean) {
                    isConstant = true;
                } else if (node2.getBoolean() instanceof AConditionalBoolean) {
                    mips.incLabel();
                    mips.beq("$zero", "$s0", falselabel);
                }
            }
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getThen() != null) {
            node.getThen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
            symbolTable.incScope();
        }
        if ((node.getStmtseq() != null)
            && (!isConstant || constant)) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
            if (!isConstant) {
                mips.addLabel(falselabel);
            }
            symbolTable.decScope();
        }
    }

    @Override
    public void caseAIfElseBlockStmt(AIfElseBlockStmt node) {
        String falselabel = mips.getLabel();
        mips.incLabel();
        String endlabel = mips.getLabel();
        boolean isConstant = false;
        boolean constant = false;
        Symbol s = null;
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().getText();
                s = getSymbol(id);
                if (s == null) {
                    mips.decLabel();
                } else {
                    mips.incLabel();
                    if(s.getType().equals("BOOLEAN")){
                        mips.decLabel();
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to BOOLEAN.",
                                id,
                                s.getType()
                            )
                        );
                    }
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.beq("$zero", "$t0", falselabel);
                }
            } else {
                ABoolBoolid node2 = (ABoolBoolid) node.getBoolid();
                if (node2.getBoolean() instanceof ATrueBoolean) {
                    isConstant = true;
                    constant = true;
                    mips.decLabel();
                } else if (node2.getBoolean() instanceof AFalseBoolean) {
                    isConstant = true;
                    mips.decLabel();
                } else if (node2.getBoolean() instanceof AConditionalBoolean) {
                    mips.beq("$zero", "$s0", falselabel);
                    mips.incLabel();
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
            symbolTable.incScope();
            node.getIflcurly().apply(this);
        }
        if (node.getIfBlockStmts() != null) {
            if (constant) {
                node.getIfBlockStmts().apply(this);
            }
        }
        if (node.getIfrcurly() != null) {
            if (!isConstant) {
                mips.j(endlabel);
            }
            node.getIfrcurly().apply(this);
            symbolTable.decScope();
        }
        if (node.getElse() != null) {
            node.getElse().apply(this);
        }
        if (node.getElselcurly() != null) {
            node.getElselcurly().apply(this);
            symbolTable.incScope();
        }
        if (node.getElseBlockStmts() != null) {
            if (!isConstant) {
                mips.addLabel(falselabel);
            }
            if (!isConstant || !constant) {
                node.getElseBlockStmts().apply(this);
            }
        }
        if (node.getElsercurly() != null) {
            node.getElsercurly().apply(this);
            if (!isConstant) {
                mips.addLabel(endlabel);
            }
            symbolTable.decScope();
        }
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node) {
        String truelabel = mips.getLabel();
        mips.incLabel();
        String falselabel = mips.getLabel();
        boolean isConstant = false;
        boolean constant = false;
        Symbol s = null;
        if (node.getWhile() != null) {
            node.getWhile().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if (node.getBoolid() instanceof AIdBoolid) {
                String id = ((AIdBoolid) node.getBoolid()).getId().getText();
                s = getSymbol(id);
                if (s == null) {
                    mips.decLabel();
                } else {
                    if (s.getType().equals("BOOLEAN")) {
                        mips.incLabel();
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.beq("$zero", "$t0", falselabel);
                        mips.addLabel(truelabel);
                    } else {
                        mips.decLabel();
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to BOOLEAN.",
                                id,
                                s.getType()
                            )
                        );
                    }
                }
            } else {
                ABoolBoolid ABoolBoolidNode = (ABoolBoolid) node.getBoolid();
                if ((ABoolBoolidNode.getBoolean()) instanceof ATrueBoolean) {
                    mips.addLabel(truelabel);
                    isConstant = true;
                    constant = true;
                } else if ((ABoolBoolidNode.getBoolean()) instanceof AFalseBoolean) {
                    mips.decLabel();
                    isConstant = true;
                } else if ((ABoolBoolidNode.getBoolean()) instanceof AConditionalBoolean) {
                    mips.incLabel();
                    mips.beq("$zero", "$s0", falselabel);
                }
            }
            node.getBoolid().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getLcurly() != null) {
            node.getLcurly().apply(this);
            symbolTable.incScope();
        }
        if (node.getStmtseq() != null) {
            if(isConstant && constant) {
                node.getStmtseq().apply(this);
                mips.j(truelabel);
            } else if (!isConstant) {
                node.getStmtseq().apply(this);
                mips.lw("$t0", s.getOffset(), "$sp");
                mips.bne("$zero", "$t0", truelabel);
                mips.j(falselabel);
            }
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
            if (!isConstant) {
                mips.addLabel(falselabel);
            }
            symbolTable.decScope();
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
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (isArray(s)) {
                switch (s.getType()) {
                    case "REAL":
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.li("$v0", 6);
                        mips.syscall();
                        mips.swc1("$f0", 4 * index, "$t0");
                        break;
                    case "STRING":
                        String label = mips.addWords(100);
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.li("$v0", 8);
                        mips.la("$a0", label);
                        mips.li("$a1", 399);
                        mips.syscall();
                        mips.la("$t1", label);
                        mips.sw("$t1", 4 * index, "$t0");
                        break;
                    case "BOOLEAN":
                        String falselabel = mips.getLabel();
                        mips.incLabel();
                        String endlabel   = mips.getLabel();
                        mips.incLabel();
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.li("$v0", 5);
                        mips.syscall();
                        mips.beq("$zero", "$v0", falselabel);
                        mips.li("$t1", 1);
                        mips.j(endlabel);
                        mips.addLabel(falselabel);
                        mips.li("$t1", 0);
                        mips.addLabel(endlabel);
                        mips.sw("$t1", 4 * index, "$t0");
                        break;
                    default:
                        mips.lw("$t0", s.getOffset(), "$sp");
                        mips.li("$v0", 5);
                        mips.syscall();
                        mips.sw("$v0", 4 * index, "$t0");
                }
                ((Array) s).initializeAt(index);
            } else {
                switch (s.getType()) {
                    case "REAL":
                        mips.li("$v0", 6);
                        mips.syscall();
                        mips.swc1("$f0", s.getOffset(), "$sp");
                        break;
                    case "STRING":
                        String label = mips.addWords(100);
                        mips.li("$v0", 8);
                        mips.la("$a0", label);
                        mips.li("$a1", 399);
                        mips.syscall();
                        mips.la("$t0", label);
                        mips.sw("$t0", s.getOffset(), "$sp");
                        break;
                    case "BOOLEAN":
                        String falselabel = mips.getLabel();
                        mips.incLabel();
                        String endlabel   = mips.getLabel();
                        mips.incLabel();
                        mips.li("$v0", 5);
                        mips.syscall();
                        mips.beq("$zero", "$v0", falselabel);
                        mips.li("$t0", 1);
                        mips.j(endlabel);
                        mips.addLabel(falselabel);
                        mips.li("$t0", 0);
                        mips.addLabel(endlabel);
                        mips.sw("$t0", s.getOffset(), "$sp");
                        break;
                    default:
                        mips.li("$v0", 5);
                        mips.syscall();
                        mips.sw("$v0", s.getOffset(), "$sp");
                }
                ((Variable) s).initialize();
            }
        }
    }

    @Override
    public void caseAPutStmt(APutStmt node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            switch (s.getType()) {
                case "REAL":
                    mips.li("$v0", 2);
                    if (isArray(s)) {
                        mips.lwc1("$f12", 4 * index, "$t0");
                    } else {
                        mips.lwc1("$f12", s.getOffset(), "$sp");
                    }
                    break;
                case "STRING":
                    mips.li("$v0", 4);
                    if (isArray(s)) {
                        mips.lw("$a0", 4 * index, "$t0");
                    } else {
                        mips.lw("$a0", s.getOffset(), "$sp");
                    }
                    break;
                case "BOOLEAN":
                    mips.li("$v0", 4);
                    if (isArray(s)) {
                        mips.lw("$t0", 4 * index, "$t0");
                    } else {
                        mips.lw("$t0", s.getOffset(), "$sp");
                    }
                    String falselabel = mips.getLabel();
                    mips.incLabel();
                    String endlabel   = mips.getLabel();
                    mips.incLabel();
                    mips.beq("$zero", "$t0", falselabel);
                    mips.la("$a0", "TRUE");
                    mips.j(endlabel);
                    mips.addLabel(falselabel);
                    mips.la("$a0", "FALSE");
                    mips.addLabel(endlabel);
                    break;
                default:
                    mips.li("$v0", 1);
                    if (isArray(s)) {
                        mips.lw("$a0", 4 * index, "$t0");
                    } else {
                        mips.lw("$a0", s.getOffset(), "$sp");
                    }
            }
            mips.syscall();
            // Print newline
            mips.li("$v0", 11);
            mips.li("$a0", 0xA);
            mips.syscall();
        }
    }

    @Override
    public void caseAIncrStmt(AIncrStmt node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (s.getType().equals("INT") || s.getType().equals("VOID")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lw("$t1", 4 * index, "$t0");
                    mips.addi("$t1", "$t1", 1);
                    mips.sw("$t1", 4 * index, "$t0");
                } else {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.addi("$t0", "$t0", 1);
                    mips.sw("$t0", s.getOffset(), "$sp");
                }
            } else if (s.getType().equals("REAL")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lwc1("$f0", 4 * index, "$t0");
                    mips.li("$t1", Float.floatToIntBits((float) 1.0));
                    mips.mtc1("$t1", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.lwc1("$f0", s.getOffset(), "$sp");
                    mips.li("$t0", Float.floatToIntBits((float) 1.0));
                    mips.mtc1("$t0", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", s.getOffset(), "$sp");
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has type %s " +
                        "which cannot be incremented.",
                        id,
                        s.getType()
                    )
                );
            }
        }
    }

    @Override
    public void caseADecrStmt(ADecrStmt node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = getArrayOption(id, s, node.getArrayOption());
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
        if (index > -1) {
            if (s.getType().equals("INT") || s.getType().equals("VOID")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lw("$t1", 4 * index, "$t0");
                    mips.addi("$t1", "$t1", -1);
                    mips.sw("$t1", 4 * index, "$t0");
                } else {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.addi("$t0", "$t0", -1);
                    mips.sw("$t0", s.getOffset(), "$sp");
                }
            } else if (s.getType().equals("REAL")) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    mips.lwc1("$f0", 4 * index, "$t0");
                    mips.li("$t1", Float.floatToIntBits((float) -1.0));
                    mips.mtc1("$t1", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", 4 * index, "$t0");
                } else {
                    mips.lwc1("$f0", s.getOffset(), "$sp");
                    mips.li("$t0", Float.floatToIntBits((float) -1.0));
                    mips.mtc1("$t0", "$f1");
                    mips.add_s("$f0", "$f0", "$f1");
                    mips.swc1("$f0", s.getOffset(), "$sp");
                }
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has type %s " +
                        "which cannot be decremented.",
                        id,
                        s.getType()
                    )
                );
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
        Function curFunction = null;
        if (node.getId() != null) {
            node.getId().apply(this);
            if(globalSet.containsFunction(node.getId().getText())){
                curFunction = globalSet.getFunction(node.getId().getText());
                globalSet.setCurrentFunction(node.getId().getText());
            } else {
                mips.printError(
                    String.format(
                        "Method call %s has not been declared.",
                        node.getId().getText()
                    )
                );
            }
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
        argsNum = 0;
        globalSet.clearCurrentFunction();
        if(curFunction != null){
            mips.jal(globalSet.getFunction(node.getId().getText()).getLabel());
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
        String name = globalSet.getCurrentClass() == null ?
            globalSet.getCurrentFunctionName() :
            globalSet.getCurrentClass().getCurrentMethodName();
        String returnType = globalSet.getCurrentClass() == null ?
            globalSet.getCurrentFunction().getReturnType() :
            globalSet.getCurrentClass().getCurrentMethod().getReturnType();
        type = returnType;
        if (node.getReturn() != null) {
            node.getReturn().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
        if (returnType.equals("REAL")) {
            mips.mfc1("$v0", "$f0");
        } else {
            mips.move("$v0", "$s0");
        }
    }

    @Override
    public void caseAAssignBooleanStmt(AAssignBooleanStmt node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = setArrayOption(id, s, node.getArrayOption());
        if (s != null) {
            type = s.getType();
        }
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
        if (index > -1) {
            if (isArray(s)) {
                mips.lw("$t0", s.getOffset(), "$sp");
                if (type.equals("REAL")) {
                    mips.cvt_w_s("$f0", "$f0");
                    mips.mfc1("$f0", "$s0");
                }
                mips.sw("$s0", 4 * index, "$t0");
                ((Array) s).initializeAt(index);
            } else {
                if (type.equals("REAL")) {
                    mips.cvt_w_s("$f0", "$f0");
                    mips.mfc1("$f0", "$s0");
                }
                mips.sw("$s0", s.getOffset(), "$sp");
                ((Variable) s).initialize();
            }
        }
    }

    @Override
    public void caseASwitchStmt(ASwitchStmt node) {
        breakLabel = mips.getLabel();
        mips.incLabel();
        String caseOneLabel = mips.getLabel();
        mips.incLabel();
        String afterCaseOneLabel = mips.getLabel();
        mips.incLabel();
        if (node.getSwitch() != null) {
            node.getSwitch().apply(this);
        }
        if (node.getFirst() != null) {
            node.getFirst().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
            mips.move("$s1", "$s0");
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
            mips.li("$t0", Integer.parseInt(node.getInt().getText()));
            mips.beq("$s1", "$t0", caseOneLabel);
            mips.j(afterCaseOneLabel);
            node.getInt().apply(this);
        }
        if (node.getFourth() != null) {
            node.getFourth().apply(this);
        }
        if (node.getFifth() != null) {
            node.getFifth().apply(this);
        }
        if (node.getStmts() != null) {
            mips.addLabel(caseOneLabel);
            node.getStmts().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            mips.j(afterCaseOneLabel);
            mips.addLabel(afterCaseOneLabel);
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
            mips.addLabel(breakLabel);
        }
    }

    @Override
    public void caseAAnotherCaseCaseHelper(AAnotherCaseCaseHelper node) {
        String caseNLabel = mips.getLabel();
        mips.incLabel();
        String afterCaseNLabel = mips.getLabel();
        mips.incLabel();
        if (node.getCase() != null) {
            node.getCase().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getInt() != null) {
            mips.li("$t0", Integer.parseInt(node.getInt().getText()));
            mips.beq("$s1", "$t0", caseNLabel);
            mips.j(afterCaseNLabel);
            node.getInt().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getStmtseq() != null) {
            mips.addLabel(caseNLabel);
            node.getStmtseq().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            mips.j(afterCaseNLabel);
            mips.addLabel(afterCaseNLabel);
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
        mips.j(breakLabel);
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
        String idVal = "";
        Symbol s = null;
        String type = "";
        int size = -1;
        if (node.getId() != null) {
            idVal = node.getId().getText();
            node.getId().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            if (node.getType() instanceof ATypesType) {
                type = ((ATypesType) node.getType()).getTypeDecl().getText();
            } else {
                mips.printError(
                    String.format(
                        "Invalid type %s.",
                        ((AIdType) node.getType()).getId().getText()
                    )
                );
            }
            node.getType().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (node.getArrayOption() instanceof AArrayArrayOption) {
                size = Integer.parseInt(
                    ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                );
                if (size < 1) {
                    mips.printError(
                        String.format(
                            "Size %d is invalid for array %s.",
                            size,
                            idVal
                        )
                    );
                    size = -1;
                }
            } else {
                size = 0;
            }
            node.getArrayOption().apply(this);
        }
        if (size >= 1) {
            mips.la("$t0", mips.addWords(size));
            mips.sw("$t0", offset, "$sp");
            globalSet.getCurrentFunction().addSymbol(idVal, new Array(type, offset, size));
            offset -= 4;
        } else if (size == 0) {
            globalSet.getCurrentFunction().addSymbol(idVal, new Variable(type, offset));
            offset -= 4;
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
            Function currentFunction = globalSet.getCurrentFunction();
            Symbol currentSymbol = currentFunction.getSymbol(argsNum);
            if(currentSymbol.getType().equals("REAL")){
                mips.swc1("$f0", currentSymbol.getOffset(), "$sp");
            } else {
                mips.sw("$s0", currentSymbol.getOffset(), "$sp");
            }
            argsNum++;
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
        boolean addition = node.getAddop() instanceof APlusAddop;
        if (!(type.equals("INT") || type.equals("REAL"))) {
            mips.printError(
                String.format(
                    "Expecting type %s, not INT or REAL.",
                    type
                )
            );
        }
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (type.equals("REAL")) {
                mips.swc1("$f0", offset, "$sp");
            } else {
                mips.sw("$s0", offset, "$sp");
            }
            offset -= 4;
        }
        if (node.getAddop() != null) {
            node.getAddop().apply(this);
        }
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
            offset += 4;
            if (type.equals("REAL")) {
                mips.l_s("$f1", offset, "$sp");
                if (addition) {
                    mips.add_s("$f0", "$f1", "$f0");
                } else {
                    mips.sub_s("$f0", "$f1", "$f0");
                }
            } else {
                mips.lw("$t0", offset, "$sp");
                if (addition) {
                    mips.add("$s0", "$t0", "$s0");
                } else {
                    mips.sub("$s0", "$t0", "$s0");
                }
            }
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
        boolean multiplication = node.getMultop().getText().equals("*");
        if (!(type.equals("INT") || type.equals("REAL"))) {
            mips.printError(
                String.format(
                    "Expecting type %s, not INT or REAL.",
                    type
                )
            );
        }
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
            if (type.equals("REAL")) {
                mips.swc1("$f0", offset, "$sp");
            } else {
                mips.sw("$s0", offset, "$sp");
            }
            offset -= 4;
        }
        if (node.getMultop() != null) {
            node.getMultop().apply(this);
        }
        if (node.getFactor() != null) {
            node.getFactor().apply(this);
            offset += 4;
            if (type.equals("REAL")) {
                mips.l_s("$f1", offset, "$sp");
                if (multiplication) {
                    mips.mul_s("$f0", "$f1", "$f0");
                } else {
                    mips.div_s("$f0", "$f1", "$f0");
                }
            } else {
                mips.lw("$t0", offset, "$sp");
                if (multiplication) {
                    mips.mul("$s0", "$t0", "$s0");
                } else {
                    mips.div("$s0", "$t0", "$s0");
                }
            }
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
            if (type.equals("REAL")) {
                mips.li("$t0", -1);
                mips.mul("$s0", "$t0", "$s0");
            } else {
                mips.li("$t2", Float.floatToIntBits((float) -1.0));
                mips.mtc1("$t2", "$f1");
                mips.mul_s("$f0", "$f1", "$f0");
            }
            node.getFactor().apply(this);
        }
    }

    @Override
    public void caseAIntFactor(AIntFactor node) {
        if (node.getInt() != null) {
            if (type.equals("REAL")) {
                mips.li(
                    "$t0",
                    Float.floatToIntBits(
                        Float.parseFloat(
                            node.getInt().getText()
                        )
                    )
                );
                mips.mtc1("$t0", "$f0");
            } else {
                mips.li(
                    "$s0",
                    Integer.parseInt(
                        node.getInt().getText()
                    )
                );
            }
            node.getInt().apply(this);
        }
    }

    @Override
    public void caseARealFactor(ARealFactor node) {
        if (node.getReal() != null) {
            if (!type.equals("REAL")) {
                mips.printError(
                    String.format(
                        "Expecting type %s, not REAL.",
                        type
                    )
                );
            }
            mips.li(
                "$t1",
                Float.floatToIntBits(
                    Float.parseFloat(
                        node.getReal().getText()
                    )
                )
            );
            mips.mtc1("$t1", "$f0");
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
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        int index = Integer.parseInt(node.getInt().getText());
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
        if (s != null) {
            if (wider(type, s.getType())) {
                if ((index >= 0) && (index < ((Array) s).getSize())) {
                    if (((Array) s).isInitializedAt(index)) {
                        mips.lw("$t0", s.getOffset(), "$sp");
                        if (type.equals("REAL")) {
                            mips.lwc1("$f0", 4 * index, "$t0");
                            if (!s.getType().equals("REAL")) {
                                mips.cvt_s_w("$f0", "$f0");
                            }
                        } else {
                            mips.lw("$s0", 4 * index, "$t0");
                        }
                    } else {
                        mips.printError(
                            String.format(
                                "Array %s has not been " +
                                "initialized at index %d.",
                                id,
                                index
                            )
                        );
                    }
                } else {
                    mips.printError(
                        String.format(
                            "Index %d is not valid for array %s.",
                            index,
                            id
                        )
                    );
                }
            } else {
                mips.printError(
                    String.format(
                        "Expecting type compatible with %s. " +
                        "Array %s is type %s.",
                        type,
                        id,
                        s.getType()
                    )
                );
            }
        }
    }

    @Override
    public void caseAIdArrayOrId(AIdArrayOrId node) {
        String id = node.getId().getText();
        Symbol s = getSymbol(id);
        if (node.getId() != null) {
            if (s != null) {
                if (wider(type, s.getType())) {
                    if (((Variable) s).isInitialized()) {
                        if (type.equals("REAL")) {
                            mips.lwc1("$f0", s.getOffset(), "$sp");
                            if (!s.getType().equals("REAL")) {
                                mips.cvt_s_w("$f0", "$f0");
                            }
                        } else {
                            mips.lw("$s0", s.getOffset(), "$sp");
                        }
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s has not been initialized.",
                                id
                            )
                        );
                    }
                } else {
                    mips.printError(
                        String.format(
                            "Expecting type compatible with %s. " +
                            "Variable %s is type %s.",
                            type,
                            id,
                            s.getType()
                        )
                    );
                }
            }
            node.getId().apply(this);
        }
    }

    @Override
    public void caseATrueBoolean(ATrueBoolean node) {
        if (node.getTrue() != null) {
            if (type.equals("REAL")) {
                mips.li("$t0", Float.floatToIntBits((float) 1.0));
                mips.mtc1("$f0", "$t0");
            } else {
                mips.li("$s0", 1);
            }
            node.getTrue().apply(this);
        }
    }

    @Override
    public void caseAFalseBoolean(AFalseBoolean node) {
        if (node.getFalse() != null) {
            if (type.equals("REAL")) {
                mips.li("$t0", Float.floatToIntBits((float) 0.0));
                mips.mtc1("$f0", "$t0");
            } else {
                mips.li("$s0", 0);
            }
            node.getFalse().apply(this);
        }
    }

    @Override
    public void caseAConditionalBoolean(AConditionalBoolean node) {
        if (node.getFirst() != null) {
            type = "REAL";
            node.getFirst().apply(this);
            mips.swc1("$f0", offset, "$sp");
            offset -= 4;
        }
        if (node.getCond() != null) {
            node.getCond().apply(this);
        }
        if (node.getSec() != null) {
            node.getSec().apply(this);
            if (type.equals("REAL")) {
                mips.lwc1("$f1", offset, "$sp");
                switch (node.getCond().getText()) {
                    case "==":
                        mips.c_eq_s("$f1", "$f0");
                        break;
                    case "!=":
                        mips.c_ne_s("$f1", "$f0");
                        break;
                    case ">":
                        mips.c_lt_s("$f0", "$f1");
                        break;
                    case "<":
                        mips.c_lt_s("$f1", "$f0");
                        break;
                    case ">=":
                        mips.c_le_s("$f0", "$f1");
                        break;
                    case "<=":
                        mips.c_le_s("$f1", "$f0");
                        break;
                    default:
                        mips.printError("Invalid condition.");
                }
                mips.li("$s0", 1);
                mips.movf("$s0", "$zero");
            } else {
                mips.lw("$t0", offset, "$sp");
                switch (node.getCond().getText()) {
                    case "==":
                        mips.seq("$s0", "$t0", "$s0");
                        break;
                    case "!=":
                        mips.sne("$s0", "$t0", "$s0");
                        break;
                    case ">":
                        mips.sgt("$s0", "$t0", "$s0");
                        break;
                    case "<":
                        mips.slt("$s0", "$t0", "$s0");
                        break;
                    case ">=":
                        mips.sge("$s0", "$t0", "$s0");
                        break;
                    case "<=":
                        mips.sle("$s0", "$t0", "$s0");
                        break;
                    default:
                        mips.printError("Invalid condition.");
                }
            }
            offset += 4;
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

    private boolean isArray(Symbol symbol) {
        return symbol instanceof Array;
    }

    private void errorClassAlreadyDeclared(String id) {
        mips.printError(
            String.format(
                "Class %s has already been declared.",
                id
            )
        );
    }
    private void errorFunctionAlreadyDeclared(String id) {
        mips.printError(
            String.format(
                "Function %s has already been declared.",
                id
            )
        );
    }
    private void errorMethodAlreadyDeclared(String id) {
        mips.printError(
            String.format(
                "Method %s has already been declared.",
                id
            )
        );
    }
    private void errorVariableAlreadyDeclared(String id) {
        mips.printError(
            String.format(
                "Variable %s has already been declared.",
                id
            )
        );
    }
    private Symbol getSymbol(String id) {
        Symbol s = symbolTable.getSymbol(id);
        if (s == null) {
            mips.printError(
                String.format(
                    "Variable %s has not been declared.",
                    id
                )
            );
        }
        return s;
    }
    private int setArrayOption(String id, Symbol s, PArrayOption ao) {
        if (s != null) {
            if (isArray(s)) {
                if (ao instanceof AArrayArrayOption) {
                    int index = Integer.parseInt(
                        ((AArrayArrayOption) ao).getInt().getText()
                    );
                    if (((Array) s).isOutOfBounds(index)) {
                        mips.printError(
                            String.format(
                                "Index %d is invalid for array %s.",
                                index,
                                id
                            )
                        );
                    } else {
                        return index;
                    }
                } else {
                    mips.printError(
                        String.format(
                            "No index specified for array %s.",
                            id
                        )
                    );
                }
            } else {
                if (ao instanceof AEpsilonArrayOption) {
                    return 0;
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s is not an array " +
                            "and may not be given an index.",
                            id
                        )
                    );
                }
            }
        }
        return -1;
    }
    private int getArrayOption(String id, Symbol s, PArrayOption ao) {
        int index = setArrayOption(id, s, ao);
        if (index > -1) {
            if (isArray(s)) {
                if (((Array) s).isInitializedAt(index)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                } else {
                    mips.printError(
                        String.format(
                            "Array %s has not been " +
                            "initialized at index %d.",
                            id,
                            index
                        )
                    );
                    index = -1;
                }
            } else if (!((Variable) s).isInitialized()) {
                mips.printError(
                    String.format(
                        "Variable %s has not been initialized.",
                        id
                    )
                );
                index = -1;
            }
        }
        return index;
    }

    private void errorWrongReturnType(String functionName, String returnType) {
        mips.printError(
            String.format(
                "Wrong return type for function %s. " +
                "Expected %s.",
                functionName,
                returnType
            )
        );
    }

    private String checkType(PType t) {
        String type = t instanceof AIdType
            ? ((AIdType) t).getId().getText()
            : ((ATypesType) t).getTypeDecl().getText();
        if ((t instanceof AIdType)
                && (!globalSet.containsClass(type))) {
            mips.printError(
                String.format(
                    "Invalid type %s.",
                    type
                )
            );
        }
        return type;
    }

    private boolean wider(String wide, String narrow) {
        if (wide.equals(narrow)) {
            return true;
        } else if (wide.equals("REAL") &&
                (narrow.equals("INT") || narrow.equals("BOOLEAN"))) {
            return true;
        } else if (wide.equals("INT") && narrow.equals("BOOLEAN")) {
            return true;
        }
        return false;
    }
}
