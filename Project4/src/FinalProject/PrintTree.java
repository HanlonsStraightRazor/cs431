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
    public static ArrayList<HashMap<String, Variable>> symbolTables = new ArrayList<HashMap<String, Variable>>();
    StringBuilder textSB = new StringBuilder();
    StringBuilder dataSB = new StringBuilder();
    StringBuilder errorSB = new StringBuilder();

    public PrintTree() {
        System.out.println("Start of the Printing Action");
    }

    @Override
    public void caseStart(Start node) {
        node.getPProg().apply(this);
        node.getEOF().apply(this);
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
    public void caseAEpsilonClassmethodstmts(AEpsilonClassmethodstmts node) {
    }

    @Override
    public void caseAClassDefClassmethodstmt(AClassDefClassmethodstmt node) {
        if (node.getClassLit() != null) {
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
    public void caseAEpsilonMethodstmtseqs(AEpsilonMethodstmtseqs node) {
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
    public void caseAEpsilonStmtseq(AEpsilonStmtseq node) {
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
            node.getExpr().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignStringStmt(AAssignStringStmt node) {
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
    public void caseAVarDeclStmt(AVarDeclStmt node) {
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
        if (node.getArrayOption() != null) {
            node.getArrayOption().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAIfBlockStmt(AIfBlockStmt node) {
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
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
        }
        if (node.getStmtseq() != null) {
            node.getStmtseq().apply(this);
        }
        if (node.getRcurly() != null) {
            node.getRcurly().apply(this);
        }
    }

    @Override
    public void caseAIfElseBlockStmt(AIfElseBlockStmt node) {
        if (node.getIf() != null) {
            node.getIf().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            node.getBoolid().apply(this);
        }
        if (node.getRparen() != null) {
            node.getRparen().apply(this);
        }
        if (node.getThen() != null) {
            node.getThen().apply(this);
        }
        if (node.getIflcurly() != null) {
            node.getIflcurly().apply(this);
        }
        if (node.getIfBlockStmts() != null) {
            node.getIfBlockStmts().apply(this);
        }
        if (node.getIfrcurly() != null) {
            node.getIfrcurly().apply(this);
        }
        if (node.getElse() != null) {
            node.getElse().apply(this);
        }
        if (node.getElselcurly() != null) {
            node.getElselcurly().apply(this);
        }
        if (node.getElseBlockStmts() != null) {
            node.getElseBlockStmts().apply(this);
        }
        if (node.getElsercurly() != null) {
            node.getElsercurly().apply(this);
        }
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node) {
        if (node.getWhile() != null) {
            node.getWhile().apply(this);
        }
        if (node.getLparen() != null) {
            node.getLparen().apply(this);
        }
        if (node.getBoolid() != null) {
            node.getBoolid().apply(this);
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
    public void caseAIncrStmt(AIncrStmt node) {
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
    public void caseADecrStmt(ADecrStmt node) {
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
            node.getExprOrBool().apply(this);
        }
        if (node.getSemicolon() != null) {
            node.getSemicolon().apply(this);
        }
    }

    @Override
    public void caseAAssignBooleanStmt(AAssignBooleanStmt node) {
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
    public void caseAEpsilonCaseHelper(AEpsilonCaseHelper node) {
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
    public void caseAEpsilonBreakHelper(AEpsilonBreakHelper node) {
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
    public void caseAEpsilonMethodChainingOption(AEpsilonMethodChainingOption node) {
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
    public void caseAEpsilonForOptionalType(AEpsilonForOptionalType node) {
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
    public void caseAEpsilonMoreIds(AEpsilonMoreIds node) {
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
    public void caseAEpsilonVarlist(AEpsilonVarlist node) {
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
    public void caseAEpsilonArrayOption(AEpsilonArrayOption node) {
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
    public void caseAEpsilonMoreVarlist(AEpsilonMoreVarlist node) {
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
    public void caseAEpsilonVarListTwo(AEpsilonVarListTwo node) {
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
    public void caseAEpsilonMoreVarListTwo(AEpsilonMoreVarListTwo node) {
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
}