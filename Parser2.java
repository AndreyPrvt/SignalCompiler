import java.util.ArrayList;

/**
 * Created by Andrii on 13.05.2015.
 */
public class Parser2 {
    TokenTable tokenTable;
    static Token TS;
    ArrayList<Node> ChildrenList = new ArrayList<Node>();
    ArrayList<Node> ChildrenStatemntList = new ArrayList<Node>();
    ArrayList<Node> ChildrenConpartStmtLst;// = new ArrayList<Node>();
    Parser2(TokenTable tokenTable){
        this.tokenTable = tokenTable;
    }

    private Boolean CheckCurentSymbol(String val){
        if (this.tokenTable.tokenList.isEmpty()) {
            Errors.Errors.add(new Err("ERROR", val + "expected got EOF"));
            return false;
        }
        TS = this.tokenTable.tokenList.get(0);
        if (!TS.getAtribut().equals(val)){
            Errors.Errors.add(new Err("ERROR", val + " expected", TS.row));
            return false;
        }
        this.tokenTable.tokenList.remove(0);
        if (!this.tokenTable.tokenList.isEmpty())
            TS = this.tokenTable.tokenList.get(0);
        return true;
    }

    private Node Identifier(){

        if (this.tokenTable.tokenList.isEmpty()) {
            Errors.Errors.add(new Err("ERROR", "expected identifier"));
            return null;
        }

        TS = tokenTable.tokenList.get(0);

        if (TS.elemCode < AllovedElements.IdentifierStartIndex){
            Errors.Errors.add(new Err("ERROR", TS.getAtribut() + " not identifier", TS.row));
            return null;
        }

        tokenTable.tokenList.remove(0);

        return new Node(null, "<Identifier>", TS.getAtribut());
    }

    private Node ProcedureIdentifier(){
      ArrayList<Node> identifier =new ArrayList<Node>();
        Node procidentchild = Identifier();

        if (procidentchild == null)
            return null;

        identifier.add(procidentchild);
        return new Node(identifier, "<ProcedureIdentifier>", null);
    }

    public Node Program(){
        if(!CheckCurentSymbol("PROGRAM"))
            return null;

        Node procedureIdentifier = ProcedureIdentifier();

        if(!CheckCurentSymbol(";"))
            return null;

        Node declarations = Declarations();
        Node block = Block();

        ArrayList<Node> rootnode = new ArrayList<Node>();

        rootnode.add(procedureIdentifier);
        rootnode.add(declarations);
        rootnode.add(block);

        if (!CheckCurentSymbol("."))
            return null;

        return new Node(rootnode, "<Program>", "PROGRAM");
    }

    private Node Declarations(){
        ArrayList<Node> labelDeclarations = new ArrayList<Node>();
        labelDeclarations.add(LabelDeclarations());

        return new Node(labelDeclarations, "<Declarations>", null);
    }

    private Node LabelDeclarations(){
        if (!(TS = tokenTable.tokenList.get(0)).getAtribut().equals("LABEL"))
            return new Node(null, "<LabelDeclaration>", null);
        if (!CheckCurentSymbol("LABEL"))
            return null;
        Node integer = Integer();
        if (integer.equals(null))
            return null;
        ArrayList<Node> labelDeclar = new ArrayList<Node>();
        labelDeclar.add(integer);
        Node labelList = LabelList();
        labelDeclar.add(labelList);
        if (!CheckCurentSymbol(";"))
            return null;
        return new Node(labelDeclar, "<LabelDeclaration>", "LABEL");
    }



    private Node LabelList(){

        TS = this.tokenTable.tokenList.get(0);

        if (TS.getAtribut().equals(";"))
            return null;

        if (!TS.getAtribut().equals(",")){
            Errors.Errors.add(new Err("ERROR", ", expected", TS.row));
            return null;
        }

        this.tokenTable.tokenList.remove(0);

        Node nextInteger = Integer();

        if (nextInteger != null) {
            ChildrenList.add(nextInteger);
            LabelList();
        }
        return new Node(ChildrenList, "<LabelList>", null);
    }


    private Node Integer(){

        if (this.tokenTable.tokenList.isEmpty()) {
            Errors.Errors.add(new Err("ERROR", "expected integer"));
            return null;
        }

        TS = tokenTable.tokenList.get(0);

        if (TS.elemCode < AllovedElements.ConstantStartIndex || TS.elemCode > AllovedElements.CostantEndIndex){
            Errors.Errors.add(new Err("ERROR", TS.getAtribut() + " not integer", TS.row));
            return null;
        }

        tokenTable.tokenList.remove(0);

        return new Node(null, "<Integer>", TS.getAtribut());
    }


    private Node Label(

    ){

        if (this.tokenTable.tokenList.isEmpty()) {
            Errors.Errors.add(new Err("ERROR", "expected label"));
            return null;
        }

        TS = tokenTable.tokenList.get(0);

        if (TS.elemCode < AllovedElements.ConstantStartIndex || TS.elemCode > AllovedElements.CostantEndIndex){
            Errors.Errors.add(new Err("ERROR", TS.getAtribut() + " not label", TS.row));
            return null;
        }

        tokenTable.tokenList.remove(0);

        //System.out.println(TS.getAtribut());

        return new Node(null, "<Label>", TS.getAtribut());
    }


    private Node Block(){
        if (!CheckCurentSymbol("BEGIN"))
            return null;

        Node stmtlist = StatementList(ChildrenStatemntList);
            if(stmtlist == null)
                return new Node(null, "<Block>", "BEGIN, END");
        ArrayList<Node> blockChild = new ArrayList<Node>();
        blockChild.add(stmtlist);
        if (!CheckCurentSymbol("END"))
            return null;
        return new Node(blockChild, "<Block>", "BEGIN, END");
    }

    private Node StatementList(ArrayList<Node> childrenStatemntList){
        if (this.tokenTable.tokenList.isEmpty())
            return null;

        TS = tokenTable.tokenList.get(0);

        if (TS.getAtribut().equals("ELSE") || TS.getAtribut().equals("ENDIF") ||TS.getAtribut().equals("END"))
            return null;

        Node label = null;

        if (TS.elemCode > AllovedElements.ConstantStartIndex && TS.elemCode < AllovedElements.CostantEndIndex){
            label = Label();

            if (!CheckCurentSymbol(":"))
                return null;
        }

        Node nextstmt = Statement();

       //ArrayList<Node> childrenStatemntList = new ArrayList<Node>();
        if (nextstmt != null){
            if (label != null)
                childrenStatemntList.add(label);
            childrenStatemntList.add(nextstmt);
            StatementList(childrenStatemntList);
        }

        return new Node(childrenStatemntList, "<StatementLst>", null);
    }

    private Node Statement(){


        TS = tokenTable.tokenList.get(0);
        if (TS.getAtribut().equals("GOTO")){
            tokenTable.tokenList.remove(0);

            Node label = new Node(Label());
            if (label == null)
                return null;
            ArrayList<Node> statementChildren = new ArrayList<Node>();
            statementChildren.add(label);
            if (!CheckCurentSymbol(";"))
                return null;

            return new Node(statementChildren, "<Statement>", "GotoStatement");
        }

        Node condstmt = conditionalStmt();

        if (condstmt != null) {
            if (!CheckCurentSymbol("ENDIF"))
                return null;
            if (!CheckCurentSymbol(";"))
                return null;
            ArrayList<Node> statementChildren1 = new ArrayList<Node>();
            statementChildren1.add(condstmt);
            return new Node(statementChildren1, "<Statement>", "ConditionalStatement");
        }

        if (!CheckCurentSymbol(";")){
            return null;
        }


        return new Node(null, "<Statement>", "<Empty>");
    }

    private Node conditionalStmt(){

        //ChildrenStatemntList.clear();



        Node incomplete = IcompltCondStmt();

        if (incomplete == null)
            return null;


        ArrayList<Node> conditionalChildren = new ArrayList<Node>();
        conditionalChildren.add(incomplete);

        Node alternative = AlternativePart();

        if (alternative == null)
            return new Node(conditionalChildren, "<ConditionalStatement>", null);

        conditionalChildren.add(alternative);

        return new Node(conditionalChildren, "<ConditionalStatement>", null);
    }

    private Node AlternativePart(){

        TS = this.tokenTable.tokenList.get(0);

        if (!TS.getAtribut().equals("ELSE"))
            return null;

        if(!CheckCurentSymbol("ELSE"))
                return null;

        ChildrenConpartStmtLst = new ArrayList<Node>();
        Node stmtList = StatementList(ChildrenConpartStmtLst);
        //ChildrenConpartStmtLst.clear();

        if (stmtList == null) {
            Errors.Errors.add(new Err("ERROR", "Statement expected", TS.row));
            return null;
        }

        ArrayList<Node> alternativePartChildren = new ArrayList<Node>();
        alternativePartChildren.add(stmtList);

        return new Node(alternativePartChildren, "<AlternativePart>", "ELSE");
    }

    private Node IcompltCondStmt(){
        TS = this.tokenTable.tokenList.get(0);

        if (!TS.getAtribut().equals("IF")){
            return null;
        }

        if (!CheckCurentSymbol("IF"))
            return null;

        Node condexpr = CondExpr();

        if (condexpr == null)
            return null;

        if (!CheckCurentSymbol("THEN"))
            return null;

        ChildrenConpartStmtLst = new ArrayList<Node>();
        Node stmtList = StatementList(ChildrenConpartStmtLst);
        //ChildrenConpartStmtLst.clear();

        if (stmtList == null){
            Errors.Errors.add(new Err("ERROR", "Statement expected", TS.row));
            return null;
        }


        ArrayList<Node> incompPartChildren = new ArrayList<Node>();
        incompPartChildren.add(condexpr);
        incompPartChildren.add(stmtList);

        return new Node(incompPartChildren, "<IncompletePart>", "IF THEN");
    }

    private Node CondExpr(){
        Node varIdent = VarIdent();

        if(varIdent.equals(null))
            return null;

        if (!CheckCurentSymbol("="))
            return null;

        Node integer = Integer();
        if (integer.equals(null))
            return null;

        ArrayList<Node> condExpChildren = new ArrayList<Node>();
        condExpChildren.add(varIdent);
        condExpChildren.add(integer);
        return new Node(condExpChildren, "<ConditionalExpression>", null);
    }

    private Node VarIdent(){

        ArrayList<Node> identifier =new ArrayList<Node>();
        Node varIdent = Identifier();

        if (varIdent == null)
            return null;

        identifier.add(varIdent);
        return new Node(identifier, "<VariableIdentifier>", null);
    }
}

