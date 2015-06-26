import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii on 22.05.2015.
 */
public class CodeGenerator {
    public static ArrayList<String> listing = new ArrayList<String>();
    private ArrayList<String> reservedLabels = new ArrayList<String>();
    private ArrayList<String> reservedIdentifiers = new ArrayList<String>();
    ArrayList<String> usedLabels = new ArrayList<String>();
    private Node syntaxTree;
    private TokenTable tokenTable;
    private int cond = 0;


    CodeGenerator(Node syntaxTree, TokenTable tokenTable){
        this.syntaxTree = syntaxTree;
        this.tokenTable = tokenTable;
    }

    public void Program(){ // node program has 3 children (0 - procedure identifier; 1 - declarations; 2 - block)
        listing.add(".386");
        listing.add("DATA SEGMENT USE16");
        for (Token tk: this.tokenTable.tokenList) {
            if ((tk.elemCode > AllovedElements.IdentifierStartIndex + 1) && !reservedIdentifiers.contains(tk.getAtribut())){
                reservedIdentifiers.add(tk.getAtribut());
                listing.add("\t" + tk.getAtribut() + " dd 0");
            }
        }
        listing.add("DATA ENDS");
        ProcedureIdentifier(syntaxTree.children.get(0));
        Declarations(syntaxTree.children.get(1));
        Block(syntaxTree.children.get(2));


    }

    private void ProcedureIdentifier(Node procedIdent){
        Node ident = procedIdent.children.get(0);
        reservedIdentifiers.add(ident.atribut);
    }

    private void Declarations(Node declarations){
        LabelDeclaration(declarations.children.get(0));

    }

    private void LabelDeclaration(Node labelDeclarations){
        reservedLabels.add(labelDeclarations.children.get(0).atribut);
        LabelList(labelDeclarations.children.get(1));
    }



    private void LabelList(Node labelList){
        for (Node child: labelList.children){
            reservedLabels.add(child.atribut);
        }
    }

    private void Block(Node Block) {
        listing.add("CODE SEGMENT USE16");
        listing.add("ASSUME CS:CODE, DS:DATA");
        listing.add("BEGIN:");

        StatementList(Block.children.get(0));

        listing.add("CODE ENDS");
        listing.add("END BEGIN");
    }

    private void StatementList(Node stmntList){

        listing.add("; new stmt list");
        for(Node child: stmntList.children){
            if (child.type.equals("<Label>"))
                Label(child);
        }
        for(Node child: stmntList.children) {
            if (child.type.equals("<Label>"))
                listing.add(LabelIdentifier(child) + ":");
            if (child.type.equals("<Statement>"))
                Statement(child);
        }
    }

    private void Label(Node label){
        if (!reservedLabels.contains(label.atribut)){
            Token tk = tokenTable.GetToken(label.atribut);
            Errors.Errors.add(new Err("Error", "unknown label", tk.row));
            Errors.print();
            System.exit(0);
        }

        if (usedLabels.contains(label.atribut)){
            Token tk = tokenTable.GetToken(label.atribut);
            Errors.Errors.add(new Err("ERROR","label is used",tk.row));
            Errors.print();
            System.exit(0);
        }

        System.out.println(label.atribut);
        usedLabels.add(label.atribut);
    }


    private void Statement(Node statement){
        if (statement.atribut.equals("GotoStatement")){
            listing.add("\t" + "jmp " + LabelIdentifier(statement.children.get(0)));
        }
        if (statement.atribut.equals("ConditionalStatement")){
            ConditionStatement(statement.children.get(0));
        }
    }

    private String LabelIdentifier(Node label){
        if (usedLabels.contains(label.atribut))
            return label.atribut;
        Token tk = tokenTable.GetToken(label.atribut);
        Errors.Errors.add(new Err("Error", "such label not found", tk.row));
        Errors.print();
        System.exit(0);
        return null;
    }

    private void ConditionStatement(Node conditionStatement){
            IncompletePart(conditionStatement.children.get(0));
        if (conditionStatement.children.size() == 2)
            AlternativePart(conditionStatement.children.get(1));
    }

    private void IncompletePart(Node incomplitePart){
        ConditionalExpression(incomplitePart.children.get(0));
        cond++;
        listing.add("jne condleb" + cond);
        StatementList(incomplitePart.children.get(1));
    }

    private void ConditionalExpression(Node conditionalExpression){
        VariableIdentifier(conditionalExpression.children.get(0));
        Integer(conditionalExpression.children.get(1));
        listing.add("CMP AX, BX");

    }

    private void VariableIdentifier(Node variableIdentifier){
        Identifier(variableIdentifier.children.get(0));
    }

    private void Identifier(Node identifier){
        listing.add("MOV AX, " + identifier.atribut);
    }
    private void Integer(Node integer){
        listing.add("MOV BX, " + integer.atribut);
    }
    private void AlternativePart(Node alternativePart){
        listing.add("condleb" + cond + ":");
        StatementList(alternativePart.children.get(0));
    }
}
