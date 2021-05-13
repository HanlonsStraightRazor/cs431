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
    private static SymbolTable symbolTable;
    private static StringBuilder data;
    private static StringBuilder arrays;
    private static StringBuilder text;
    private static final String DELIMITER = "    ";
    private static final String LABELPREFIX = "label";
    private static final String ARRAYPREFIX = "array";
    private static final String STRINGPREFIX = "string";
    private static final String BUFFERPREFIX = "buffer";
    private static final String FUNCTIONPREFIX = "function";
    private static final String TRUE = "TRUE";
    private static final String FALSE = "FALSE";
    private static int labelnum;
    private static int arraynum;
    private static int stringnum;
    private static int functionnum;
    private static int offset;
    private static Queue<String> error;
    private static boolean isFloat;
    private static String breakLabel;
    private static GlobalSet globalSet;
    /*
     * Constructor. Initializes non final class variables.
     */
    public PrintTree() {
        symbolTable = new SymbolTable();
        error = new LinkedList<String>();
        data   = new StringBuilder();
        arrays = new StringBuilder();
        text   = new StringBuilder();
        labelnum  = 0;
        arraynum  = 0;
        stringnum = 0;
        functionnum = 0;
        offset = 0;
        isFloat = false;
        globalSet = new GlobalSet();
    }

    @Override
    public void caseAProg(AProg node) {
        if (node.getBegin() != null) {
            data.append(DELIMITER
                + ".data\n"
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
                + ".text\n");
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
                System.out.print(data);
                System.out.print(arrays);
                System.out.print(text);
                node.getEnd().apply(this);
            }
        }
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
                error.add("Class " + id + " has already been declared.");
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
        String type = node.getType() instanceof AIdType
            ? ((AIdType) node.getType()).getId().getText()
            : ((ATypesType) node.getType()).getTypeDecl().getText();
        if (id.equals("MAIN")) {
            // TODO: check if main already exists
            if(!type.equals("VOID")){
                error.add("Invalid return type for main method. "
                        + "Must be VOID, got "
                        + type
                        + ".");
            }
            if (!(node.getVarlist() instanceof AEpsilonVarlist)) {
                error.add("Arguments not allowed in main method. Got "
                    + node.getVarlist()
                    + ".");
            }
            text.append("main:\n");
        } else {
            text.append(FUNCTIONPREFIX
                    + functionnum
                    + ":\n");
            functionnum++;
            this.offset = 0;
            text.append(DELIMITER
                    + "addi $sp, $sp, "
                    + offset
                + "\n");
        }
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
            text.append(DELIMITER
                + "li $v0, 10\n");
            text.append(DELIMITER
                + "syscall");
        } else {
            this.offset = offset;
            text.append(DELIMITER
                    + "addi $sp, $sp, "
                    + (-1 * offset)
                    + "\n");
            text.append(DELIMITER
                    + "jr $ra\n");
        }
    }

    //global variables
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
        String label = FUNCTIONPREFIX + functionnum;
        functionnum++;
        text.append(label
                + ":\n");
        int offset = this.offset;
        this.offset = 0;
        text.append(DELIMITER
                + "addi $sp, $sp, "
                + offset
                + "\n");
        if (node.getType() != null) {
            node.getType().apply(this);
        }
        if (node.getId() != null) {
            if (node.getId().getText().equals("MAIN")) {
                error.add("The main method may not be declared inside of a class.");
            }
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
        text.append(DELIMITER
                + "addi $sp, $sp, "
                + (-1 * offset)
                + "\n");
        text.append(DELIMITER
                + "jr $ra\n");
    }

    //var decl in a class
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
        String id = "";
        Symbol s = null;
        int index = -1;
        Boolean errorOfSomeType = false;
        if (node.getId() != null) {
            node.getId().apply(this);
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
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
                        error.add(index
                                + " is not a valid index for array "
                                + id
                                + ".");
                        errorOfSomeType = true;
                    }
                } else {
                    error.add("Variable "
                            + id
                            + " is not an array.");
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
                    error.add("Cannot store numerical types into STRING.");
                } else {
                    if ((s.getType().equals("BOOLEAN")
                            || s.getType().equals("INT"))
                            && isFloat) {
                        error.add("Variable "
                                + id
                                + " has type "
                                + s.getType()
                                + " which cannot be converted to REAL.");
                    } else {
                        if (isArray(s)) {
                            text.append(DELIMITER
                                    + "lw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n");
                            if (isFloat) {
                                text.append(DELIMITER
                                    + "swc1 $f0, "
                                    + (index * 4)
                                    + "($t0)\n");
                            } else {
                                text.append(DELIMITER
                                    + "sw $s0, "
                                    + (index * 4)
                                    + "($t0)\n");
                            }
                            ((Array) s).initializeAt(index);
                            symbolTable.add(id, s);
                        } else {
                            if (isFloat) {
                                text.append(DELIMITER
                                    + "swc1 $f0, "
                                    + Integer.toString(s.getOffset())
                                    + "($sp)\n");
                            } else {
                                text.append(DELIMITER
                                    + "sw $s0, "
                                    + Integer.toString(s.getOffset())
                                    + "($sp)\n");
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
                error.add("Variable "
                        + id
                        + " has not been declared.");
            } else {
                s = symbolTable.getSymbol(id);
                if (!s.getType().equals("STRING")) {
                    error.add("Variable "
                            + id
                            + " is type "
                            + s.getType()
                            + ". Must be type STRING.");
                }
            }
            node.getId().apply(this);
        }
        if (node.getArrayOption() != null) {
            if (symbolTable.contains(id)) {
                if (node.getArrayOption() instanceof AEpsilonArrayOption) {
                    if (isArray(s)) {
                        error.add("No array index specified");
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
                            error.add("Index "
                                    + num
                                    + " for array "
                                    + id
                                    + " is invalid.");
                        }
                    } else {
                        error.add("Variable "
                                + id
                                + " is not an array.");
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
                data.append(STRINGPREFIX
                        + stringnum
                        + ":\n");
                data.append(DELIMITER
                        + ".asciiz "
                        + node.getAnychars().getText()
                        + "\n");
                text.append(DELIMITER
                        + "la $t0, "
                        + STRINGPREFIX
                        + stringnum
                        + "\n");
                if (isArray(s)) {
                    text.append(DELIMITER
                            + "lw $t1, "
                            + s.getOffset()
                            + "($sp)\n"
                            + DELIMITER
                            + "sw $t0, "
                            + (index * 4)
                            + "($t1)\n");
                    ((Array) s).initializeAt(index);
                } else {
                    text.append(DELIMITER
                            + "sw $t0, "
                            + s.getOffset()
                            + "($sp)\n");
                    ((Variable) s).initialize();
                }
                stringnum++;
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
                    error.add(id.get(i) + " has already been declared in this scope.");
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
                error.add("Invalid type "
                        + ((AIdType) node.getType()).getId().getText()
                        + ".");
            }
            node.getType().apply(this);
        }
        if (node.getArrayOption() != null) {
            if(node.getArrayOption() instanceof AArrayArrayOption){
                size = Integer.parseInt(
                    ((AArrayArrayOption) node.getArrayOption()).getInt().getText()
                );
                if (size < 1) {
                    error.add(size + " is not a valid array size.");
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
                        arrays.append(ARRAYPREFIX
                            + arraynum
                            + ":\n");
                        arrays.append(DELIMITER
                                + ".word "
                                + size
                                + "\n");
                        text.append(DELIMITER
                                + "la $t0, "
                                + ARRAYPREFIX
                                + arraynum
                                + "\n");
                        text.append(DELIMITER
                                + "sw $t0, "
                                + offset
                                +"($sp)\n");
                        arraynum++;
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
            node.getBoolid().apply(this);
            if(node.getBoolid() instanceof AIdBoolid){
                String id = ((AIdBoolid) node.getBoolid()).getId().getText().trim();
                if (!symbolTable.contains(id)) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum++;
                }
                Variable var = (Variable) symbolTable.getSymbol(id);
                if(!((Variable) var).getType().toString().equals("BOOLEAN")){
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, "
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
                    labelnum++;
                    text.append(DELIMITER
                        + "beq $zero, $s0, "
                        + falselabel
                        + "\n");
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
            if (!isConstant) {
                text.append("\n" + falselabel
                    + ":\n");
            }
            symbolTable.decScope();
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
                if (!symbolTable.contains(id)) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum += 2;
                }
                Variable var = (Variable) symbolTable.getSymbol(id);
                if(!var.getType().toString().equals("BOOLEAN")){
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, "
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
                    labelnum += 2;
                    text.append(DELIMITER
                        + "beq $zero, $s0, "
                        + falselabel
                        + "\n");
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
                text.append(DELIMITER + "j "
                   + endlabel + "\n");
            }
            symbolTable.decScope();
            node.getIfrcurly().apply(this);
        }
        if (node.getElse() != null) {
            node.getElse().apply(this);
        }
        if (node.getElselcurly() != null) {
            symbolTable.incScope();
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
            symbolTable.decScope();
            node.getElsercurly().apply(this);
        }
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node) {
        String truelabel = LABELPREFIX
            + labelnum;
        String falselabel = LABELPREFIX
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
                if (!symbolTable.contains(id)) {
                    error.add("Variable "
                        + id
                        + " has not been declared.");
                } else {
                    labelnum += 2;
                }
                var = (Variable) symbolTable.getSymbol(id);
                if(!var.getType().toString().equals("BOOLEAN")) {
                    error.add("Variable "
                        + id
                        + " has type "
                        + var.getType()
                        + " which cannot be converted to BOOLEAN.");
                }
                text.append(DELIMITER
                    + "lw $t0, "
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "beq $zero, $t0, "
                    + falselabel
                    + "\n");
                text.append(truelabel
                    + ":\n");
                }
            else {
                ABoolBoolid ABoolBoolidNode = (ABoolBoolid)node.getBoolid();
                if((ABoolBoolidNode.getBoolean()) instanceof ATrueBoolean) {
                    isConstant = true;
                    constant = true;
                    text.append(truelabel
                    + ":\n");
                    }
                else if((ABoolBoolidNode.getBoolean()) instanceof AFalseBoolean) {
                    isConstant = true;
                }
                else if((ABoolBoolidNode.getBoolean()) instanceof AConditionalBoolean) {
                    labelnum += 2;
                    text.append(DELIMITER
                        + "beq $zero, $s0, "
                        + falselabel
                        + "\n");
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
                text.append(DELIMITER
                    + "j "
                    + truelabel
                    + "\n");
            }
            else if(!isConstant) {
                node.getStmtseq().apply(this);
                text.append(DELIMITER
                    + "lw $t0, "
                    + var.getOffset()
                    + "($sp)\n");
                text.append(DELIMITER
                    + "bne $zero, $t0, "
                    + truelabel
                    + "\n");
                text.append(DELIMITER
                    + "j "
                    + falselabel
                    + "\n");
            }
        }
        if (node.getRcurly() != null) {
            if (!isConstant) {
                text.append(falselabel
                    + ":\n");
            }
            symbolTable.decScope();
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
            } else {
                s = symbolTable.getSymbol(id);
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
                        error.add("Index "
                                + index
                                + " is invalid for array "
                                + id
                                + ".");
                        index = -1;
                    }
                } else {
                    error.add("No index given for array "
                            + id
                            + ".");
                }
            } else {
                if(node.getArrayOption() instanceof AEpsilonArrayOption){
                    index = 0;
                } else {
                    error.add("Variable "
                            + id
                            + " is not an array and may not be given an index.");
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
                            text.append(DELIMITER
                                    + "lw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n"
                                    + DELIMITER
                                    + "li $v0, 6\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "swc1 $f0, "
                                    + (index * 4)
                                    + "($t0)\n");
                            break;
                        case "STRING":
                            arrays.append(BUFFERPREFIX
                                    + arraynum
                                    + ":\n"
                                    + DELIMITER
                                    + ".word 100\n");
                            text.append(DELIMITER
                                    + "lw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n"
                                    + DELIMITER
                                    + "li $v0, 8\n"
                                    + DELIMITER
                                    + "la $a0, "
                                    + BUFFERPREFIX
                                    + arraynum
                                    + "\n"
                                    + DELIMITER
                                    + "li $a1, 399\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "la $t1, "
                                    + BUFFERPREFIX
                                    + arraynum
                                    + "\n"
                                    + DELIMITER
                                    + "sw $t1, "
                                    + (index * 4)
                                    + "($t0)\n");
                            arraynum++;
                            break;
                        case "BOOLEAN":
                            String falselabel = LABELPREFIX + labelnum++;
                            String endlabel   = LABELPREFIX + labelnum++;
                            text.append(DELIMITER
                                    + "lw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n"
                                    + DELIMITER
                                    + "li $v0, 5\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "beq $zero, $v0, "
                                    + falselabel
                                    + "\n"
                                    + DELIMITER
                                    + "li $t1, 1\n"
                                    + DELIMITER
                                    + "j "
                                    + endlabel
                                    + "\n\n"
                                    + falselabel
                                    + ":\n"
                                    + DELIMITER
                                    + "li $t1, 0\n\n"
                                    + endlabel
                                    + ":\n"
                                    + DELIMITER
                                    + "sw $t1, "
                                    + (index * 4)
                                    + "($t0)\n");
                            break;
                        default:
                            text.append(DELIMITER
                                    + "lw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n"
                                    + DELIMITER
                                    + "li $v0, 5\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "sw $v0, "
                                    + (index * 4)
                                    + "($t0)\n");
                    }
                    ((Array) s).initializeAt(index);
                } else {
                    switch (s.getType()) {
                        case "REAL":
                            text.append(DELIMITER
                                    + "li $v0, 6\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "swc1 $f0, "
                                    + s.getOffset()
                                    + "($sp)\n");
                            break;
                        case "STRING":
                            arrays.append(BUFFERPREFIX
                                    + arraynum
                                    + ":\n"
                                    + DELIMITER
                                    + ".word 100\n");
                            text.append(DELIMITER
                                    + "li $v0, 8\n"
                                    + DELIMITER
                                    + "la $a0, "
                                    + BUFFERPREFIX
                                    + arraynum
                                    + "\n"
                                    + DELIMITER
                                    + "li $a1, 399\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "la $t0, "
                                    + BUFFERPREFIX
                                    + arraynum
                                    + "\n"
                                    + DELIMITER
                                    + "sw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n");
                            arraynum++;
                            break;
                        case "BOOLEAN":
                            String falselabel = LABELPREFIX + labelnum++;
                            String endlabel   = LABELPREFIX + labelnum++;
                            text.append(DELIMITER
                                    + "li $v0, 5\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "beq $zero, $v0, "
                                    + falselabel
                                    + "\n"
                                    + DELIMITER
                                    + "li $t0, 1\n"
                                    + DELIMITER
                                    + "j "
                                    + endlabel
                                    + "\n\n"
                                    + falselabel
                                    + ":\n"
                                    + DELIMITER
                                    + "li $t0, 0\n\n"
                                    + endlabel
                                    + ":\n"
                                    + DELIMITER
                                    + "sw $t0, "
                                    + s.getOffset()
                                    + "($sp)\n");
                            break;
                        default:
                            text.append(DELIMITER
                                    + "li $v0, 5\n"
                                    + DELIMITER
                                    + "syscall\n"
                                    + DELIMITER
                                    + "sw $v0, "
                                    + s.getOffset()
                                    + "($sp)\n");
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
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
            } else {
                s = symbolTable.getSymbol(id);
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
                                text.append(DELIMITER
                                        + "lw $t0, "
                                        + s.getOffset()
                                        + "($sp)\n");
                            } else {
                                error.add("Array "
                                    + id
                                    + " has not been initialized at index "
                                    + index
                                    + ".");
                            }
                        } else {
                            error.add("Index"
                                    + index
                                    + " is not valid for array "
                                    + id
                                    + ".");
                        }
                    } else {
                        error.add("No index specified for array "
                                + id
                                + ".");
                    }
                } else {
                    if (node.getArrayOption() instanceof AEpsilonArrayOption) {
                        if (!((Variable) s).isInitialized()) {
                            error.add("Variable "
                                + id
                                + " has not been initialized.");
                        }
                    } else {
                        error.add("Variable "
                                + id
                                + " is not an array.");
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
                text.append(DELIMITER
                        + "li $v0, ");
                switch (s.getType()) {
                    case "REAL":
                        text.append("2\n");
                        if (isArray(s)) {
                            text.append(DELIMITER
                                + "lwc1 $f12, "
                                + (index * 4)
                                + "($t0)\n");
                        } else {
                            text.append(DELIMITER
                                + "lwc1 $f12, "
                                + s.getOffset()
                                + "($sp)\n");
                        }
                        break;
                    case "STRING":
                        text.append("4\n");
                        if (isArray(s)) {
                            text.append(DELIMITER
                                + "lw $a0, "
                                + (index * 4)
                                + "($t0)\n");
                        } else {
                            text.append(DELIMITER
                                + "lw $a0, "
                                + s.getOffset()
                                + "($sp)\n");
                        }
                        break;
                    case "BOOLEAN":
                        text.append("4\n");
                        if (isArray(s)) {
                            text.append(DELIMITER
                                + "lw $t0, "
                                + (index * 4)
                                + "($t0)\n");
                        } else {
                            text.append(DELIMITER
                                + "lw $t0, "
                                + s.getOffset()
                                + "($sp)\n");
                        }
                        String falselabel = LABELPREFIX + labelnum++;
                        String endLabel   = LABELPREFIX + labelnum++;
                        text.append(DELIMITER
                            + "beq $zero, $t0, "
                            + falselabel
                            + "\n"
                            + DELIMITER
                            + "la $a0, TRUE\n"
                            + DELIMITER
                            + "j "
                            + endLabel
                            + "\n\n"
                            + falselabel
                            + ":\n"
                            + DELIMITER
                            + "la $a0, FALSE\n\n"
                            + endLabel
                            + ":\n");
                        break;
                    default:
                        text.append("1\n");
                        if (isArray(s)) {
                            text.append(DELIMITER
                                + "lw $a0, "
                                + (index * 4)
                                + "($t0)\n");
                        } else {
                            text.append(DELIMITER
                                + "lw $a0, "
                                + s.getOffset()
                                + "($sp)\n");
                        }
                }
                text.append(DELIMITER
                    + "syscall\n"
                    + DELIMITER
                    + "li $v0, 11\n"
                    + DELIMITER
                    + "li $a0, 0xA\n"
                    + DELIMITER
                    + "syscall\n"); // Print newline
            }
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
            if (!symbolTable.contains(id)) {
                error.add("Variable " + id + " has not been declared.");
            }
            Variable var = (Variable) symbolTable.getSymbol(id);
            if(!(var.getType().equals("INT")
                || var.getType().equals("REAL")
                || var.getType().equals("VOID"))){
                error.add("Variable " + id + " has type " + var.getType() + " which cannot be incremented.");
            } else {
                if (var.getType().equals("INT")
                    || var.getType().equals("VOID")){
                    text.append(DELIMITER + "lw $t0, " + var.getOffset() + "($sp)\n");
                    text.append(DELIMITER + "li $t1, " + "1" + "\n");
                    text.append(DELIMITER + "add $t0, " + "$t0, " + "$t1\n");
                    text.append(DELIMITER + "sw $t0, " + var.getOffset() + "($sp)\n");
                } else {
                    text.append(DELIMITER + "lwc1 $f0, " + var.getOffset() + "($sp)\n");
                    text.append(DELIMITER + "li $t2, " + "1" + "\n");
                    text.append(DELIMITER + "mtc1 $t2, " + "$f1" + "\n");
                    text.append(DELIMITER + "cvt.s.w $f1, " + "$f1" + "\n");
                    text.append(DELIMITER + "add.s $f0, " + "$f0, " + "$f1\n");
                    text.append(DELIMITER + "swc1 $f0, " + var.getOffset() + "($sp)\n");
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
            if (!symbolTable.contains(id)) {
                error.add("Variable " + id + " has not been declared.");
            }
            Variable var = (Variable) symbolTable.getSymbol(id);
            if (!(var.getType().equals("INT")
                || var.getType().equals("REAL")
                || var.getType().equals("VOID"))) {
                error.add("Variable " + id + " has type " + var.getType() + " which cannot be decremented.");
            }
            if (var.getType().equals("INT")
                || var.getType().equals("VOID")) {
                text.append(DELIMITER + "lw $t0, " + var.getOffset() + "($sp)\n");
                text.append(DELIMITER + "li $t1, " + "1" + "\n");
                text.append(DELIMITER + "sub $t0, " + "$t0, " + "$t1\n");
                text.append(DELIMITER + "sw $t0, " + var.getOffset() + "($sp)\n");
            } else {
                text.append(DELIMITER + "lwc1 $f0, " + var.getOffset() + "($sp)\n");
                text.append(DELIMITER + "li $t2, " + "1" + "\n");
                text.append(DELIMITER + "mtc1 $t2, " + "$f1" + "\n");
                text.append(DELIMITER + "cvt.s.w $f1, " + "$f1" + "\n");
                text.append(DELIMITER + "sub.s $f0, " + "$f0, " + "$f1\n");
                text.append(DELIMITER + "swc1 $f0, " + var.getOffset() + "($sp)\n");
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
        String id = "";
        Symbol s = null;
        int index = -1;
        if (node.getId() != null) {
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
            } else {
                s = symbolTable.getSymbol(id);
                if(!s.getType().equals("BOOLEAN")){
                    error.add("Variable "
                            + id
                            + " has type "
                            + s.getType()
                            + " which cannot be converted to BOOLEAN.");
                } else {
                    index = 0;
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
                            error.add("Index "
                                    + index
                                    + " is invalid for array "
                                    + id
                                    + ".");
                        }
                    } else {
                        error.add("Variable "
                                + id
                                + " is not an array and may not have an index.");
                    }
                } else {
                    if (isArray(s)) {
                        error.add("Missing index for array "
                                + id
                                + ".");
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
            if (index != -1) {
                if (isArray(s)) {
                    text.append(DELIMITER
                            + "lw $t0, "
                            + s.getOffset()
                            + "($sp)\n");
                    if (isFloat) {
                        text.append(DELIMITER
                                + "cvt.w.s $f0, $f0\n"
                                + DELIMITER
                                + "mfc1 $f0, $s0\n");
                    }
                    text.append(DELIMITER
                            + "sw $s0, "
                            + (index * 4)
                            + "($t0)\n");
                    ((Array) s).initializeAt(index);
                } else {
                    if (isFloat) {
                        text.append(DELIMITER
                                + "cvt.w.s $f0, $f0\n"
                                + DELIMITER
                                + "mfc1 $f0, $s0\n");
                    }
                    text.append(DELIMITER
                            + "sw $s0, "
                            + s.getOffset()
                            + "($sp)\n");
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
        breakLabel = LABELPREFIX
            + labelnum;
        String caseOneLabel = LABELPREFIX
            + (labelnum + 1);
        String afterCaseOneLabel = LABELPREFIX
            + (labelnum + 2);
        labelnum += 3;

        if (node.getSwitch() != null) {
            node.getSwitch().apply(this);
        }
        if (node.getFirst() != null) {
            node.getFirst().apply(this);
        }
        if (node.getExprOrBool() != null) {
            node.getExprOrBool().apply(this);
            text.append(DELIMITER
                    + "move $s1, $s0\n");
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
            text.append(DELIMITER
                    + "li $t0, "
                    + node.getInt() + "\n");
            text.append(DELIMITER
                    + "beq $s1, $t0, "
                    + caseOneLabel
                    + "\n");
            text.append(DELIMITER
                + "j "
                + afterCaseOneLabel
                + "\n");
            node.getInt().apply(this);
        }
        if (node.getFourth() != null) {
            node.getFourth().apply(this);
        }
        if (node.getFifth() != null) {
            node.getFifth().apply(this);
        }
        if (node.getStmts() != null) {
            text.append("\n" + caseOneLabel
                    + ":\n");
            node.getStmts().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            text.append(DELIMITER
                + "j "
                + afterCaseOneLabel
                + "\n");
            text.append("\n" + afterCaseOneLabel
                + ":\n");
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
            text.append(DELIMITER
                + "j "
                + breakLabel
                + "\n");
            text.append("\n" + breakLabel
                    + ":\n");
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAAnotherCaseCaseHelper(AAnotherCaseCaseHelper node) {
        String caseNLabel = LABELPREFIX
            + labelnum;
        String afterCaseNLabel = LABELPREFIX
            + (labelnum + 1);
        labelnum += 2;
        if (node.getCase() != null) {
            node.getCase().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getInt() != null) {
            text.append(DELIMITER
                    + "li $t0, "
                    + node.getInt() + "\n");
            text.append(DELIMITER
                    + "beq $s1, $t0, "
                    + caseNLabel
                    + "\n");
            text.append(DELIMITER
                + "j "
                + afterCaseNLabel
                + "\n");
            node.getInt().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getColon() != null) {
            node.getColon().apply(this);
        }
        if (node.getStmtseq() != null) {
           text.append("\n" + caseNLabel
                    + ":\n");
            node.getStmtseq().apply(this);
        }
        if (node.getBreakHelper() != null) {
            node.getBreakHelper().apply(this);
        }
        if (node.getCaseHelper() != null) {
            text.append(DELIMITER
                + "j "
                + afterCaseNLabel
                + "\n");
            text.append("\n" + afterCaseNLabel
                + ":\n");
            node.getCaseHelper().apply(this);
        }
    }

    @Override
    public void caseABreakBreakHelper(ABreakBreakHelper node) {
        if (node.getBreak() != null) {
            text.append(DELIMITER
                + "j "
                + breakLabel
                + "\n");
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
            if(node.parent() instanceof AAssignExprStmt){
                //FIXME
            } else if(node.parent() instanceof AIncrStmt){
                AIncrStmt AIncrStmtNode = (AIncrStmt)node.parent();
                String idVal = AIncrStmtNode.getId().toString().trim();
                int index = Integer.parseInt(node.getInt().toString().trim());
                if (!symbolTable.contains(idVal)) {
                    error.add("Array " + idVal + " has not been declared.");
                }
                Array var = (Array) symbolTable.getSymbol(idVal);
                if(!(var.getType().equals("INT")
                    || var.getType().equals("REAL"))){
                    error.add("Array " + idVal + " has type " + var.getType() + " which cannot be incremented.");
                } else {
                    if(var.isInitializedAt(index)){
                        if (var.getType().equals("INT")){
                            text.append(DELIMITER + "la $t2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $t0, " + (index * 4) + "($t2)\n");
                            text.append(DELIMITER + "li $t1, " + "1" + "\n");
                            text.append(DELIMITER + "add $t0, " + "$t1, " + "$t0\n");
                            text.append(DELIMITER + "sw $t0, " + (index * 4) + "($t2)\n");
                        } else {
                            text.append(DELIMITER + "lw $t1, " + var.getOffset() + "($sp)\n");
                            text.append(DELIMITER + "lw $f0, " + (index * 4) + "($t1)\n");
                            text.append(DELIMITER + "li $t2, " + "1" + "\n");
                            text.append(DELIMITER + "mtc1 $t2, " + "$f1" + "\n");
                            text.append(DELIMITER + "cvt.s.w $f1, " + "$f1" + "\n");
                            text.append(DELIMITER + "add $f0, " + "$f1, " + "$f0\n");
                            text.append(DELIMITER + "swc1 $f0, " + (index * 4) + "($f2)\n");
                        }
                    } else {
                        error.add("Array " + idVal + " at index: " + index + " has not been initialized yet.");
                    }
                }
            } else if(node.parent() instanceof ADecrStmt){
                AIncrStmt AIncrStmtNode = (AIncrStmt)node.parent();
                String idVal = AIncrStmtNode.getId().toString().trim();
                int index = Integer.parseInt(node.getInt().toString().trim());
                if (!symbolTable.contains(idVal)) {
                    error.add("Array " + idVal + " has not been declared.");
                }
                Array var = (Array) symbolTable.getSymbol(idVal);
                if(!(var.getType().equals("INT")
                    || var.getType().equals("REAL"))){
                    error.add("Array " + idVal + " has type " + var.getType() + " which cannot be decremented.");
                } else {
                    if(var.isInitializedAt(index)){
                        if (var.getType().equals("INT")){
                            text.append(DELIMITER + "la $t2, " + idVal + "\n");
                            text.append(DELIMITER + "lw $t0, " + (index * 4) + "($t2)\n");
                            text.append(DELIMITER + "li $t1, " + "1" + "\n");
                            text.append(DELIMITER + "sub $t0, " + "$t0, " + "$t1\n");
                            text.append(DELIMITER + "sw $t0, " + (index * 4) + "($t2)\n");
                        } else {
                            text.append(DELIMITER + "la $t1, " + var.getOffset() + "($sp)\n");
                            text.append(DELIMITER + "lw $f0, " + (index * 4) + "($t1)\n");
                            text.append(DELIMITER + "li $t2, " + "1" + "\n");
                            text.append(DELIMITER + "mtc1 $t2, " + "$f1" + "\n");
                            text.append(DELIMITER + "cvt.s.w $f1, " + "$f1" + "\n");
                            text.append(DELIMITER + "sub $f0, " + "$f0, " + "$f1\n");
                            text.append(DELIMITER + "swc1 $f0, " + (index * 4) + "($f2)\n");
                        }
                    } else {
                        error.add("Array " + idVal + " at index: " + index + " has not been initialized yet.");
                    }
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
                text.append(DELIMITER
                        + "swc1 $f0, "
                        + offset
                        + "($sp)\n");
                        isFloatAfterFirstExpr = true;
            } else {
                text.append(DELIMITER
                        + "sw $s0, "
                        + offset
                        + "($sp)\n");
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
                if(!isFloatAfterFirstExpr){
                    text.append(DELIMITER
                        + "lw $s0, "
                        + offset
                        + "($sp)\n");
                    text.append(DELIMITER
                        + "mtc1 $s0, $f1\n");
                    text.append(DELIMITER
                        + "cvt.s.w $f1, $f1\n");
                } else {
                    text.append(DELIMITER
                        + "l.s $f1, "
                        + offset
                        + "($sp)\n");
                }
                if (addition) {
                    text.append(DELIMITER
                            + "add.s $f0, $f1, $f0\n");
                } else {
                    text.append(DELIMITER
                            + "sub.s $f0, $f1, $f0\n");
                }
            } else {
                text.append(DELIMITER
                        + "lw $t0, "
                        + offset
                        + "($sp)\n");
                if (addition) {
                    text.append(DELIMITER
                            + "add $s0, $t0, $s0\n");
                } else {
                    text.append(DELIMITER
                            + "sub $s0, $t0, $s0\n");
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
                text.append(DELIMITER
                        + "s.s $f0, "
                        + offset
                        + "($sp)\n");
                        isFloatAfterFirstExpr = true;
            } else {
                text.append(DELIMITER
                        + "sw $s0, "
                        + offset
                        + "($sp)\n");
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
                if(!isFloatAfterFirstExpr){
                    text.append(DELIMITER
                        + "lw $t2, "
                        + offset
                        + "($sp)\n");
                    text.append(DELIMITER
                        + "mtc1 $t2, $f1\n");
                    text.append(DELIMITER
                        + "cvt.s.w $f1, $f1\n");
                } else {
                    text.append(DELIMITER
                    + "l.s $f1, "
                    + offset
                    + "($sp)\n");
                }
                if (divison) {
                    text.append(DELIMITER
                            + "div.s $f0, $f1, $f0\n");
                } else {
                    text.append(DELIMITER
                            + "mul.s $f0, $f1, $f0\n");
                }
            } else {
                text.append(DELIMITER
                        + "lw $t0, "
                        + offset
                        + "($sp)\n");
                if (divison) {
                    text.append(DELIMITER
                            + "div $t0, $s0\n");
                    text.append(DELIMITER
                            + "mflo $s0\n");
                } else {
                    text.append(DELIMITER
                            + "mult $t0, $s0\n");
                    text.append(DELIMITER
                            + "mflo $s0\n");
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
                text.append(DELIMITER + "li $t2, " + "-1" + "\n");
                text.append(DELIMITER + "mtc1 $t2, " + "$f1" + "\n");
                text.append(DELIMITER + "cvt.s.w $f1, " + "$f1" + "\n");
                text.append(DELIMITER + "mul $f1, $f0\n");
                text.append(DELIMITER + "mflo $f0\n");
            } else {
                text.append(DELIMITER + "li $t0, -1\n");
                text.append(DELIMITER + "mul $t0, $s0\n");
                text.append(DELIMITER + "mflo $s0\n");
            }
            node.getFactor().apply(this);
        }
    }

    @Override
    public void caseAIntFactor(AIntFactor node) {
        if (node.getInt() != null) {
            if (isFloat) {
                text.append(DELIMITER
                        + "li $t0, "
                        + Integer.parseInt(node.getInt().getText())
                        + "\n");
                text.append(DELIMITER
                        + "mtc1 $t0, $f0\n");
                text.append(DELIMITER
                        + "cvt.s.w $f0, $f0\n");
            } else {
                text.append(DELIMITER
                        + "li $s0, "
                        + Integer.parseInt(node.getInt().getText())
                        + "\n");
            }
            node.getInt().apply(this);
        }
    }

    @Override
    public void caseARealFactor(ARealFactor node) {
        if (node.getReal() != null) {
            isFloat = true;
            text.append(DELIMITER
                    + "li $t1, "
                    + Float.floatToIntBits(Float.parseFloat(node.getReal().getText()))
                    + "\n");
            text.append(DELIMITER
                    + "mtc1 $t1, $f0"
                    + "\n");
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
        if (node.getId() != null) {
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
            }
            node.getId().apply(this);
        }
        if (node.getLbracket() != null) {
            node.getLbracket().apply(this);
        }
        if (node.getInt() != null) {
            if (symbolTable.contains(id)) {
                Array array = (Array) symbolTable.getSymbol(id);
                int index = Integer.parseInt(node.getInt().getText());
                if (array.isInitializedAt(index)) {
                    text.append(DELIMITER
                            + "la $t0, "
                            + id
                            + "\n");
                    if (isFloat || array.getType().equals("REAL")) {
                        isFloat = true;
                        text.append(DELIMITER
                                + "lwc1 $f0, "
                                + (index * 4)
                                + "($t0)\n");
                    } else {
                        text.append(DELIMITER
                                + "lw $s0, "
                                + (index * 4)
                                + "($t0)\n");
                    }
                } else {
                    error.add("Array "
                            + id
                            + " has not been initialized at index "
                            + index
                            + ".");
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
        if (node.getId() != null) {
            id = node.getId().getText();
            if (!symbolTable.contains(id)) {
                error.add("Variable "
                        + id
                        + " has not been declared.");
            } else {
                Variable var = (Variable) symbolTable.getSymbol(id);
                if (var.isInitialized()) {
                    if (isFloat || var.getType().equals("REAL")) {
                        isFloat = true;
                        if(var.getType().equals("REAL")){
                            text.append(DELIMITER
                                + "lwc1 $f0, "
                                + var.getOffset()
                                + "($sp)\n");
                        } else {
                            text.append(DELIMITER
                                + "lw $t2, "
                                + var.getOffset()
                                + "($sp)\n");
                            text.append(DELIMITER
                                + "mtc1 $t2, $f0\n");
                            text.append(DELIMITER
                                + "cvt.s.w $f0, $f0\n");
                        }
                    } else {
                        text.append(DELIMITER
                                + "lw $s0, "
                                + var.getOffset()
                                + "($sp)\n");
                    }
                } else {
                    error.add("Variable "
                            + id
                            + " has not been initialized.");
                }
            }
            node.getId().apply(this);
        }
    }

    @Override
    public void caseATrueBoolean(ATrueBoolean node) {
        if (node.getTrue() != null) {
            if (isFloat) {
                text.append(DELIMITER + "li $t0, 1\n");
                text.append(DELIMITER + "mtc1 $f0, $t0\n");
                text.append(DELIMITER + "cvt.s.w $f0, $f0\n");
            } else {
                text.append(DELIMITER + "li $s0, 1\n");
            }
            node.getTrue().apply(this);
        }
    }

    @Override
    public void caseAFalseBoolean(AFalseBoolean node) {
        if (node.getFalse() != null) {
            if (isFloat) {
                text.append(DELIMITER + "li $t0, 0\n");
                text.append(DELIMITER + "mtc1 $f0, $t0\n");
                text.append(DELIMITER + "cvt.s.w $f0, $f0\n");
            } else {
                text.append(DELIMITER + "li $s0, 0\n");
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
                text.append(DELIMITER
                        + "swc1 $f0, "
                        + offset
                        + "($sp)\n");
            } else {
                text.append(DELIMITER
                        + "sw $s0, "
                        + offset
                        + "($sp)\n");
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
                text.append(DELIMITER
                        + "lwc1 $f1, "
                        + offset
                        + "($sp)\n"
                        + DELIMITER);
                switch (cond) {
                    case "==":
                        text.append("c.eq.s $f1, $f0\n");
                        break;
                    case "!=":
                        text.append("c.ne.s $f1, $f0\n");
                        break;
                    case ">":
                        text.append("c.lt.s $f0, $f1\n");
                        break;
                    case "<":
                        text.append("c.lt.s $f1, $f0\n");
                        break;
                    case ">=":
                        text.append("c.le.s $f0, $f1\n");
                        break;
                    case "<=":
                        text.append("c.le.s $f1, $f0\n");
                        break;
                    default:
                        error.add("Invalid condition");
                }
                text.append(DELIMITER + "li $s0, 1\n");
                text.append(DELIMITER + "movf $s0, $zero\n");
            } else {
                text.append(DELIMITER
                        + "lw $t0, "
                        + offset
                        + "($sp)\n"
                        + DELIMITER);
                switch (cond) {
                    case "==":
                        text.append("seq $s0, $t0, $s0\n");
                        break;
                    case "!=":
                        text.append("sne $s0, $t0, $s0\n");
                        break;
                    case ">":
                        text.append("sgt $s0, $t0, $s0\n");
                        break;
                    case "<":
                        text.append("slt $s0, $t0, $s0\n");
                        break;
                    case ">=":
                        text.append("sge $s0, $t0, $s0\n");
                        break;
                    case "<=":
                        text.append("sle $s0, $t0, $s0\n");
                        break;
                    default:
                        error.add("Invalid condition");
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
