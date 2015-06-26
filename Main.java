import java.io.IOException;

/**
 * Created by Andrii on 19.02.2015.
 */

public class Main {

    public static void main(String[] args) throws Exception{
        LA la = new LA();

        TokenTable tb = la.Analising();


        TokenTable tb_copy =la.Analising();

        //for (Token tk: tb.tokenList){
        //    tk.print();
        //}

        Parser2 parser = new Parser2(tb);
        Node tree = parser.Program();
        Errors.print();
        //Errors.CheckErrors();
        tree.PrintTree();
        CodeGenerator codeGenerator = new CodeGenerator(tree, tb_copy);
        codeGenerator.Program();

        for (String str: codeGenerator.listing){
            System.out.println(str);
        }
    }
}
