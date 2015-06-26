import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;

/**
 * Created by Andrii on 02.04.2015.
 */
public class AllovedElements {
    public static List<Character> whitespase;
    public static List<Character> delimeters;
    public static List<Character> letters;
    public static List<Character> digits;
    public static List<String> keywords;
    public static char StartComment = '(';
    public static char EndComment = ')';
    public static final int IdentifierStartIndex = 1001;
    public static final int ConstantStartIndex = 501;
    public static final int CostantEndIndex = 1000;



    AllovedElements(){

        whitespase = new ArrayList<Character>();
        whitespase = asList((char) 9, (char) 10, (char) 11, (char) 12, (char) 13, ' ');


        delimeters = new ArrayList<Character>();
        delimeters = asList(':', ';', ',', '=', '.');


        keywords = new ArrayList<String>();
        keywords = asList("BEGIN", "END", "PROGRAM", "IF", "THEN", "ELSE", "ENDIF", "LABEL", "GOTO");

        letters = GenEnum((int) 'A', (int) 'Z');
        digits = GenEnum((int) '0', (int) '9');

    }

    private List<Character> GenEnum(int start, int end){
        List<Character> Enum = new ArrayList<Character>();
        for (int i = start; i < end + 1; i++) {
            Enum.add((char) i);
        }
        return Enum;
    }

    public String GetType(char cur){
        if (delimeters.contains(cur))
            return "dm";
        if (whitespase.contains(cur))
            return "ws";
        if (letters.contains(cur))
            return "lit";
        if (digits.contains(cur))
            return "dig";
        if (StartComment == cur)
            return "scom";
        if (EndComment == cur)
            return "ecom";
        if (cur == '*')
            return "com";
        return "error";
    }

    public boolean CheckKeyWord(String PossibleKeyWord){
        if (keywords.contains(PossibleKeyWord)){
            return true;
        }
        return false;
    }

}
