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
    private boolean isFloat;
    private String breakLabel;
    /*
     * Constructor. Initializes non final class variables.
     */
    public PrintTree() {
        globalSet = new GlobalSet();
        symbolTable = new SymbolTable();
        mips = new MIPS();
        offset = 0;
        isFloat = false;
        breakLabel = "shouldNotShowUp";
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
                mips.printError(
                    String.format(
                        "Class %s has already been declared.",
                        id
                    )
                );
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
        String type = node.getType() instanceof AIdType
            ? ((AIdType) node.getType()).getId().getText()
            : ((ATypesType) node.getType()).getTypeDecl().getText();
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
        } else {
            mips.addLabel();
            this.offset = 0;
            mips.addi("$sp", "$sp", offset);
        }
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            node.getId().apply(this);
        }
        if(globalSet.containsFunction(id) && mainTwice == false){
            mips.printError(
                String.format(
                    "Method %s has already been declared.",
                    id
                )
            );
        }
        Function newFunction = new Function(id, type);
        globalSet.addFunction(id, newFunction);
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
            this.offset = offset;
            mips.addi("$sp", "$sp", -1 * offset);
            mips.jr("$ra");
        }
        globalSet.clearCurrentFunction();
    }

    //global variables
    @Override
    public void caseAVarDeclClassmethodstmt(AVarDeclClassmethodstmt node) {
        ArrayList<String> id = new ArrayList<String>();
        Symbol s = null;
        String type = "";
        if (node.getId() != null) {
            id.add(node.getId().getText());
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if(node.getMoreIds() instanceof AMoreIdsMoreIds){
                childNode = node.getMoreIds();
                while(childNode instanceof AMoreIdsMoreIds){
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for(int i = 0; i < id.size(); i++){
                if(symbolTable.declaredAtCurrentScope(id.get(i))){
                    mips.printError(
                        String.format(
                            "Variable %s has already been declared in this scope.",
                            id.get(i)
                        )
                    );
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            if(node.getType() instanceof ATypesType){
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
        String type = node.getType() instanceof AIdType
            ? ((AIdType) node.getType()).getId().getText()
            : ((ATypesType) node.getType()).getTypeDecl().getText();
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
            }
            else if(globalSet.getCurrentClass().containsMethod(id)){
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
        String type = "";
        if (node.getId() != null) {
            id.add(node.getId().getText());
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if(node.getMoreIds() instanceof AMoreIdsMoreIds){
                childNode = node.getMoreIds();
                while(childNode instanceof AMoreIdsMoreIds){
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for(int i = 0; i < id.size(); i++){
                if(symbolTable.declaredAtCurrentScope(id.get(i))){
                    mips.printError(
                        String.format(
                            "Variable %s has already been declared in this scope.",
                            id.get(i)
                        )
                    );
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            if(node.getType() instanceof ATypesType){
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
        if (node.getSemicolon() != null) {
            for(int i = 0; i < id.size(); i++){
                symbolTable.add(id.get(i), new Variable(type, offset));
                offset -= 4;
            }
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignEqualsMethodstmtseq(AAssignEqualsMethodstmtseq node) {
        String id = "";
        Symbol s = null;
        int index = -1;
        Boolean errorOfSomeType = false;
        if (node.getId() != null) {
            node.getId().apply(this);
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                s = symbolTable.getSymbol(id);
            }
        }
        if (node.getArrayOption() != null) {
            if (node.getArrayOption() instanceof AArrayArrayOption) {
                if (isArray(s)) {
                    index = Integer.parseInt(
                        ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                    );
                    if ((index < 0) || (index >= ((Array) s).getSize())) {
                        mips.printError(
                            String.format(
                                "%d is not a valid index for array %s.",
                                index,
                                id
                            )
                        );
                        errorOfSomeType = true;
                    }
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s is not an array.",
                            id
                        )
                    );
                    errorOfSomeType = true;
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExpr() != null) {
            if(!errorOfSomeType && s.getType().equals("REAL")){
                isFloat = true;
            }
            node.getExpr().apply(this);
            if (!errorOfSomeType) {
                if (s.getType().equals("STRING")) {
                    mips.printError("Cannot store numerical types into STRING.");
                } else {
                    if ((s.getType().equals("BOOLEAN")
                            || s.getType().equals("INT"))
                            && isFloat) {
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to REAL.",
                                id,
                                s.getType()
                            )
                        );
                    } else {
                        if (isArray(s)) {
                            mips.lw("$t0", s.getOffset(), "$sp");
                            if (isFloat) {
                                mips.swc1("$f0", 4 * index, "$t0");
                            } else {
                                mips.sw("$s0", 4 * index, "$t0");
                            }
                            ((Array) s).initializeAt(index);
                            symbolTable.add(id, s);
                        } else {
                            if (isFloat) {
                                mips.swc1("$f0", s.getOffset(), "$sp");
                            } else {
                                mips.sw("$s0", s.getOffset(), "$sp");
                            }
                            ((Variable) s).initialize();
                            symbolTable.add(id, (Variable) s);
                        }
                    }
                }
            }
            isFloat = false;
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getPut() != null) {
            node.getPut().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getId() != null) {
            id = node.getId().getText();
            if (symbolTable.contains(id)) {
                s = symbolTable.getSymbol(id);
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id

                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (symbolTable.contains(id)) {
                if (isArray(s)) {
                    if (node.getArrayOption() instanceof AArrayArrayOption) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index >= 0 && index < (((Array) s).getSize())) {
                            if (((Array) s).isInitializedAt(index)) {
                                mips.lw("$t0", s.getOffset(), "$sp");
                            } else {
                                mips.printError(
                                    String.format(
                                        "Array %s has not been initialized at index %d.",
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
                                "No index specified for array %s.",
                                id
                            )
                        );
                    }
                } else {
                    if (node.getArrayOption() instanceof AEpsilonArrayOption) {
                        if (!((Variable) s).isInitialized()) {
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
                                "Variable %s is not an array.",
                                id
                            )
                        );
                    }
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            if (symbolTable.contains(id)) {
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption) {
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index < 0 || index >= ((Array) s).getSize()) {
                            mips.printError(
                                String.format(
                                    "Index %d is not valid for array %s.",
                                    index,
                                    id
                                )
                            );
                            index = -1;
                        }
                    } else {
                            mips.printError(
                                String.format(
                                    "Variable %s is not an array " +
                                    "and may not have an index.",
                                    id
                                )
                            );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Variable %s is an array " +
                                "and must have a valid index.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption) {
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index < 0 || index >= ((Array) s).getSize()) {
                            mips.printError(
                                String.format(
                                    "Index %d is not valid for array %s.",
                                    index,
                                    id
                                )
                            );
                            index = -1;
                        }
                    } else {
                            mips.printError(
                                String.format(
                                    "Variable %s is not an array " +
                                    "and may not have an index.",
                                    id
                                )
                            );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Variable %s is an array " +
                                "and must have a valid index.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                if(s.getType().equals("BOOLEAN")){
                    index = 0;
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s has type %s " +
                            "which cannot be converted to BOOLEAN.",
                            id,
                            s.getType()
                        )
                    );
                }
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption){
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if ((index < 0) || (index >= ((Array) s).getSize())) {
                            mips.printError(
                                String.format(
                                    "Index %d is invalid for array %s.",
                                    index,
                                    id
                                )
                            );
                        }
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s is not an array " +
                                "and may not have an index.",
                                id
                            )
                        );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Missing index for array %s.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getBoolean() != null) {
            node.getBoolean().apply(this);
            if (index > -1) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    if (isFloat) {
                        mips.cvt_w_s("$f0", "$f0");
                        mips.mfc1("$f0", "$s0");
                    }
                    mips.sw("$s0", 4 * index, "$t0");
                    ((Array) s).initializeAt(index);
                } else {
                    if (isFloat) {
                        mips.cvt_w_s("$f0", "$f0");
                        mips.mfc1("$f0", "$s0");
                    }
                    mips.sw("$s0", s.getOffset(), "$sp");
                    ((Variable) s).initialize();
                }
            }
            isFloat = false;
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
        String id = "";
        Symbol s = null;
        int index = -1;
        Boolean errorOfSomeType = false;
        if (node.getId() != null) {
            node.getId().apply(this);
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                s = symbolTable.getSymbol(id);
            }
        }
        if (node.getArrayOption() != null) {
            if (node.getArrayOption() instanceof AArrayArrayOption) {
                if (isArray(s)) {
                    index = Integer.parseInt(
                        ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                    );
                    if ((index < 0) || (index >= ((Array) s).getSize())) {
                        mips.printError(
                            String.format(
                                "%d is not a valid index for array %s.",
                                index,
                                id
                            )
                        );
                        errorOfSomeType = true;
                    }
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s is not an array.",
                            id
                        )
                    );
                    errorOfSomeType = true;
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getExpr() != null) {
            if(!errorOfSomeType && s.getType().equals("REAL")){
                isFloat = true;
            }
            node.getExpr().apply(this);
            if (!errorOfSomeType) {
                if (s.getType().equals("STRING")) {
                    mips.printError("Cannot store numerical types into STRING.");
                } else {
                    if ((s.getType().equals("BOOLEAN")
                            || s.getType().equals("INT"))
                            && isFloat) {
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to REAL.",
                                id,
                                s.getType()
                            )
                        );
                    } else {
                        if (isArray(s)) {
                            mips.lw("$t0", s.getOffset(), "$sp");
                            if (isFloat) {
                                mips.swc1("$f0", 4 * index, "$t0");
                            } else {
                                mips.sw("$s0", 4 * index, "$t0");
                            }
                            ((Array) s).initializeAt(index);
                            symbolTable.add(id, s);
                        } else {
                            if (isFloat) {
                                mips.swc1("$f0", s.getOffset(), "$sp");
                            } else {
                                mips.sw("$s0", s.getOffset(), "$sp");
                            }
                            ((Variable) s).initialize();
                            symbolTable.add(id, (Variable) s);
                        }
                    }
                }
            }
            isFloat = false;
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignStringStmt(AAssignStringStmt node) {
        String id = node.getId().getText();
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            if (!symbolTable.contains(id)) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                s = symbolTable.getSymbol(id);
                if (!s.getType().equals("STRING")) {
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
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (symbolTable.contains(id)) {
                if (node.getArrayOption() instanceof AEpsilonArrayOption) {
                    if (isArray(s)) {
                        mips.printError("No array index specified.");
                    } else {
                        index = 0;
                    }
                } else {
                    if (isArray(s)) {
                        int num = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if ((((Array) s).getSize() > num)) {
                            index = num;
                        } else {
                            mips.printError(
                                String.format(
                                    "Index %d for array %s is invalid.",
                                    num,
                                    id
                                )
                            );
                        }
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s is not an array.",
                                id
                            )
                        );
                    }
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getAnychars() != null) {
            if (index != -1) {
                mips.la("$t0", mips.addString(node.getAnychars().getText()));
                if (isArray(s)) {
                    mips.lw("$t1", s.getOffset(), "$sp");
                    mips.sw("$t0", 4 * index, "$t1");
                    ((Array) s).initializeAt(index);
                } else {
                    mips.sw("$t0", s.getOffset(), "$sp");
                    ((Variable) s).initialize();
                }
            }
            node.getAnychars().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAVarDeclStmt(AVarDeclStmt node) {
        ArrayList<String> id = new ArrayList<String>();
        Symbol s = null;
        String type = "";
        int size = -1;
        if (node.getId() != null) {
            id.add(node.getId().getText());
            node.getId().apply(this);
        }
        if (node.getMoreIds() != null) {
            Node childNode = null;
            if(node.getMoreIds() instanceof AMoreIdsMoreIds){
                childNode = node.getMoreIds();
                while(childNode instanceof AMoreIdsMoreIds){
                    id.add(((AMoreIdsMoreIds) childNode).getId().getText());
                    childNode = ((AMoreIdsMoreIds) childNode).getMoreIds();
                }
            }
            node.getMoreIds().apply(this);
            for(int i = 0; i < id.size(); i++){
                if(symbolTable.declaredAtCurrentScope(id.get(i))){
                    mips.printError(
                        String.format(
                            "Variable %s has already been declared in this scope.",
                            id.get(i)
                        )
                    );
                }
            }
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getType() != null) {
            if(node.getType() instanceof ATypesType){
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
            if(node.getArrayOption() instanceof AArrayArrayOption){
                size = Integer.parseInt(
                    ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                );
                if (size < 1) {
                    mips.printError(
                        String.format(
                            "%d is not a valid array size.",
                            size
                        )
                    );
                }
            } else {
                size = 0;
            }
            node.getArrayOption().apply(this);
        }
        if (node.getSemicolon() != null) {
            if (size > -1) {
                //if it is not an array
                if (size == 0) {
                    for(int i = 0; i < id.size(); i++){
                        symbolTable.add(id.get(i), new Variable(type, offset));
                        offset -= 4;
                    }
                } else {
                    for(int i = 0; i < id.size(); i++){
                        mips.la("$t0", mips.addWords(size));
                        mips.sw("$t0", offset, "$sp");
                        symbolTable.add(id.get(i), new Array(type, offset, size));
                        offset -= 4;
                    }
                }
            }
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAIfBlockStmt(AIfBlockStmt node) {
        String falselabel = mips.getLabel();
        boolean isConstant = false;
        boolean constant = false;
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
                if (symbolTable.contains(id)) {
                    Variable var = (Variable) symbolTable.getSymbol(id);
                    if (((Variable) var).getType().equals("BOOLEAN")) {
                        mips.incLabel();
                        mips.lw("$t0", var.getOffset(), "$sp");
                        mips.beq("$zero", "$t0", falselabel);
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to BOOLEAN.",
                                id,
                                var.getType()
                            )
                        );
                    }
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s has not been declared.",
                            id
                        )
                    );
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
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().getText();
                if (symbolTable.contains(id)) {
                    mips.incLabel();
                } else {
                    mips.decLabel();
                    mips.printError(
                        String.format(
                            "Variable %s has not been declared.",
                            id
                        )
                    );
                }
                Variable var = (Variable) symbolTable.getSymbol(id);
                if(!var.getType().toString().equals("BOOLEAN")){
                    mips.decLabel();
                    mips.printError(
                        String.format(
                            "Variable %s has type %s which" +
                            "cannot be converted to BOOLEAN.",
                            id,
                            var.getType()
                        )
                    );
                }
                mips.lw("$t0", var.getOffset(), "$sp");
                mips.beq("$zero", "$t0", falselabel);
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
        Variable var = null;
        if (node.getWhile() != null) {
            node.getWhile().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            if (node.getBoolid() instanceof AIdBoolid) {
                String id = ((AIdBoolid) node.getBoolid()).getId().getText();
                if (symbolTable.contains(id)) {
                    var = (Variable) symbolTable.getSymbol(id);
                    if (var.getType().equals("BOOLEAN")) {
                        mips.incLabel();
                        mips.lw("$t0", var.getOffset(), "$sp");
                        mips.beq("$zero", "$t0", falselabel);
                        mips.addLabel(truelabel);
                    } else {
                        mips.decLabel();
                        mips.printError(
                            String.format(
                                "Variable %s has type %s which " +
                                "cannot be converted to BOOLEAN.",
                                id,
                                var.getType()
                            )
                        );
                    }
                } else {
                    mips.decLabel();
                    mips.printError(
                        String.format(
                            "Variable %s has not been declared.",
                            id
                        )
                    );
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
                mips.lw("$t0", var.getOffset(), "$sp");
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            if (symbolTable.contains(id)) {
                s = symbolTable.getSymbol(id);
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (isArray(s)) {
                if(node.getArrayOption() instanceof AArrayArrayOption){
                    index = Integer.parseInt(
                        ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                    );
                    if ((index < 0) || (index >= ((Array) s).getSize())) {
                        mips.printError(
                            String.format(
                                "Index %d is invalid for array %s.",
                                index,
                                id
                            )
                        );
                        index = -1;
                    }
                } else {
                    mips.printError(
                        String.format(
                            "No index given for array %s.",
                            id
                        )
                    );
                }
            } else {
                if(node.getArrayOption() instanceof AEpsilonArrayOption){
                    index = 0;
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
            if (index != -1) {
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
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAPutStmt(APutStmt node) {
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getPut() != null) {
            node.getPut().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getId() != null) {
            id = node.getId().getText();
            if (symbolTable.contains(id)) {
                s = symbolTable.getSymbol(id);
            } else {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id

                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (symbolTable.contains(id)) {
                if (isArray(s)) {
                    if (node.getArrayOption() instanceof AArrayArrayOption) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index >= 0 && index < (((Array) s).getSize())) {
                            if (((Array) s).isInitializedAt(index)) {
                                mips.lw("$t0", s.getOffset(), "$sp");
                            } else {
                                mips.printError(
                                    String.format(
                                        "Array %s has not been initialized at index %d.",
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
                                "No index specified for array %s.",
                                id
                            )
                        );
                    }
                } else {
                    if (node.getArrayOption() instanceof AEpsilonArrayOption) {
                        if (!((Variable) s).isInitialized()) {
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
                                "Variable %s is not an array.",
                                id
                            )
                        );
                    }
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getSemicolon() != null) {
            if (symbolTable.contains(id)) {
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
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAIncrStmt(AIncrStmt node) {
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption) {
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index < 0 || index >= ((Array) s).getSize()) {
                            mips.printError(
                                String.format(
                                    "Index %d is not valid for array %s.",
                                    index,
                                    id
                                )
                            );
                            index = -1;
                        }
                    } else {
                            mips.printError(
                                String.format(
                                    "Variable %s is not an array " +
                                    "and may not have an index.",
                                    id
                                )
                            );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Variable %s is an array " +
                                "and must have a valid index.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption) {
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if (index < 0 || index >= ((Array) s).getSize()) {
                            mips.printError(
                                String.format(
                                    "Index %d is not valid for array %s.",
                                    index,
                                    id
                                )
                            );
                            index = -1;
                        }
                    } else {
                            mips.printError(
                                String.format(
                                    "Variable %s is not an array " +
                                    "and may not have an index.",
                                    id
                                )
                            );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Variable %s is an array " +
                                "and must have a valid index.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
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
                    mips.li("$s0", 0);
                } else {
                    mips.li("$s0", 1);
                }
            }
            else {
                mips.printError(
                    "Return type is not an expression or boolean."
                );
            }
            node.getExprOrBool().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignBooleanStmt(AAssignBooleanStmt node) {
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                if(s.getType().equals("BOOLEAN")){
                    index = 0;
                } else {
                    mips.printError(
                        String.format(
                            "Variable %s has type %s " +
                            "which cannot be converted to BOOLEAN.",
                            id,
                            s.getType()
                        )
                    );
                }
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (s != null) {
                if (node.getArrayOption() instanceof AArrayArrayOption){
                    if (isArray(s)) {
                        index = Integer.parseInt(
                            ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                        );
                        if ((index < 0) || (index >= ((Array) s).getSize())) {
                            mips.printError(
                                String.format(
                                    "Index %d is invalid for array %s.",
                                    index,
                                    id
                                )
                            );
                        }
                    } else {
                        mips.printError(
                            String.format(
                                "Variable %s is not an array " +
                                "and may not have an index.",
                                id
                            )
                        );
                    }
                } else {
                    if (isArray(s)) {
                        mips.printError(
                            String.format(
                                "Missing index for array %s.",
                                id
                            )
                        );
                    } else {
                        index = 0;
                    }
                }
            }
            node.getArrayOption().apply(this);
        }
        if (node.getEquals() != null) {
            node.getEquals().apply(this);
        }
        if (node.getBoolean() != null) {
            node.getBoolean().apply(this);
            if (index > -1) {
                if (isArray(s)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    if (isFloat) {
                        mips.cvt_w_s("$f0", "$f0");
                        mips.mfc1("$f0", "$s0");
                    }
                    mips.sw("$s0", 4 * index, "$t0");
                    ((Array) s).initializeAt(index);
                } else {
                    if (isFloat) {
                        mips.cvt_w_s("$f0", "$f0");
                        mips.mfc1("$f0", "$s0");
                    }
                    mips.sw("$s0", s.getOffset(), "$sp");
                    ((Variable) s).initialize();
                }
            }
            isFloat = false;
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
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
        isFloat = false;
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
        boolean addition = true;
        boolean isFloatAfterFirstExpr = false;
        if (node.getExpr() != null) {
            node.getExpr().apply(this);
            if (isFloat) {
                mips.swc1("$f0", offset, "$sp");
                isFloatAfterFirstExpr = true;
            } else {
                mips.sw("$s0", offset, "$sp");
                isFloatAfterFirstExpr = false;
            }
            offset -= 4;
        }
        if (node.getAddop() != null) {
            if (node.getAddop() instanceof AMinusAddop) {
                addition = false;
            }
            node.getAddop().apply(this);
        }
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
            offset += 4;
            if (isFloat) {
                if(isFloatAfterFirstExpr){
                    mips.l_s("$f1", offset, "$sp");
                } else {
                    mips.lw("$s0", offset, "$sp");
                    mips.mtc1("$s0", "$f1");
                    mips.cvt_s_w("$f1", "$f1");
                }
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
        boolean isFloatAfterFirstExpr = false;
        boolean divison = true;
        if (node.getTerm() != null) {
            node.getTerm().apply(this);
            if (isFloat) {
                mips.s_s("$f0", offset, "$sp");
                isFloatAfterFirstExpr = true;
            } else {
                mips.sw("$s0", offset, "$sp");
                isFloatAfterFirstExpr = false;
            }
            offset -= 4;
        }
        if (node.getMultop() != null) {
            if (node.getMultop().getText().equals("*")) {
                divison = false;
            }
            node.getMultop().apply(this);
        }
        if (node.getFactor() != null) {
            node.getFactor().apply(this);
            offset += 4;
            if (isFloat) {
                if(isFloatAfterFirstExpr){
                    mips.l_s("$f1", offset, "$sp");
                } else {
                    mips.lw("$t2", offset, "$sp");
                    mips.mtc1("$t2", "$f1");
                    mips.cvt_s_w("$f1", "$f1");
                }
                if (divison) {
                    mips.div_s("$f0", "$f1", "$f0");
                } else {
                    mips.mul_s("$f0", "$f1", "$f0");
                }
            } else {
                mips.lw("$t0", offset, "$sp");
                if (divison) {
                    mips.div("$s0", "$t0", "$s0");
                    mips.mflo("$s0");
                } else {
                    mips.mult("", "$t0", "$s0");
                    mips.mflo("$s0");
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
            if (isFloat) {
                mips.li("$t2", Float.floatToIntBits((float) -1.0));
                mips.mtc1("$t2", "$f1");
                mips.mult("$f0", "$f1", "$f0");
                mips.mflo("$f0");
            } else {
                mips.li("$t0", -1);
                mips.mult("$f0", "$t0", "$s0");
                mips.mflo("$s0");
            }
            node.getFactor().apply(this);
        }
    }

    @Override
    public void caseAIntFactor(AIntFactor node) {
        if (node.getInt() != null) {
            if (isFloat) {
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
            isFloat = true;
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
        String id = "";
        Symbol s = null;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            }
            node.getId().apply(this);
        }
        if (node.getLbracket() != null) {
            node.getLbracket().apply(this);
        }
        if (node.getInt() != null) {
            if (s != null) {
                int index = Integer.parseInt(node.getInt().getText());
                if (((Array) s).isInitializedAt(index)) {
                    mips.lw("$t0", s.getOffset(), "$sp");
                    if (isFloat || s.getType().equals("REAL")) {
                        isFloat = true;
                        mips.lwc1("$f0", 4 * index, "$t0");
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
            }
            node.getInt().apply(this);
        }
        if (node.getRbracket() != null) {
            node.getRbracket().apply(this);
        }
    }

    @Override
    public void caseAIdArrayOrId(AIdArrayOrId node) {
        String id = "";
        Symbol s = null;
        if (node.getId() != null) {
            id = node.getId().getText();
            s = symbolTable.getSymbol(id);
            if (s == null) {
                mips.printError(
                    String.format(
                        "Variable %s has not been declared.",
                        id
                    )
                );
            } else {
                if (((Variable) s).isInitialized()) {
                    if (isFloat || s.getType().equals("REAL")) {
                        isFloat = true;
                        if(s.getType().equals("REAL")){
                            mips.lwc1("$f0", s.getOffset(), "$sp");
                        } else {
                            mips.lw("$t2", s.getOffset(), "$sp");
                            mips.mtc1("$t2", "$f0");
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
            }
            node.getId().apply(this);
        }
    }

    @Override
    public void caseATrueBoolean(ATrueBoolean node) {
        if (node.getTrue() != null) {
            if (isFloat) {
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
            if (isFloat) {
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
        String cond = "";
        if (node.getFirst() != null) {
            isFloat = false;
            node.getFirst().apply(this);
            if (isFloat) {
                mips.swc1("$f0", offset, "$sp");
            } else {
                mips.sw("$s0", offset, "$sp");
            }
            offset -= 4;
        }
        if (node.getCond() != null) {
            cond = node.getCond().getText();
            node.getCond().apply(this);
        }
        if (node.getSec() != null) {
            node.getSec().apply(this);
            if (isFloat) {
                mips.lwc1("$f1", offset, "$sp");
                switch (cond) {
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
                switch (cond) {
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
            isFloat = false;
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

    public boolean isArray(Symbol symbol) {
        return symbol instanceof Array;
    }
}
