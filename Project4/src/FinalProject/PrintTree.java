package FinalProject;
import FinalProject.analysis.*;
import FinalProject.node.*;
import java.util.*;

/*
* Project 4
* Professor: Erik Krohn
* Authors: Ben Pink, Martin Mueller, Isaiah Ley
*/
class PrintTree extends DepthFirstAdapter
{
    public static HashMap<String, Variable> symbolTable = new HashMap<>();

    public PrintTree() {
        System.out.println("Start of the Printing Action");
    }
    /*
    //this gets called if the production is prog --> id digit
    public void caseAFirstProg(AFirstProg node){
        System.out.println("\tGot a first prog!");
    }

    //prog --> lotnumbers
    public void caseASecondProg(ASecondProg node){
        System.out.println("\tGot a second prog!");
        node.getLotnumbers().apply(this);
    }

    //prog --> id id digit digit
    public void caseAThirdProg(AThirdProg node){
        System.out.println("\tGot a third prog!");
        node.getEachsymbolisuniqueinaproduction().apply(this);
        node.getSecondid().apply(this);
        node.getDigitone().apply(this);
        node.getDigittwo().apply(this);
    }

    //if it reaches an id, print it off
    public void caseTId(TId node){
        System.out.println("\tGot myself an id: <"+node.getText()+">");
    }
    */

    public void caseAAddExpr(AAddExpr node) {
    }
    public void caseAAnotherCaseCaseHelper(AAnotherCaseCaseHelper node) {
    }
    public void caseAArrayArrayOption(AArrayArrayOption node) {
    }
    public void caseAArrayArrayOrId(AArrayArrayOrId node) {
    }
    public void caseAArrayFactor(AArrayFactor node) {
    }
    public void caseAAssignBooleanMethodstmtseq(AAssignBooleanMethodstmtseq node) {
    }
    public void caseAAssignBooleanStmt(AAssignBooleanStmt node) {
    }
    public void caseAAssignDecMethodstmtseq(AAssignDecMethodstmtseq node) {
    }
    public void caseAAssignEqualsMethodstmtseq(AAssignEqualsMethodstmtseq node) {
    }
    public void caseAAssignExprStmt(AAssignExprStmt node) {
    }
    public void caseAAssignIncMethodstmtseq(AAssignIncMethodstmtseq node) {
    }
    public void caseAAssignmentForIncrStep(AAssignmentForIncrStep node) {
    }
    public void caseAAssignReadInMethodstmtseq(AAssignReadInMethodstmtseq node) {
    }
    public void caseAAssignStringMethodstmtseq(AAssignStringMethodstmtseq node) {
    }
    public void caseAAssignStringStmt(AAssignStringStmt node) {
    }
    public void caseABoolBoolid(ABoolBoolid node) {
    }
    public void caseABoolExprOrBool(ABoolExprOrBool node) {
    }
    public void caseABreakBreakHelper(ABreakBreakHelper node) {
    }
    public void caseAClassDefClassmethodstmt(AClassDefClassmethodstmt node) {
    }
    public void caseAClassStmtsClassmethodstmts(AClassStmtsClassmethodstmts node) {
    }
    public void caseAConditionalBoolean(AConditionalBoolean node) {
    }
    public void caseADeclObjectMethodstmtseq(ADeclObjectMethodstmtseq node) {
    }
    public void caseADeclObjectStmt(ADeclObjectStmt node) {
    }
    public void caseADecrForIncrStep(ADecrForIncrStep node) {
    }
    public void caseADecrStmt(ADecrStmt node) {
    }
    public void caseAEpsilonArrayOption(AEpsilonArrayOption node) {
    }
    public void caseAEpsilonBreakHelper(AEpsilonBreakHelper node) {
    }
    public void caseAEpsilonCaseHelper(AEpsilonCaseHelper node) {
    }
    public void caseAEpsilonClassmethodstmts(AEpsilonClassmethodstmts node) {
    }
    public void caseAEpsilonForOptionalType(AEpsilonForOptionalType node) {
    }
    public void caseAEpsilonMethodChainingOption(AEpsilonMethodChainingOption node) {
    }
    public void caseAEpsilonMethodstmtseqs(AEpsilonMethodstmtseqs node) {
    }
    public void caseAEpsilonMoreIds(AEpsilonMoreIds node) {
    }
    public void caseAEpsilonMoreVarlist(AEpsilonMoreVarlist node) {
    }
    public void caseAEpsilonMoreVarListTwo(AEpsilonMoreVarListTwo node) {
    }
    public void caseAEpsilonStmtseq(AEpsilonStmtseq node) {
    }
    public void caseAEpsilonVarlist(AEpsilonVarlist node) {
    }
    public void caseAEpsilonVarListTwo(AEpsilonVarListTwo node) {
    }
    public void caseAExprFactor(AExprFactor node) {
    }
    public void caseAExprOrBool(AExprOrBool node) {
    }
    public void caseAFactorTerm(AFactorTerm node) {
    }
    public void caseAFalseBoolean(AFalseBoolean node) {
    }
    public void caseAFirstStmtStmtseq(AFirstStmtStmtseq node) {
    }
    public void caseAForOptionalType(AForOptionalType node) {
    }
    public void caseAForStmt(AForStmt node) {
    }
    public void caseAGetStmt(AGetStmt node) {
    }
    public void caseAIdArrayOrId(AIdArrayOrId node) {
    }
    public void caseAIdBoolid(AIdBoolid node) {
    }
    public void caseAIdType(AIdType node) {
    }
    public void caseAIdvarlistFactor(AIdvarlistFactor node) {
    }
    public void caseAIfBlockStmt(AIfBlockStmt node) {
    }
    public void caseAIfElseBlockStmt(AIfElseBlockStmt node) {
    }
    public void caseAIncrForIncrStep(AIncrForIncrStep node) {
    }
    public void caseAIncrStmt(AIncrStmt node) {
    }
    public void caseAIntFactor(AIntFactor node) {
    }
    public void caseALastFactor(ALastFactor node) {
    }
    public void caseAMethodCallInClassStmt(AMethodCallInClassStmt node) {
    }
    public void caseAMethodCallMethodChainingOption(AMethodCallMethodChainingOption node) {
    }
    public void caseAMethodCallStmt(AMethodCallStmt node) {
    }
    public void caseAMethodDeclClassmethodstmt(AMethodDeclClassmethodstmt node) {
    }
    public void caseAMethodDeclMethodstmtseq(AMethodDeclMethodstmtseq node) {
    }
    public void caseAMethodStmtsMethodstmtseqs(AMethodStmtsMethodstmtseqs node) {
    }
    public void caseAMinusAddop(AMinusAddop node) {
    }
    public void caseAMoreIdsMoreIds(AMoreIdsMoreIds node) {
    }
    public void caseAMoreIdsMoreVarlist(AMoreIdsMoreVarlist node) {
    }
    public void caseAMoreIdsVarlist(AMoreIdsVarlist node) {
    }
    public void caseAMoreVarListTwo(AMoreVarListTwo node) {
    }
    public void caseAMultTerm(AMultTerm node) {
    }
    public void caseANegativeFactor(ANegativeFactor node) {
    }
    public void caseAPlusAddop(APlusAddop node) {
    }
    public void caseAPrintStmtMethodstmtseq(APrintStmtMethodstmtseq node) {
    }
    public void caseAProg(AProg node) {
    }
    public void caseAPutStmt(APutStmt node) {
    }
    public void caseARealFactor(ARealFactor node) {
    }
    public void caseAReturnStmt(AReturnStmt node) {
    }
    public void caseASwitchStmt(ASwitchStmt node) {
    }
    public void caseATermExpr(ATermExpr node) {
    }
    public void caseATrueBoolean(ATrueBoolean node) {
    }
    public void caseATypesType(ATypesType node) {
    }
    public void caseAVarDeclClassmethodstmt(AVarDeclClassmethodstmt node) {
    }
    public void caseAVarDeclMethodstmtseq(AVarDeclMethodstmtseq node) {
    }
    public void caseAVarDeclStmt(AVarDeclStmt node) {
    }
    public void caseAVarListVarListTwo(AVarListVarListTwo node) {
    }
    public void caseAWhileStmt(AWhileStmt node) {
    }

    /*
    //if it reaches a digit, print it off
    public void caseTDigit(TDigit node){
        System.out.println("\tGot myself a digit: <"+node.getText()+">");
    }
    */

}
