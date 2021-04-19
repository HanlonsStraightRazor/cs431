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

    //if it reaches a digit, print it off
    public void caseTDigit(TDigit node){
        System.out.println("\tGot myself a digit: <"+node.getText()+">");
    }

    //if it reaches a ALotnumbers, print off the digit stored inside of it
    public void caseALotnumbers(ALotnumbers node){
        System.out.println("\tPrinting the first number: "+node.getDigit());
    }
}
