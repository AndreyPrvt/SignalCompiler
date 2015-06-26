import java.util.*;

/**
 * Created by Andrii on 05.04.2015.
 */
public class TokenTable {
   public static List<Token> tokenList = new ArrayList<Token>() {};
   public static Map<Integer, String>  usedSymbols = new HashMap<Integer, String>();
   public int GenTokenCode (String cur, int elemCode){
       if (usedSymbols.values().contains(cur))
            return getCode(cur);
       return ++elemCode;
   }
   private int getCode(String cur){
       for(Token tk: tokenList){
           if (tk.getAtribut().equals(cur))
               return  tk.elemCode;
       }
       return 0;
   }

   public Token GetToken(String val){
       for (Token tk: this.tokenList ){
           if (tk.getAtribut().equals(val))
               return tk;
       }
       return null;
   }

}

abstract class Token{
    int row, coloumn, elemnum, elemCode;
    String type;
    public abstract void print();
    public abstract String getAtribut();
}

class SingleCharTocken extends Token{
    Character aChar;
    SingleCharTocken(int row, int coloumn, int elemCode, String type, char aChar){
        this.row = row;
        this.coloumn = coloumn;
        this.elemCode = elemCode;
        this.type = type;
        this.aChar = aChar;
    }

    @Override
    public void print() {
        System.out.println(aChar + " Type: " + type + " code: " + elemCode + " " + this.elemnum);
    }

    @Override
    public String getAtribut() {
        return aChar.toString();
    }
}

class MultiCharTocken extends Token{
    String string;
    MultiCharTocken(int row, int coloumn, int elemCode, String type, String string){
        this.row = row;
        this.coloumn = coloumn;
        this.elemCode = elemCode;
        this.type = type;
        this.string = string;
    }

    @Override
    public void print() {
        if (string.equals("ERROR"))
            System.out.println(string + " Type " + type + " Line " + row);
        else
            System.out.println(string + " Type: " + type +" code: " + elemCode + "   " + this.elemnum);
    }

    @Override
    public String getAtribut() {
        return string;
    }

}

class Errors{
    public static ArrayList<Err> Errors = new ArrayList<Err>();

    public static void CheckErrors(){
        if (!Errors.isEmpty()){
            System.exit(0);
        }
    }

    public static void print(){
        for (Err err: Errors)
            System.out.println(err.symbol + "  " + err.type + " at " + err.row);
    }

}

class Err{
    String symbol;
    String type;
    int row;
    int coloumn;

    Err(String symbol, String type){
        this.symbol = symbol;
        this.type = type;
    }

    Err(String symbol, String type, int row){
        this.symbol = symbol;
        this.type = type;
        this.row = row;
        //this.coloumn = coloumn;

    }

    Err(String symbol, String type, int row, int coloumn){
        this.symbol = symbol;
        this.type = type;
        this.row = row;
        this.coloumn = coloumn;

    }

}